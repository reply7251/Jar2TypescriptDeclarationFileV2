package me.hellrevenger.jar2dts.typescriptDeclarations;

import me.hellrevenger.jar2dts.converter.TypeScriptData;

import java.util.ArrayList;
import java.util.HashSet;

public class ShadowInterface extends Interface{
    Interface target = null;
    String simpleName = null;
    public void setRenamedTo(Interface interf) {
        generics = interf.generics;
        name = interf.name;
        interfaces = interf.interfaces;
        target = interf;
        simpleName = target.renamedFrom.substring(target.renamedFrom.lastIndexOf(".")+1);
    }

    public void handleStatic(TypeScriptData data) {
        data.appendIndent().append("let ").append(simpleName).append(": ").append(target.scope + "._" + name).append("$$static").append(getGenerics()).append(";\n");
    }

    public void handleInstance(TypeScriptData data) {
        data.appendIndent().append("interface ").append(simpleName);
        handleGenerics(data);
        data.stringBuilder.append(" extends CombineTypes<[");

        var types = new HashSet<String>();
        //types.add("_" + name + getGenerics());
        if(!target.superClass.isEmpty()) types.add(target.superClass);
        types.addAll(target.interfaces);
        var typesWithSelf = new ArrayList<String>();
        typesWithSelf.add(target.scope + "." + target.name + getGenerics());
        typesWithSelf.addAll(types);
        data.stringBuilder.append(String.join(", ", typesWithSelf));

        data.stringBuilder.append("]> {}\n");
    }
}
