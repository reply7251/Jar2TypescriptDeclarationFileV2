package me.hellrevenger.jar2dts.typescriptDeclarations;

import me.hellrevenger.jar2dts.converter.AcceptTypeScriptData;
import me.hellrevenger.jar2dts.converter.TypeScriptData;
import me.hellrevenger.jar2dts.utils.DefaultMap;
import me.hellrevenger.jar2dts.utils.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Interface implements AcceptTypeScriptData {
    public DefaultMap<String, ArrayList<Function>> functions = new DefaultMap<>();
    public DefaultMap<String, Variable> variables = new DefaultMap<>();

    public String superClass = "";

    public HashSet<String> interfaces = new HashSet<>();

    public String name;
    public String fullName;

    public String scope;

    public String generics = "";

    public boolean hasConstructor = false;

    public Function constructor = null;

    public String renamedFrom = "";

    public String getGenerics() {
        return generics.isEmpty() ? "" : "<" + generics + ">";
    }

    public void handleGenerics(TypeScriptData data) {
        if(!generics.isEmpty()) {
            data.stringBuilder.append("<");
            data.stringBuilder.append(generics);
            data.stringBuilder.append(">");
        }
    }

    public void handleStaticPre(TypeScriptData data) {
        data.appendIndent().append("interface _").append(name).append("$$static");
        handleGenerics(data);
        data.stringBuilder.append(" {\n");
        data.increaseIndent();
    }

    public void handleStaticPost(TypeScriptData data) {
        data.decreaseIndent().appendIndent().append("}\n");
        data.appendIndent().append("let ").append(name).append(": _").append(name).append("$$static").append(getGenerics()).append(" & ClassLike;\n");
    }

    public void handleStaticIn(TypeScriptData data) {
        data.accessingStatic = true;
        acceptMembers(data);
        data.accessingOriginalName = true;
        acceptMembers(data);
        data.accessingOriginalName = false;
        data.accessingStatic = false;

        data.accessingConstructor = true;
        acceptFunctions(data);
        data.accessingConstructor = false;
        //data.appendIndent().append("class: java.lang.Class<any>;\n");
    }

    public void handleStatic(TypeScriptData data) {
        handleStaticPre(data);
        handleStaticIn(data);
        handleStaticPost(data);
    }

    public void handleInstancePre(TypeScriptData data) {
        data.appendIndent().append("interface _").append(name);
        handleGenerics(data);
        data.stringBuilder.append(" {\n");
        data.increaseIndent();
    }

    public void handleInstancePost(TypeScriptData data) {

        data.decreaseIndent().appendIndent().append("}\n");
        data.appendIndent().append("interface ").append(name);
        handleGenerics(data);
        data.stringBuilder.append(" extends CombineTypes<[");

        var types = new HashSet<String>();
        //types.add("_" + name + getGenerics());
        if(!superClass.isEmpty()) types.add(superClass);
        types.addAll(interfaces);
        var typesWithSelf = new ArrayList<String>();
        typesWithSelf.add("_" + name + getGenerics());
        typesWithSelf.addAll(types);
        data.stringBuilder.append(String.join(", ", typesWithSelf));

        data.stringBuilder.append("]> {}\n");
    }

    public void handleInstanceIn(TypeScriptData data) {
        acceptMembers(data);
        data.accessingOriginalName = true;
        acceptMembers(data);
        data.accessingOriginalName = false;
    }

    public void handleInstance(TypeScriptData data) {
        handleInstancePre(data);
        handleInstanceIn(data);
        handleInstancePost(data);
    }

    @Override
    public void accept(TypeScriptData data) {
        if(name == null || name.isEmpty()) return;

        handleStatic(data);

        handleInstance(data);
    }

    public void acceptMembers(TypeScriptData data) {
        acceptFunctions(data);
        acceptVariables(data);
    }

    public void acceptFunctions(TypeScriptData data) {
        for(var func : functions.values()) {
            for(var declaration : func)
                declaration.accept(data);
        }
    }

    public void acceptVariables(TypeScriptData data) {
        for(var v : variables.values()) {
            v.accept(data);
        }
    }

    public ArrayList<Function> getFunction(String name) {
        return functions.getOrCreate(name, ArrayList::new);
    }

    public Variable getVariable(String name) {
        return variables.getOrCreate(name, Variable::new);
    }
}
