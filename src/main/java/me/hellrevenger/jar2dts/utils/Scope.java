package me.hellrevenger.jar2dts.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Scope {
    public static String reduceScope(String scope, String target) {
        target = target.replaceAll("/",".");

        String[] scopes1 = scope.split("\\.");
        String[] scopes2 = target.split("\\.");


        int len = Math.min(scopes1.length, scopes2.length);//Math.min(scope.length(), Math.max(target.lastIndexOf("."), 0));

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
