package me.hellrevenger.jar2dts.visitor;

import me.hellrevenger.jar2dts.typescriptDeclarations.Function;
import me.hellrevenger.jar2dts.typescriptDeclarations.Variable;
import me.hellrevenger.jar2dts.utils.ClassName;

public class ConstructorSignatureVisitor extends MethodSignatureVisitor{
    public int counter = 0;
    int originalCount;

    public ConstructorSignatureVisitor(Function function) {
        super(function);
        originalCount = function.parameters.size();
    }



    @Override
    public MySignatureVisitor visitParameter(String type) {
        if(counter++ < originalCount) {
            var variable = function.parameters.get(counter-1);
            variable.type += "|" + ClassName.remap(type);
        } else {
            Variable variable = new Variable();
            variable.name = "arg" + function.parameters.size();
            function.parameters.add(variable);
            variable.type = ClassName.remap(type);
            variable.isParam = true;
        }
        return this;
    }

    @Override
    public MySignatureVisitor visitEnd() {
        function.minArgCount = function.minArgCount == -1 ? counter : Math.min(function.minArgCount, counter);
        return this;
    }
}
