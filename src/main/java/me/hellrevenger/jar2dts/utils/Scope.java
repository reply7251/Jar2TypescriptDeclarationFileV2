package me.hellrevenger.jar2dts.utils;

import me.hellrevenger.jar2dts.converter.TypeScriptData;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Scope {
    public static String reduceScope(String scope, String target) {
        target = target.replaceAll("/",".");
        if(!TypeScriptData.INSTANCE.reduceScope) {
            return target;
        }

        String[] scopes1 = scope.split("\\.");
        String[] scopes2 = target.split("\\.");

        if(scopes2.length == 1 && scopes1.length > 1 && !ClassName.isPrimitiveType(target)) {
            if(scope.endsWith("$" + target)) {
                return scopes1[scopes1.length-1];
            }
            for(var className : TypeScriptData.INSTANCE.jar.getClasses()) {
                className = className.substring(0, className.length() - 6).replace("/",".");
                var index = className.lastIndexOf(".");
                if(!className.substring(0, index).equals(scope.substring(0, scope.lastIndexOf(".")))) {
                    continue;
                }
                if(className.endsWith("$" + target)) {
                    return className.substring(index+1);
                }
            }
            return scopes1[scopes1.length-1] + "$" + target;
        }


        int len = Math.min(scopes1.length, scopes2.length-1);//Math.min(scope.length(), Math.max(target.lastIndexOf("."), 0));

        for(int i = 0; i < len; i++) {
            if(!scopes1[i].equals(scopes2[i])) {
                if(i > 0) {
                    String result = String.join(".", Arrays.copyOfRange(scopes2, i-1, scopes2.length));
                    return result.startsWith("function.") ? target : result;
                }
                return target;
            }
        }

        String result = String.join(".", Arrays.copyOfRange(scopes2, len, scopes2.length));
        return result.startsWith("function.") ? target : result;
    }
}
