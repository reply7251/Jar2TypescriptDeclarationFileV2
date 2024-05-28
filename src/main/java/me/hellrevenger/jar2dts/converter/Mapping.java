package me.hellrevenger.jar2dts.converter;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Mapping {
    public final HashMap<String, ClassMapping> classes = new HashMap<>();

    public void parse(File file) throws FileNotFoundException {
        StringBuilder contentBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        try {
            while((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String content = contentBuilder.toString();
        var class_mappings = new java.util.ArrayList<>(Arrays.stream(content.split("\nc"))
                .map(clazz ->
                        Arrays.stream(clazz.split("\n")).map(member ->
                                member.split("\t")
                        ).collect(Collectors.toList())
                ).toList());
        var firstLine = class_mappings.remove(0);
        //System.out.println("firstLine: " + String.join("," + firstLine.get(0)));
        var counter = 0;
        for(var clazz : class_mappings) {
            var class_def = clazz.remove(0);
            if(class_def.length < 2) {
                continue;
            }
            //System.out.println("class_def: " + String.join(", ",class_def));
            var from = class_def[1].replace("/",".");;
            var to = class_def[2].replace("/",".");;

            var current_class = new ClassMapping(from, to);
            for(var item : clazz) {
                if(String.join("",item).isEmpty()) continue;
                //System.out.println("item: " + String.join(", ",item));
                switch (item[1]) {
                    case "m":
                    case "f":
                        current_class.members.put(item[3], item[4]);
                        break;
                }
            }

            classes.put(from, current_class);
            classes.put(to, current_class);
        }
    }

    public String map(String clazz, String member) {
        clazz = clazz.replace("/",".");;
        return classes.containsKey(clazz) ? classes.get(clazz).map(member) : member;
    }

    public String map(String clazz) {
        clazz = clazz.replace("/",".");;
        return classes.containsKey(clazz) ? classes.get(clazz).map() : clazz;
        //return clazz;
    }

    public String reverse(String clazz) {
        clazz = clazz.replace("/",".");
        return classes.containsKey(clazz) ? classes.get(clazz).from : clazz;
    }

    static class ClassMapping {
        public final String from, to;

        HashMap<String, String> members = new HashMap<>();

        public ClassMapping(String from, String to) {
            this.from = from;
            this.to = to;
        }

        public String map(String member) {
            if(members.containsKey(member)) {
                return members.get(member);
            }
            return member;
        }

        public String map() {
            return to;
        }
    }
}
