package me.hellrevenger.jar2dts.visitor;

import me.hellrevenger.jar2dts.converter.TypeScriptData;
import me.hellrevenger.jar2dts.typescriptDeclarations.Function;
import me.hellrevenger.jar2dts.typescriptDeclarations.Interface;
import me.hellrevenger.jar2dts.utils.ClassName;
import me.hellrevenger.jar2dts.utils.Lists;
import org.objectweb.asm.*;

import java.util.Arrays;
import java.util.stream.Collectors;

public class JavaClassVisitor extends ClassVisitor {
    String lastAccessClassName;
    String lastAccessClassNameSimple;
    String lastAccessPackage;

    boolean unnecessary = false;

    boolean isRecord = false;

    public JavaClassVisitor() {
        this(null);
    }
    public JavaClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM9, classVisitor);
    }
    /*
    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        System.out.printf("visitOuterClass: %s %s\n", name, descriptor);
        super.visitOuterClass(owner, name, descriptor);
    }

     */

    // can ignore
    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        //System.out.printf("visitTypeAnnotation: %s %s\n", lastClassName, descriptor);
        return super.visitAnnotation(descriptor, visible);
    }

    // useless
    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        //System.out.printf("visitTypeAnnotation: %s %s\n", typePath, descriptor);
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        //System.out.printf("visitField: %s %s %s\n", name, descriptor, signature);
        if(name.startsWith("$") || unnecessary || isRecord) {
            return super.visitField(access, name, descriptor, signature, value);
        }
        String location = TypeScriptData.INSTANCE.namespacePrefix + lastAccessPackage; // "Packages." +
        var clazz = TypeScriptData.INSTANCE.getNamespace(location).getInterface(lastAccessClassNameSimple);
        var originalName = name;
        var fieldName = TypeScriptData.INSTANCE.mapping.map(lastAccessClassName, name);
        if(fieldName.length() == 1) {
            var f2 = TypeScriptData.INSTANCE.mapping2.map(lastAccessClassName, name);
            if(f2.length() != 1) {
                fieldName = f2;
            }
        }
        if (fieldName.equals("constructor")){
            fieldName = "_" + fieldName;
        }
        var self = clazz.getVariable(fieldName);
        self.name = fieldName;

        self.isStatic = (access & Opcodes.ACC_STATIC) != 0;
        self.isReadonly = (access & Opcodes.ACC_FINAL) != 0;
        self.isPrivate = (access & Opcodes.ACC_PRIVATE) != 0;
        self.isProtected = (access & Opcodes.ACC_PROTECTED) != 0;

        if(!originalName.equals(fieldName)) {
            self.renamedFrom = originalName;
        }

        /*
        if((signature != null && signature.contains("-")) || descriptor.contains("-")) {
            System.out.printf("field: %s, sign: %s, descriptor: %s\n", fieldName, signature, descriptor);
        }

         */

        String sign = signature == null ? descriptor : signature;

        MySignatureReader mySignatureReader = new MySignatureReader(sign);
        mySignatureReader.accept(new FieldSignatureVisitor(self));

        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        //System.out.printf("visitMethod: %s %s %s, synthetic: %s\n", name, descriptor, signature, (access & Opcodes.ACC_SYNTHETIC) != 0);
        String sign = signature == null ? descriptor : signature;
        if(name.equals("<clinit>") || unnecessary){
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
        if(name.equals("<init>")) {
            visitConstructor(access, name, descriptor, signature, exceptions);
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }


        String location = TypeScriptData.INSTANCE.namespacePrefix + lastAccessPackage; // "Packages." +
        var clazz = TypeScriptData.INSTANCE.getNamespace(location).getInterface(lastAccessClassNameSimple);

        var originalName = name;
        var methodName = TypeScriptData.INSTANCE.mapping.map(lastAccessClassName, name);

        if(methodName.length() == 1) {
            var f2 = TypeScriptData.INSTANCE.mapping2.map(lastAccessClassName, name);
            if(f2.length() != 1) {
                methodName = f2;
            }
        }

        if (methodName.equals("constructor")){
            methodName = "_" + methodName;
        }

        var selves = clazz.getFunction(methodName);
        var self = new Function();
        selves.add(self);

        self.name = methodName;
        self.isStatic = (access & Opcodes.ACC_STATIC) != 0;
        self.isPrivate = (access & Opcodes.ACC_PRIVATE) != 0;
        self.isProtected = (access & Opcodes.ACC_PROTECTED) != 0;

        if(!originalName.equals(methodName)) {
            self.renamedFrom = originalName;
        }

        MySignatureReader mySignatureReader = new MySignatureReader(sign);
        mySignatureReader.accept(new MethodSignatureVisitor(self));

        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    void visitConstructor(int access, String name, String descriptor, String signature, String[] exceptions) {
        if((access & (Opcodes.ACC_SYNTHETIC | Opcodes.ACC_PRIVATE)) != 0) return;
        //System.out.printf("visitMethod from %s: %s %s %s\n", lastAccessClassName,name, descriptor, signature);
        String sign = signature == null ? descriptor : signature;
        String location = TypeScriptData.INSTANCE.namespacePrefix + lastAccessPackage; //"Packages." +
        var clazz = TypeScriptData.INSTANCE.getNamespace(location).getInterface(lastAccessClassNameSimple);

        var methodName = "new";
        /*
        if ((access & Opcodes.ACC_PRIVATE) != 0){
            methodName = "_" + methodName;
        }

         */
        var selves = clazz.getFunction(methodName);
        Function self;

        if(!clazz.hasConstructor || true) {
            //clazz.interfaces.add(lastAccessClassNameSimple + "$$constructor");
            clazz.constructor = new Function();
            clazz.constructor.name = methodName;
            self = clazz.constructor;;
            selves.add(self);
        } else {
            self = selves.get(0);
        }
        clazz.hasConstructor = true;

        self.name = methodName;
        //self.isStatic = true;
        self.isPrivate = (access & Opcodes.ACC_PRIVATE) != 0;
        self.isProtected = (access & Opcodes.ACC_PROTECTED) != 0;
        self.isConstructor = true;

        MySignatureReader mySignatureReader = new MySignatureReader(sign);
        mySignatureReader.accept(new MethodSignatureVisitor(self));
        self.returnType = lastAccessClassNameSimple;

    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        //System.out.printf("visitClass: %s sign: %s super:%s interfaces:[%s]\n", name, signature, superName, Lists.from(interfaces).stream().collect(Collectors.joining(",")));

        isRecord = (Opcodes.ACC_RECORD & access) != 0;
        lastAccessClassName = ClassName.remapForNamespace(name);
        lastAccessClassNameSimple = lastAccessClassName.substring(lastAccessClassName.contains(".") ? lastAccessClassName.lastIndexOf(".")+1 : 0);
        lastAccessPackage = lastAccessClassName.substring(0,lastAccessClassName.contains(".") ? lastAccessClassName.lastIndexOf(".") : 0);

        if(lastAccessClassNameSimple.isEmpty() || (access & (Opcodes.ACC_SYNTHETIC)) != 0
                || !lastAccessClassNameSimple.replaceAll("\\$\\d","").equals(lastAccessClassNameSimple)) {
            super.visit(version, access, name, signature, superName, interfaces);
            unnecessary = true;
            return;
        }
        String location =  TypeScriptData.INSTANCE.namespacePrefix + lastAccessPackage; //"Packages." +
        Interface interf;
        if((access & Opcodes.ACC_INTERFACE) != 0) {
            interf = TypeScriptData.INSTANCE.getNamespace(location).getInterface(lastAccessClassNameSimple);
        } else {
            interf = TypeScriptData.INSTANCE.getNamespace(location).getTypescriptClass(lastAccessClassNameSimple);
        }
        interf.fullName = lastAccessClassName;
        interf.name = lastAccessClassNameSimple;
        ClassSignatureVisitor csv = new ClassSignatureVisitor(interf);
        if(signature != null) {
            MySignatureReader mySignatureReader = new MySignatureReader(signature);
            mySignatureReader.accept(csv);
        } else {
            if(superName != null) {
                csv.visitSuperclass(superName);
            }
            if(interfaces != null) {
                Arrays.stream(interfaces).forEach(csv::visitInterface);
            }
        }

        //SignatureReader signatureReader = new SignatureReader(signature);


        super.visit(version, access, name, signature, superName, interfaces);
    }


    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        return super.visitRecordComponent(name, descriptor, signature);
    }
}
