package me.hellrevenger.jar2dts.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

public class ClassName {
    static HashMap<String, String> remapMap = new HashMap<>();
    static HashMap<String, String> remapMap2 = new HashMap<>();
    static HashMap<String, String> remapMap3 = new HashMap<>();
    public static String remap(String s) {
        s = s.replace("/",".");;
        return _remap(s);
    }

    public static String remapForNamespace(String s) {
        s = s.replace("/",".");
        return s;
    }

    static String _remap(String s) {
        for(var entry : remapMap.entrySet()) {
            if(s.contains(entry.getKey())) {
                s = s.replaceAll(entry.getKey(), entry.getValue());
            }
        }

        for(var entry : remapMap2.entrySet()) {
            s = s.replaceAll(entry.getKey(), entry.getValue());
        }
        for(var entry : remapMap3.entrySet()) {
            String s2 = s.replaceAll(entry.getKey(), entry.getValue());
            while (!s2.equals(s)) {
                s = s2;
                s2 = s.replaceAll(entry.getKey(), entry.getValue());
            }
        }
        return s;
    }

    public static boolean isPrimitiveType(String type) {
        return remapMap.containsValue(type);
    }

    static {
        remapMap.put("java.lang.String","string");
        remapMap.put("java.lang.Object","any");
        remapMap.put("java.lang.Number","number");
        remapMap.put("java.lang.Float","float");
        remapMap.put("java.lang.Double","double");
        remapMap.put("java.lang.Integer","int");
        remapMap.put("java.lang.Long","long");
        remapMap.put("java.lang.Short","short");
        remapMap.put("java.lang.Byte","byte");
        remapMap.put("java.lang.Char","char");
        remapMap.put("java.lang.Boolean","boolean");
        remapMap2.put("\\? super ","");
        remapMap2.put("\\? extends ","");
        remapMap2.put("\\.\\d+",".");
        remapMap3.put("<[^<]+?>\\.",".");
    }
}
