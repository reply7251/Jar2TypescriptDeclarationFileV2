package me.hellrevenger.jar2dts.visitor;

import me.hellrevenger.jar2dts.typescriptDeclarations.Variable;
import me.hellrevenger.jar2dts.utils.ClassName;

import java.util.List;
import java.util.stream.Collectors;

public class FieldSignatureVisitor implements MySignatureVisitor {
    Variable variable;

    public FieldSignatureVisitor(Variable variable) {
        this.variable = variable;
    }

    @Override
    public MySignatureVisitor visitSuperclass(String superclass) {
        variable.type = ClassName.remap(superclass);
        return this;
    }

    @Override
    public MySignatureVisitor visitGenerics(List<String> generics) {
        variable.generics = generics.stream().map(x->x.contains(";")?x.substring(x.lastIndexOf(";")):x)
                .collect(Collectors.joining(","))
                .replace("/",".").replace("$",".");
        return this;
    }
}
