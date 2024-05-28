package me.hellrevenger.jar2dts.visitor;

import me.hellrevenger.jar2dts.typescriptDeclarations.Function;
import me.hellrevenger.jar2dts.utils.Lists;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MyMethodVisitor extends MethodVisitor {
    Function function;

    BiConsumer<Integer, String> callback;

    static final List<String> keywords = Lists.from("in", "export", "function", "var", "let", "with");

    public MyMethodVisitor(Function func, MethodVisitor methodVisitor) {
        this(func, methodVisitor, null);
    }

    public MyMethodVisitor(Function func, MethodVisitor methodVisitor, BiConsumer<Integer, String> callback) {
        super(Opcodes.ASM9, methodVisitor);
        function = func;
        this.callback = callback;
    }
    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        int index2 = index;
        if(!function.isStatic) {
            index2--;
        }
        if(index2 != -1 && index2 < function.parameters.size()) {
            var pname = name;

            if(keywords.contains(pname)) {
                pname = "_" + pname;
            }
            function.parameters.get(index2).name = pname;
            if(callback != null) {
                callback.accept(index2, pname);
            }
        }
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
    }
}
