package me.hellrevenger.jar2dts.typescriptDeclarations;

import me.hellrevenger.jar2dts.converter.AcceptTypeScriptData;
import me.hellrevenger.jar2dts.converter.TypeScriptData;

import java.util.ArrayList;

public class Variable implements AcceptTypeScriptData {
    public String generics = "";
    public String type;

    public String name;

    public boolean isArray = false;

    public boolean isParam = false;

    public boolean isReadonly = false;

    public boolean isStatic = false;

    public boolean isPrivate = false;
    public boolean isProtected = false;
    public String renamedFrom = "";

    @Override
    public void accept(TypeScriptData data) {
        if(isStatic != data.accessingStatic) return;
        if(!isParam) {
            if(renamedFrom.isEmpty() && data.accessingOriginalName) return;
            data.appendIndent();
            if(!renamedFrom.isEmpty()) {
                if(data.accessingOriginalName) {
                    data.stringBuilder.append("/** @renamed-to ").append(name).append(" */");
                } else {
                    data.stringBuilder.append("/** @renamed-from ").append(renamedFrom).append(" */");
                }
            }

            if(isStatic) {
                data.stringBuilder.append("static ");
            }
            if(isReadonly) {
                data.stringBuilder.append("readonly ");
            } else if(isStatic) {
                //data.stringBuilder.append("var ");
            }
            if(isPrivate || isProtected) {
                data.stringBuilder.append("#");
            }
        } else {
            renamedFrom = name;
        }
        if(data.accessingOriginalName) {
            data.stringBuilder.append(renamedFrom);
        } else {
            data.stringBuilder.append(name);
        }
        data.stringBuilder.append(": ");
        data.stringBuilder.append(type);
        if(!generics.isEmpty()) {
            data.stringBuilder.append("<");
            data.stringBuilder.append(generics);
            data.stringBuilder.append(">");
        }
        if(isArray) {
            //data.stringBuilder.append("[]");
        }
        if(!isParam) {
            data.stringBuilder.append(";\n");
        }
    }
}
