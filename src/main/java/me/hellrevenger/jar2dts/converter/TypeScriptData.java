package me.hellrevenger.jar2dts.converter;


import me.hellrevenger.jar2dts.typescriptDeclarations.Function;
import me.hellrevenger.jar2dts.typescriptDeclarations.Namespace;
import me.hellrevenger.jar2dts.typescriptDeclarations.Variable;
import me.hellrevenger.jar2dts.utils.DefaultMap;

import java.util.ArrayList;
import java.util.HashSet;

public class TypeScriptData {
    public static final TypeScriptData INSTANCE = new TypeScriptData();

    public Jar jar;

    public boolean reduceScope = true;

    public StringBuilder stringBuilder = new StringBuilder();
    public int indent = 0;

    public boolean accessingStatic = false;

    public boolean accessingOriginalName = false;
    public boolean accessingConstructor = false;
    public String location = "";

    public final Namespace rootNamespace = new Namespace("");

    public ArrayList<Class<?>> classes = new ArrayList<>();

    public String inputFile;
    public String outputFile;
    public String namespacePrefix;

    public Mapping mapping = new Mapping();
    public Mapping mapping2 = new Mapping();

    public final HashSet<Namespace> toRedo = new HashSet<>();

    public TypeScriptData() {
    }

    public void parse() {
        stringBuilder.append("type ClassLike = {\n  class: ").append(namespacePrefix).append("java.lang.Class<any>;\n" +
                "  [Symbol.hasInstance](v): boolean\n }\n");
        stringBuilder.append("""
                type isAny<T> = (T extends never ? true : false) extends false ? false : true;
                type CombineTypes<A> = (
                  A extends [infer B, ...infer Rest] ?
                    isAny<B> extends true ?
                      CombineTypes<Rest>
                      : CombineTypes<Rest> extends never ? B : B & CombineTypes<Rest>
                    : A extends [infer B] ?
                      isAny<B> extends true ? never : B
                  : never
                )
                """);
        stringBuilder.append("""
                type char   = number & {};
                type byte   = number & {};
                type short  = number & {};
                type int    = number & {};
                type long   = number | BigInt;
                type float  = number & {};
                type double = number & {};
                type Function$$JS = Function;
                """);
        rootNamespace.accept(this);
        toRedo.forEach(namespace -> namespace.redo(this));
    }

    public Namespace getNamespace(String name) {
        return rootNamespace.getNamespace(name);
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

    public void setPrefix(String prefix) {
        if (prefix.endsWith(".")) {
            prefix = prefix.replaceAll("\\.+$", "");
        }
        this.namespacePrefix = prefix + ".";
        rootNamespace.name = prefix;
    }
}

