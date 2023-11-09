package me.hellrevenger.jar2dts.visitor;

import me.hellrevenger.jar2dts.typescriptDeclarations.Interface;
import me.hellrevenger.jar2dts.utils.ClassName;

import java.util.List;
import java.util.stream.Collectors;

public class ClassSignatureVisitor implements MySignatureVisitor {
    Interface interf;

    public ClassSignatureVisitor(Interface interf) {
        this.interf = interf;
    }

    @Override
    public MySignatureVisitor visitGenerics(List<String> generics) {
        interf.generics = generics.stream().map(x->x.contains(";")?x.substring(x.lastIndexOf(";")):x)
                .collect(Collectors.joining(","))
                .replace("/",".").replace("$",".");
        return this;
    }

    @Override
    public MySignatureVisitor visitSuperclass(String superclass) {
        superclass = ClassName.remap(superclass);
        if(superclass.equals("any")) {
            superclass = "java.lang.Object";
        }
        if(superclass.startsWith("java.lang.Enum")) return this;
        //interf.interfaces.add(superclass);
        interf.superClass = superclass;
        return this;
    }

    @Override
    public MySignatureVisitor visitInterface(String superclass) {
        superclass = ClassName.remap(superclass);
        if(superclass.equals("any")) {
            superclass = "java.lang.Object";
        }
        if(superclass.startsWith("java.lang.Enum")) return this;
        interf.interfaces.add(superclass);
        return this;
    }
}
