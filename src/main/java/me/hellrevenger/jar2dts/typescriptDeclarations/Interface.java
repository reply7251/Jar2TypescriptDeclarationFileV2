package me.hellrevenger.jar2dts.typescriptDeclarations;

import me.hellrevenger.jar2dts.converter.AcceptTypeScriptData;
import me.hellrevenger.jar2dts.converter.TypeScriptData;
import me.hellrevenger.jar2dts.utils.DefaultMap;
import me.hellrevenger.jar2dts.utils.Lists;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Interface implements AcceptTypeScriptData {
    public DefaultMap<String, ArrayList<Function>> functions = new DefaultMap<>();
    public DefaultMap<String, Variable> variables = new DefaultMap<>();

    public String superClass = "";

    public HashSet<String> interfaces = new HashSet<>();

    public String name;
    public String fullName;
    public String generics = "";

    public boolean hasConstructor = false;

    public Function constructor = null;

    public String renamedFrom = "";

    public void handleGenerics(TypeScriptData data) {
        if(!generics.isEmpty()) {
            data.stringBuilder.append("<");
            data.stringBuilder.append(generics);
            data.stringBuilder.append(">");
        }
    }

    public void handleSuperClass(TypeScriptData data) {
        var supers = new ArrayList<String>();

        if(!superClass.isEmpty()) {
            supers.add(superClass);
        }
        if(!interfaces.isEmpty()) {
            supers.addAll(interfaces);
        }
        if(!supers.isEmpty()) {
            data.stringBuilder.append(" extends ");
            data.stringBuilder.append(String.join(", ", supers));
        }
    }

    @Override
    public void accept(TypeScriptData data) {
        if(name.isEmpty()) return;
        data.appendIndent();
        data.stringBuilder.append("interface ");
        data.stringBuilder.append(name);
        handleGenerics(data);

        handleSuperClass(data);
        data.stringBuilder.append(" {\n");
        data.increaseIndent();


        acceptMembers(data);

        data.accessingOriginalName = true;
        acceptMembers(data);
        data.accessingOriginalName = false;

        data.decreaseIndent();
        data.appendIndent();
        data.stringBuilder.append("}\n");

        data.appendIndent();
        data.stringBuilder.append("class ");
        data.stringBuilder.append(name);
        handleGenerics(data);
        data.stringBuilder.append(" extends ");
        data.stringBuilder.append(name);
        handleGenerics(data);
        data.stringBuilder.append(" {\n");
        data.increaseIndent();

        data.accessingStatic = true;
        acceptMembers(data);
        data.accessingOriginalName = true;
        acceptMembers(data);
        data.accessingStatic = false;
        acceptMembers(data);
        data.accessingOriginalName = false;

        data.decreaseIndent();
        data.appendIndent();
        data.stringBuilder.append("}\n");



        /*
        data.appendIndent();
        data.stringBuilder.append("declare var ");
        data.stringBuilder.append(name);
        data.stringBuilder.append(": ");
        data.stringBuilder.append(name);
        data.stringBuilder.append(";\n");

         */
    }

    public void acceptMembers(TypeScriptData data) {
        for(var func : functions.values()) {
            for(var declaration : func)
                declaration.accept(data);
        }
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
