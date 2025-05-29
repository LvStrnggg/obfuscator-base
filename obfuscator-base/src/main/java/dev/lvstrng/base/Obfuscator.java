package dev.lvstrng.base;

import dev.lvstrng.base.jar.ClassPool;
import dev.lvstrng.base.transform.Transformer;
import dev.lvstrng.base.utils.CustomClassWriter;
import dev.lvstrng.base.utils.Utils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Obfuscator {
    private final File input;
    private final int writerFlags;
    private String whitelist;
    private final List<ClassNode> excludes = new ArrayList<>();

    public Obfuscator(String input, boolean computeFrames) {
        this.input = new File(input);
        writerFlags = computeFrames ? ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES : ClassWriter.COMPUTE_MAXS;
    }

    public void read() {
        try (var zipFile = new ZipFile(input)) {
            for(var entry : zipFile.stream().toList()) {
                try (var in = zipFile.getInputStream(entry)) {
                    var name = entry.getName();

                    if(name.endsWith(".class")) {
                        var classNode = readClass(in);

                        if (whitelist != null && !name.startsWith(whitelist)) {
                            excludes.add(classNode);
                        } else {
                            ClassPool.add(classNode);
                            ClassPool.setClassFileMajor(classNode.version);
                        }
                    }
                }
            }
        } catch (IOException _) {}
    }

    public void obfuscate(Transformer... transformers) {
        if(transformers == null)
            return;

        for(var transformer : transformers) {
            System.out.println("Applying " + transformer.getClass().getSimpleName());
            transformer.transform();
        }
    }

    public void save(String fileName) {
        var classes = new ArrayList<>(ClassPool.getClasses());
        classes.addAll(ClassPool.getArtificials());
        classes.addAll(excludes);

        var file = new File(fileName);
        try (var zipOut = new ZipOutputStream(new FileOutputStream(file)); var zipFile = new ZipFile(input)) {
            if(!classes.isEmpty()) {
                for(var classNode : classes) {
                    var bytes = writeClass(classNode);
                    var classEntry = new ZipEntry(classNode.name.replace('.', '/') + ".class");

                    zipOut.putNextEntry(classEntry);
                    zipOut.write(bytes);
                    zipOut.closeEntry();
                }
            }

            for(var entry : zipFile.stream().toList()) {
                if(entry.getName().endsWith(".class"))
                    continue;

                zipOut.putNextEntry(new ZipEntry(entry.getName()));
                try (var is = zipFile.getInputStream(entry)) {
                    is.transferTo(zipOut);
                }

                zipOut.closeEntry();
            }
        } catch (IOException _) {}

        var inLength = Utils.bytesToKB(input.length());
        var outLength = Utils.bytesToKB(file.length());

        System.out.printf("Obfuscated " + input.getName() + ": %.2fkb -> %.2fkb%n", inLength, outLength);
    }

    public void whitelist(String path) {
        this.whitelist = path;
    }

    private ClassNode readClass(InputStream in) throws IOException {
        var reader = new ClassReader(in);
        var node = new ClassNode();
        reader.accept(node, 0);
        return node;
    }

    public byte[] writeClass(ClassNode classNode) {
        var writer = new CustomClassWriter(writerFlags);
        try {
            classNode.accept(writer);
        } catch (AssertionError _) {}
        return writer.toByteArray();
    }
}
