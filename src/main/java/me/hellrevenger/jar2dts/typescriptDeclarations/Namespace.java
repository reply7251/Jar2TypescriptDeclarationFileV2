package me.hellrevenger.jar2dts.typescriptDeclarations;

import me.hellrevenger.jar2dts.converter.AcceptTypeScriptData;
import me.hellrevenger.jar2dts.converter.TypeScriptData;
import me.hellrevenger.jar2dts.utils.DefaultMap;

import java.util.ArrayList;

public class Namespace implements AcceptTypeScriptData {
    public DefaultMap<String, Interface> interfaces = new DefaultMap<>();
    public DefaultMap<String, ArrayList<Function>> functions = new DefaultMap<>();
    public DefaultMap<String, Variable> variables = new DefaultMap<>();
    public DefaultMap<String, Namespace> namespaces = new DefaultMap<>();

    public String name;
    public String modified;
    public String fullName;

    public Namespace(String name) {
        this.name = name;
    }

    @Override
    public void accept(TypeScriptData data) {
        if(name.isEmpty()) return;
        if(name.equals("function")) {
            modified = "function";
            name = "_function";
        }
        data.appendIndent();
        if(data.indent == 0) {
            data.stringBuilder.append("declare ");
        } else {
            //data.stringBuilder.append("export ");
        }
        data.stringBuilder.append("namespace ");
        data.stringBuilder.append(name);
        data.stringBuilder.append(" {\n");
        data.increaseIndent();
        for(var namespace : namespaces.values()) {
            namespace.accept(data);
        }
        for(var interf : interfaces.values()) {
            interf.accept(data);
        }
        for(var func : functions.values()) {
            for(var declaration : func)
                declaration.accept(data);
        }
        for(var v : variables.values()) {
            v.accept(data);
        }



        if(modified != null) {
            data.appendIndent();
            data.stringBuilder.append("export { ");
            data.stringBuilder.append(name);
            data.stringBuilder.append(" as ");
            data.stringBuilder.append(modified);
            data.stringBuilder.append(" }\n");
        }

        data.decreaseIndent();
        data.appendIndent();
        data.stringBuilder.append("}\n");
    }

    public boolean hasInterface(String name) {
        if(name.contains(".")) {
            int i = name.indexOf(".");
            return namespaces.containsKey(name.substring(0, i)) && hasInterface(name.substring(i + 1));
        }
        return interfaces.containsKey(name);
    }

    public boolean hasNamespace(String name) {
        if(name.contains(".")) {
            int i = name.indexOf(".");
            return namespaces.containsKey(name.substring(0, i)) && getNamespace(name.substring(0, i)).hasNamespace(name.substring(i + 1));
        }
        return namespaces.containsKey(name);
    }

    public Namespace getNamespace(String name) {
        String finalName = name;
        if(finalName.contains(".")) {
            int i = finalName.indexOf(".");
            return namespaces.getOrCreate(finalName.substring(0, i), () -> new Namespace(finalName.substring(0, i))).getNamespace(name.substring(i+1));
        }
        return namespaces.getOrCreate(finalName, () -> new Namespace(finalName));
    }

    public Interface getInterface(String name) {
        String finalName = name;
        if(finalName.contains(".")) {
            int i = finalName.indexOf(".");
            return namespaces.getOrCreate(finalName.substring(0, i), () -> new Namespace(finalName.substring(0, i))).getInterface(name.substring(i+1));
        }
        return interfaces.getOrCreate(finalName, Interface::new);
    }

    public Interface getTypescriptClass(String name) {
        String finalName = name;
        if(finalName.contains(".")) {
            int i = finalName.indexOf(".");
            return namespaces.getOrCreate(finalName.substring(0, i), () -> new Namespace(finalName.substring(0, i))).getTypescriptClass(name.substring(i+1));
        }
        return interfaces.getOrCreate(finalName, TypescriptClass::new);
    }

    public ArrayList<Function> getFunction(String name) {
        if(name.contains(".")) {
            int i = name.indexOf(".");
            return namespaces.getOrCreate(name.substring(0, i), () -> new Namespace(name.substring(0, i))).getFunction(name.substring(i+1));
        }
        return functions.getOrCreate(name, ArrayList::new);
    }

    public Variable getVariable(String name) {
        if(name.contains(".")) {
            int i = name.indexOf(".");
            return namespaces.getOrCreate(name.substring(0, i), () -> new Namespace(name.substring(0, i))).getVariable(name.substring(i+1));
        }
        return variables.getOrCreate(name, Variable::new);
    }
}
