package me.hellrevenger.jar2dts;

import me.hellrevenger.jar2dts.converter.Converter;
import me.hellrevenger.jar2dts.converter.TypeScriptData;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var options = new Options();
        options.parseArg("input","","", Options.OptionType.STRING, "i");
        options.parseArg("output", "output.d.ts", "", Options.OptionType.STRING, "o");
        options.parseArg("prefix", "", "", Options.OptionType.STRING, "p");
        options.parseArg("help", "", "help", Options.OptionType.BOOL, "h");
        options.parseArg("mapping", "", "mapping", Options.OptionType.STRING, "m");
        options.parseArg("mapping2", "", "mapping2", Options.OptionType.STRING, "m2");
        options.parseArg("noReduce", "true", "mapping2", Options.OptionType.BOOL, "n");

        options.parse(args);
        var mapping = options.get("mapping");
        if(!mapping.value().isEmpty()) {
            File file = new File(mapping.value());
            TypeScriptData.INSTANCE.mapping.parse(file);

            var mapping2 = options.get("mapping2");
            if(!mapping2.value().isEmpty()) {
                File file2 = new File(mapping2.value());
                TypeScriptData.INSTANCE.mapping2.parse(file2);
            }
        }



        TypeScriptData.INSTANCE.setPrefix(options.get("prefix").value());
        TypeScriptData.INSTANCE.inputFile = options.get("input").value();
        TypeScriptData.INSTANCE.outputFile = options.get("output").value();
        TypeScriptData.INSTANCE.reduceScope = options.get("noReduce").value().equals("true");

        if(TypeScriptData.INSTANCE.inputFile.equals("")) {
            System.out.println("-i inputFile -o outputFile -p prefix");
        } else {
            Converter.convert();
        }

    }
}
