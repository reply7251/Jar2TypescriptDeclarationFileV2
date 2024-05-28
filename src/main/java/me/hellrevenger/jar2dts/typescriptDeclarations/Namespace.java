package me.hellrevenger.jar2dts.typescriptDeclarations;

import me.hellrevenger.jar2dts.converter.AcceptTypeScriptData;
import me.hellrevenger.jar2dts.converter.TypeScriptData;
import me.hellrevenger.jar2dts.utils.DefaultMap;

import java.util.ArrayList;
import java.util.function.Supplier;

public class Namespace implements AcceptTypeScriptData {
    public DefaultMap<String, Interface> interfaces = new DefaultMap<>();
    public DefaultMap<String, ArrayList<Function>> functions = new DefaultMap<>();
    public DefaultMap<String, Variable> variables = new DefaultMap<>();
    public DefaultMap<String, Namespace> namespaces = new DefaultMap<>();

    public String name;
    public String modified;

    public String scope;

    public Namespace(String name) {
        this(name, "");
    }

    public Namespace(String name, String scope) {
        this.name = name;
        this.scope = scope;
    }

    void acceptMembers(TypeScriptData data) {
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
    }

    @Override
    public void accept(TypeScriptData data) {
        if(name.equals("function")) {
            modified = "function";
            name = "_function";
            data.toRedo.add(this);
        }
        data.appendIndent();
        if(name.isEmpty()) {
            acceptMembers(data);
            return;
        }
        if(data.indent == 0) {
            data.stringBuilder.append("declare ");
        } else {
            //data.stringBuilder.append("export ");
        }
        data.stringBuilder.append("module ");
        data.stringBuilder.append(name);
        data.stringBuilder.append(" {\n");
        data.increaseIndent();

        acceptMembers(data);

        data.decreaseIndent();
        data.appendIndent();
        data.stringBuilder.append("}\n");
    }

    public void redo(TypeScriptData data) {
        data.appendIndent();
        data.stringBuilder.append("declare ").append("module ").append(scope.substring(1)).append(" {\n");
        data.increaseIndent();
        data.appendIndent();
        data.stringBuilder.append("export { ").append(name).append(" as ").append(modified).append("};");
        data.decreaseIndent();
        data.appendIndent();
        data.stringBuilder.append("}\n");
    }

    public boolean hasNamespace(String name) {
        if(name.contains(".")) {
            int i = name.indexOf(".");
            return namespaces.containsKey(name.substring(0, i)) && getNamespace(name.substring(0, i)).hasNamespace(name.substring(i + 1));
        }
        return namespaces.containsKey(name);
    }

    Supplier<Namespace> createNamespaceWithName(String name) {
        return () -> new Namespace(name, getFullName());
    }

    public Namespace getNamespace(String name) {
        String finalName = name;
        if(finalName.contains(".")) {
            int i = finalName.indexOf(".");
            return namespaces.getOrCreate(finalName.substring(0, i), createNamespaceWithName(finalName.substring(0, i))).getNamespace(name.substring(i+1));
        }
        return namespaces.getOrCreate(finalName, createNamespaceWithName(finalName));
    }

    public Interface getInterface(String name) {
        String finalName = name;
        if(finalName.contains(".")) {
            int i = finalName.lastIndexOf(".");
            return getNamespace(finalName.substring(0, i)).getInterface(finalName.substring(i+1));
        }
        return interfaces.getOrCreate(finalName, Interface::new);
    }

    public Interface get(String name, Supplier<Interface> constructor) {
        String finalName = name;
        if(finalName.contains(".")) {
            int i = finalName.lastIndexOf(".");
            return getNamespace(finalName.substring(0, i)).get(finalName.substring(i+1), constructor);
        }
        return interfaces.getOrCreate(finalName, constructor);
    }

    public Interface getTypescriptClass(String name) {
        String finalName = name;
        if(finalName.contains(".")) {
            int i = finalName.lastIndexOf(".");
            return getNamespace(finalName.substring(0, i)).getTypescriptClass(finalName.substring(i+1));
        }
        return interfaces.getOrCreate(finalName, TypescriptClass::new);
    }

    public String getFullName() {
        if(this.name.isEmpty()) {
            return this.scope;
        }
        return this.scope + "." + this.name;
    }
}
