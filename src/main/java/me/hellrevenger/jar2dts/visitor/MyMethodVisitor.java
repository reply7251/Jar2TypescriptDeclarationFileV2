package me.hellrevenger.jar2dts.visitor;

import me.hellrevenger.jar2dts.typescriptDeclarations.Function;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyMethodVisitor extends MethodVisitor {
    Function function;

    public MyMethodVisitor(Function func, MethodVisitor methodVisitor) {
        super(Opcodes.ASM9, methodVisitor);
        function = func;
    }
    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        int index2 = index;
        if(!function.isStatic) {
            index2--;
        }
        if(index2 != -1 && index2 < function.parameters.size()) {
            var pname = name;
            if(pname.equals("in") || pname.equals("export")) {
                pname = "_" + pname;
            }
            function.parameters.get(index2).name = pname;
        }
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
    }
}
