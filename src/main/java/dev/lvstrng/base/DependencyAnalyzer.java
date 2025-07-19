package dev.lvstrng.base;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.zip.ZipFile;

/**
 * @author SpigotRCE
 * Adds all library classes to a map to compute frames in {@link dev.lvstrng.base.utils.CustomClassWriter}
 */
public class DependencyAnalyzer {
    public static final HashMap<String, ClassNode> libraryClasses = new HashMap<>();

    public static void analyzeDependencies(File dependenciesDirectory) {
        analyzeJdkModules();
        System.out.println("Analyzing dependencies in: " + dependenciesDirectory.getAbsolutePath());
        if (!dependenciesDirectory.isDirectory()) {
            System.out.println("Provided path is not a directory: " + dependenciesDirectory.getAbsolutePath());
            return;
        }

        File[] files = dependenciesDirectory.listFiles();
        if (files == null) {
            System.out.println("Dependencies directory is empty or not accessible: " + dependenciesDirectory.getAbsolutePath());
            return;
        }

        System.out.println("Found " + files.length + " dependencies.");

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                System.out.println("Analyzing jar: " + file.getName());
                try (ZipFile zipFile = new ZipFile(file)) {
                    for (var entry : zipFile.stream().toList()) {
                        try (InputStream in = zipFile.getInputStream(entry)) {
                            var name = entry.getName();

                            if (name.endsWith(".class")) {
                                ClassReader reader = new ClassReader(in);
                                ClassNode node = new ClassNode();
                                reader.accept(node, 0);
                                libraryClasses.put(node.name, node);
                            }
                        } catch (IOException ignored) {
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Failed to analyze jar: " + file.getName());
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Analyzed " + libraryClasses.size() + " classes from dependencies.");
    }

    public static void analyzeJdkModules() {
        System.out.println("Analyzing all JDK modules via jrt:/");

        try {
            FileSystem jrtFs;
            try {
                jrtFs = FileSystems.getFileSystem(URI.create("jrt:/"));
            } catch (FileSystemNotFoundException e) {
                jrtFs = FileSystems.newFileSystem(URI.create("jrt:/"), Collections.emptyMap());
            }

            Path modulesPath = jrtFs.getPath("/modules");

            try (var modules = Files.list(modulesPath)) {
                for (Path modulePath : modules.toList()) {
                    String moduleName = modulePath.getFileName().toString();
                    System.out.println("Scanning module: " + moduleName);

                    Files.walk(modulePath)
                            .filter(p -> p.toString().endsWith(".class"))
                            .forEach(p -> {
                                try (InputStream in = Files.newInputStream(p)) {
                                    ClassReader reader = new ClassReader(in);
                                    ClassNode node = new ClassNode();
                                    reader.accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                                    libraryClasses.put(node.name, node);
                                } catch (IOException ignored) {
                                }
                            });
                }
            }

            System.out.println("Finished analyzing JDK modules. Total classes: " + libraryClasses.size());

        } catch (IOException e) {
            System.err.println("Failed to analyze JDK modules.");
            e.printStackTrace();
        }
    }
}
