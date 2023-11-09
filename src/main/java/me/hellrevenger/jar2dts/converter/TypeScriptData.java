package me.hellrevenger.jar2dts.converter;


import me.hellrevenger.jar2dts.typescriptDeclarations.Function;
import me.hellrevenger.jar2dts.typescriptDeclarations.Namespace;
import me.hellrevenger.jar2dts.typescriptDeclarations.Variable;
import me.hellrevenger.jar2dts.utils.DefaultMap;

import java.util.ArrayList;
import java.util.HashMap;

public class TypeScriptData {
    public static final TypeScriptData INSTANCE = new TypeScriptData();

    public StringBuilder stringBuilder = new StringBuilder();
    public int indent = 0;

    public boolean accessingStatic = false;

    public boolean accessingOriginalName = false;
    public boolean accessingConstructor = false;
    public String location = "";

    public DefaultMap<String, Namespace> namespaces = new DefaultMap<>();

    public ArrayList<Class<?>> classes = new ArrayList<>();

    public String inputFile;
    public String outputFile;
    public String namespacePrefix;

    public Mapping mapping = new Mapping();

    public TypeScriptData() {
    }

    public boolean hasNamespace(String name) {
        if(name.contains(".")) {
            int i = name.indexOf(".");
            return namespaces.containsKey(name.substring(0, i)) && getNamespace(name.substring(0, i)).hasNamespace(name.substring(i + 1));
        }
        return namespaces.containsKey(name);
    }

    public Namespace getNamespace(String name) {
        if(name.contains(".")) {
            int i = name.indexOf(".");
            return namespaces.getOrCreate(name.substring(0, i), () -> new Namespace(name.substring(0, i))).getNamespace(name.substring(i+1));
        }
        return namespaces.getOrCreate(name, () -> new Namespace(name));
    }

    public void appendIndent() {
        stringBuilder.append(" ".repeat(indent));
    }

    public void increaseIndent() {
        indent += 2;
    }

    public void decreaseIndent() {
        indent -= 2;
    }
}

