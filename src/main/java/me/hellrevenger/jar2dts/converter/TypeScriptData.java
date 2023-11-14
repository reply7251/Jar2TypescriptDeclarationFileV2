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
    public Mapping mapping2 = new Mapping();

    public TypeScriptData() {
    }

    public void parse() {
        stringBuilder.append("type ClassLike = { class: "+namespacePrefix+"java.lang.Class<any> }\n");
        stringBuilder.append("""
                type isAny<T> = (T extends never ? true : false) extends false ? false : true;
                type CombineTypes<A> = (
                  A extends [infer B] ?
                    isAny<B> extends true ? never : B
                  : A extends [infer B, ...infer Rest] ?
                    isAny<B> extends true ?
                      CombineTypes<Rest>
                    : CombineTypes<Rest> extends never ? B : B & CombineTypes<Rest>
                  : never
                )
                """);
        for(var namespace : namespaces.values()) {
            namespace.accept(this);
        }
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

    public StringBuilder appendIndent() {
        stringBuilder.append(" ".repeat(indent));
        return stringBuilder;
    }

    public TypeScriptData increaseIndent() {
        indent += 2;
        return this;
    }

    public TypeScriptData decreaseIndent() {
        indent -= 2;
        return this;
    }
}

