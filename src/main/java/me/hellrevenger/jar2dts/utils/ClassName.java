package me.hellrevenger.jar2dts.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

public class ClassName {
    static HashMap<String, String> remapMap = new HashMap<>();
    static HashMap<String, String> remapMap2 = new HashMap<>();
    static HashMap<String, String> remapMap3 = new HashMap<>();
    public static String remap(String s) {
        s = s.replace("/",".").replace("$",".");
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

    static {
        /*
        remapMap.put("([^\\w]|^)int([^\\w]|$)","$1number$2");
        remapMap.put("([^\\w]|^)float([^\\w]|$)","$1number$2");
        remapMap.put("([^\\w]|^)double([^\\w]|$)","$1number$2");
        remapMap.put("([^\\w]|^)long([^\\w]|$)","$1number$2");
        remapMap.put("([^\\w]|^)short([^\\w]|$)","$1number$2");
        remapMap.put("([^\\w]|^)byte([^\\w]|$)","$1number$2");

         */

        remapMap.put("java.lang.String","string");
        remapMap.put("java.lang.Object","any");
        remapMap2.put("\\? super ","");
        remapMap2.put("\\? extends ","");
        remapMap2.put("\\.\\d+",".");
        remapMap3.put("<[^<]+?>\\.",".");


    }
}
