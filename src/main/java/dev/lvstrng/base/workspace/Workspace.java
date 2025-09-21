package dev.lvstrng.base.workspace;

import dev.lvstrng.base.transform.Transformer;
import dev.lvstrng.base.utils.ClassUtils;
import dev.lvstrng.base.utils.ZipUtils;
import dev.lvstrng.base.workspace.exception.MissingLibraryException;
import dev.lvstrng.base.workspace.hierarchy.ClasspathLoader;
import dev.lvstrng.base.workspace.hierarchy.Hierarchy;
import dev.lvstrng.base.workspace.resource.ResourceHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Workspace {
    private int major = 0;
    private final String input, libPath;
    public int writerFlags;

    private final Map<String, ClassNode> classes, libraries, artificials;
    private final ResourceHandler resourceHandler;
    private Hierarchy hierarchy;

    public Workspace(String input, String libPath, boolean computeFrames) {
        this.input = input;
        this.libPath = libPath;
        this.writerFlags = ClassWriter.COMPUTE_MAXS;
        if(computeFrames)
            this.writerFlags |= ClassWriter.COMPUTE_FRAMES;

        this.classes        = new HashMap<>();
        this.libraries      = new HashMap<>();
        this.artificials    = new HashMap<>();

        this.resourceHandler = new ResourceHandler(this);
    }

    public void transform(Transformer... transformers) {
        for(var transformer : transformers) {
            transformer.transform(this);
        }
    }

    public void readInput() {
        new ClasspathLoader(this).loadClasspath(libPath);

        var file = new File(input);
        if(!file.exists())
            throw new IllegalStateException("This file does not exist");

        try (var zip = new ZipFile(file)) {
            zip.stream().forEach(entry -> handleEntry(zip, entry));
        } catch (IOException _) {}

        hierarchy = new Hierarchy(this);
    }

    private void handleEntry(ZipFile zip, ZipEntry entry) {
        try {
            var in = zip.getInputStream(entry);
            var name = entry.getName();

            if(name.endsWith(".class") || name.endsWith(".class/")) {
                var clazz = ClassUtils.readClass(in);
                major = Math.max(clazz.version, major);
                classes.put(clazz.name, clazz);
                return;
            }

            resourceHandler.addResource(name, in.readAllBytes());
        } catch (IOException _) {}
    }

    public void exportJar(String outName) {
        var allClasses = new HashMap<>(classes);
        allClasses.putAll(artificials);

        try (var zos = new ZipOutputStream(new FileOutputStream(outName))) {
            for(var entry : allClasses.entrySet()) {
                var name = entry.getKey().replace('.', '/') + ".class";
                var node = entry.getValue();

                ZipUtils.writeEntry(
                        zos,
                        ClassUtils.classBytesWithHierarchy(this, node, writerFlags),
                        name
                );
            }

            resourceHandler.writeToOutput(zos);
            zos.finish();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save obfuscated output:", e);
        }
    }

    public ClassNode getClass(String name) {
        var classNode = classes.get(name);
        if(classNode == null)
            classNode = libraries.get(name);

        if(classNode == null)
            classNode = artificials.get(name);

        if(classNode != null)
            return classNode;

        throw new MissingLibraryException(name);
    }

    public void addToLibraries(ClassNode classNode) {
        libraries.put(classNode.name, classNode);
    }

    public List<ClassNode> getClasses() {
        return classes.values().stream().toList();
    }

    public Map<String, ClassNode> getClassMap() {
        return classes;
    }

    public Map<String, ClassNode> getLibraries() {
        return libraries;
    }

    public Map<String, ClassNode> getArtificials() {
        return artificials;
    }

    public Hierarchy getHierarchy() {
        return hierarchy;
    }
}