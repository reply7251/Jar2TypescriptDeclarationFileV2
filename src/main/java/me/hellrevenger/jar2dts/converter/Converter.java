package me.hellrevenger.jar2dts.converter;

import me.hellrevenger.jar2dts.utils.Lists;
import me.hellrevenger.jar2dts.visitor.JavaClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Converter {

    public static void convert() throws IOException {
        var f = new File(TypeScriptData.INSTANCE.inputFile);
        if(!f.exists()){
            System.out.println("could not find specify input file: " + f.getAbsolutePath());
            return;
        }
        var jar = new Jar(TypeScriptData.INSTANCE.inputFile);
        TypeScriptData.INSTANCE.jar = jar;
        List<String> classNames = jar.getClasses();
        for(var className : classNames) {
            var byteCode = jar.getFileData(className);
            ClassReader cr = new ClassReader(byteCode);
            JavaClassVisitor classVisitor = new JavaClassVisitor();
            cr.accept(classVisitor, ClassReader.SKIP_FRAMES);

        }

        TypeScriptData.INSTANCE.parse();

        var out = new File(TypeScriptData.INSTANCE.outputFile);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(out))) {
            writer.append(TypeScriptData.INSTANCE.stringBuilder);
        }
        System.out.println("save to: " + out.getAbsolutePath());
    }
}
