package me.hellrevenger.jar2dts;

import me.hellrevenger.jar2dts.converter.TypeScriptData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Options {
    HashMap<String, Option> options = new HashMap<>();

    public HashMap<String, Option> getOptions() {
        return options;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public Option get(String key) {
        return options.get(key);
    }

    ArrayList<String> args = new ArrayList<>();

    public void parseArg(String arg, String defaultValue, String description, OptionType optionType, String alias) {
        Option option = new Option();
        option.defaultValue = defaultValue;
        option.description = description;
        option.type = optionType;
        options.put(arg, option);
        options.put(alias, option);
    }

    public void parse(String[] args) {
        for(int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                if (args[i].length() < 2) {
                    throw new IllegalArgumentException("Not a valid argument: "+args[i]);
                }
                if (args[i].charAt(1) == '-') {
                    if (args[i].length() < 3)
                        throw new IllegalArgumentException("Not a valid argument: "+args[i]);
                    // --opt
                    Option option = options.get(args[i].substring(2));
                    if(option != null) {
                        switch (option.type) {
                            case BOOL:
                                option.value = String.valueOf(List.of("null", "false").contains(option.defaultValue));
                                break;
                            case STRING:
                            case NUMBER:
                                if (args.length - 1 == i) {
                                    throw new IllegalArgumentException("Expected arg after: " + args[i]);
                                }
                                option.value = args[++i];
                                break;
                        }
                    }
                } else {
                    if (args.length-1 == i)
                        throw new IllegalArgumentException("Expected arg after: "+args[i]);
                    Option option = options.get(args[i].substring(1));
                    if(option != null) {
                        switch (option.type) {
                            case BOOL:
                                option.value = String.valueOf(List.of("null", "false").contains(option.defaultValue));
                                break;
                            case STRING:
                            case NUMBER:
                                if (args.length - 1 == i) {
                                    throw new IllegalArgumentException("Expected arg after: " + args[i]);
                                }
                                option.value = args[++i];
                                break;
                        }
                    }
                }
            } else {
                this.args.add(args[i]);
            }
        }
    }



    public static class Option {
        String defaultValue = null;
        String description;
        String value = null;

        OptionType type = OptionType.BOOL;

        public String value() {
            return value == null ? defaultValue : value;
        }
    }

    public enum OptionType {
        BOOL,
        NUMBER,
        STRING
    }
}
