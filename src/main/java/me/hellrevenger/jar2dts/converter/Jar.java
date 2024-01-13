package me.hellrevenger.jar2dts.converter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.jar.JarFile;

public class Jar extends JarFile{
    public Jar(String name) throws IOException {
        super(name);
    }

    public Jar(File file) throws IOException {
        super(file);
    }

    public ArrayList<String> getClasses() {
        ArrayList<String> classes = new ArrayList<>();
        var entries = this.entries();
        while (entries.hasMoreElements()) {
            var entry = entries.nextElement();
            if (!entry.isDirectory() && entry.getName().endsWith(".class") && !entry.getName().contains("-")) {
                classes.add(entry.getName());
            }
        }
        return classes;
    }

    public byte[] getFileData(String filename) throws IOException {
        InputStream inputStream = this.getInputStream(this.getJarEntry(filename));
        return inputStream.readAllBytes();
    }
}
