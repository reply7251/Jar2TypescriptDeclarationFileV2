package me.hellrevenger.jar2dts;

import me.hellrevenger.jar2dts.converter.Converter;
import me.hellrevenger.jar2dts.converter.TypeScriptData;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello World");
        var options = new Options();
        options.parseArg("input","","", Options.OptionType.STRING, "i");
        options.parseArg("output", "output.d.ts", "", Options.OptionType.STRING, "o");
        options.parseArg("prefix", "", "", Options.OptionType.STRING, "p");
        options.parseArg("help", "", "help", Options.OptionType.BOOL, "h");
        options.parseArg("mapping", "", "mapping", Options.OptionType.STRING, "m");

        options.parse(args);
        var mapping = options.get("mapping");
        if(!mapping.value().isEmpty()) {
            File file = new File(mapping.value());
            TypeScriptData.INSTANCE.mapping.parse(file);
        }



        TypeScriptData.INSTANCE.namespacePrefix = options.get("prefix").value();
        TypeScriptData.INSTANCE.inputFile = options.get("input").value();
        TypeScriptData.INSTANCE.outputFile = options.get("output").value();

        if(TypeScriptData.INSTANCE.inputFile.equals("")) {
            System.out.println("-i inputFile -o outputFile -p prefix");
        } else {
            Converter.convert();
        }

    }
}
