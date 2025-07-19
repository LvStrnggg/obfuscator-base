package dev.lvstrng.base;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.ZipFile;

/**
 * @author SpigotRCE
 * Adds all library classes to a map to computer frams in {@link dev.lvstrng.base.utils.CustomClassWriter}
 */
public class DependencyAnalyzer {
    public static HashMap<String, ClassNode> libraryClasses = new HashMap<>();

    public static void analyzeDependencies(File dependenciesDirectory) {
        if (!dependenciesDirectory.isDirectory()) {
            System.out.println("Provided path is not a directory: " + dependenciesDirectory.getAbsolutePath());
            return;
        }

        File[] files = dependenciesDirectory.listFiles();
        if (files == null) {
            System.out.println("Dependencies directory is empty or not accessible: " + dependenciesDirectory.getAbsolutePath());
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                try {
                    try (var zipFile = new ZipFile(file)) {
                        for(var entry : zipFile.stream().toList()) {
                            try (var in = zipFile.getInputStream(entry)) {
                                var name = entry.getName();

                                if(name.endsWith(".class")) {
                                    ClassReader reader = new ClassReader(in);
                                    ClassNode node = new ClassNode();
                                    reader.accept(node, 0);
                                    libraryClasses.put(node.name, node);
                                }
                            }
                        }
                    } catch (IOException _) {}
                } catch (Exception e) {
                    System.err.println("Failed to analyze jar: " + file.getName());
                    e.printStackTrace();
                }
            }
        }
    }
}
