package me.hellrevenger.jar2dts.typescriptDeclarations;

import me.hellrevenger.jar2dts.converter.AcceptTypeScriptData;
import me.hellrevenger.jar2dts.converter.TypeScriptData;
import me.hellrevenger.jar2dts.utils.DefaultMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Function implements AcceptTypeScriptData {
    public boolean isStatic = false;

    public boolean isPrivate = false;

    public boolean isProtected = false;

    public boolean isConstructor = false;

    public String name;

    public List<String> generics = new ArrayList<>();

    public String returnType = "";

    public int minArgCount = -1;

    public String renamedFrom = "";

    public ArrayList<Variable> parameters = new ArrayList<>();

    String cachedSign = null;

    public void accept(TypeScriptData data) {
        if(isStatic != data.accessingStatic) return;
        if(renamedFrom.isEmpty() && data.accessingOriginalName) return;
        if(isConstructor != data.accessingConstructor) return;
        if(name.contains("$")) {
            return;
        }
        if(!isStatic && name.equals("constructor")) {
            return;
        }
        if(!returnType.replaceAll("\\.1", "").equals(returnType)) {
            return;
        }
        for (Variable parameter : parameters) {
            if (!parameter.type.replaceAll("\\.1", "").equals(parameter.type))
                return;
        }
        data.appendIndent();
        if(!renamedFrom.isEmpty()) {
            if(data.accessingOriginalName) {
                data.stringBuilder.append("/** @renamed-to ").append(name).append(" */");
            } else {
                data.stringBuilder.append("/** @renamed-from ").append(renamedFrom).append(" */");
            }
        }
        if(isStatic) {
            //data.stringBuilder.append("static ");
        }
        if(isPrivate || isProtected) {
            data.stringBuilder.append("_");
        }
        if(data.accessingOriginalName) {
            data.stringBuilder.append(renamedFrom);
        } else {
            data.stringBuilder.append(name);
        }
        if(!generics.isEmpty()) {
            data.stringBuilder.append("<");
            data.stringBuilder.append(String.join(", ", generics));
            data.stringBuilder.append(">");
        }
        data.stringBuilder.append("(");
        for (int i = 0; i < parameters.size(); i++) {
            var param = parameters.get(i);
            if(minArgCount != -1 && minArgCount < i+1 && !param.name.endsWith("?"))
                param.name += "?";
            param.accept(data);
            if(i != parameters.size()-1) {
                data.stringBuilder.append(", ");
            }
        }
        if(returnType.equals("")) {
            data.stringBuilder.append(")");
        } else {
            data.stringBuilder.append("): ");
            data.stringBuilder.append(returnType);
        }
        data.stringBuilder.append(";\n");
    }

    public String getSign() {
        if(cachedSign != null) {
            return cachedSign;
        }
        cachedSign = "(" +parameters.stream().map(x -> x.type).collect(Collectors.joining(",")) + ")" + returnType;
        return cachedSign;
    }
}
