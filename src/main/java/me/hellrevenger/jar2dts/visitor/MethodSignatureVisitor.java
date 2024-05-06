package me.hellrevenger.jar2dts.visitor;

import me.hellrevenger.jar2dts.typescriptDeclarations.Function;
import me.hellrevenger.jar2dts.typescriptDeclarations.Variable;
import me.hellrevenger.jar2dts.utils.ClassName;

import java.util.List;
import java.util.stream.Collectors;

public class MethodSignatureVisitor implements MySignatureVisitor{
    Function function;
    public MethodSignatureVisitor(Function function) {
        this.function = function;
    }

    @Override
    public MySignatureVisitor visitGenerics(List<String> generics) {

        function.generics = generics.stream().map(x->x.contains(";")?x.substring(x.lastIndexOf(";")):x)
                .map(x->x.replace("/","."))
                .collect(Collectors.toList());
        return MySignatureVisitor.super.visitGenerics(generics);
    }

    @Override
    public MySignatureVisitor visitReturnType(String type) {
        function.returnType = ClassName.remap(type);
        return this;
    }

    @Override
    public MySignatureVisitor visitParameter(String type) {
        Variable variable = new Variable();
        variable.name = "a" + function.parameters.size();
        function.parameters.add(variable);
        variable.type = ClassName.remap(type);
        variable.isParam = true;
        variable.isStatic = function.isStatic;
        return this;
    }

    @Override
    public String getScope() {
        return function.scope;
    }
}
