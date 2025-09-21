package dev.lvstrng.base.workspace.hierarchy;

import dev.lvstrng.base.utils.ClassUtils;
import dev.lvstrng.base.workspace.Workspace;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClasspathLoader {
    private final Workspace workspace;

    public ClasspathLoader(Workspace workspace) {
        this.workspace = workspace;
    }

    public void loadClasspath(String path) {
        loadJavaClasspath();

        var depDir = new File(path);
        if (!depDir.isDirectory())
            return;

        var jarFiles = depDir.listFiles((_, name) -> name.endsWith(".jar"));
        if (jarFiles == null)
            return;

        for (var jar : jarFiles)
            parseJar(jar);
    }

    private void loadJavaClasspath() {
        try {
            var fs = getJRTFS();
            var stream = Files.walk(fs.getPath("/modules"));

            stream.filter(path -> path.toString().endsWith(".class")).forEach(path -> {
                try (var in = Files.newInputStream(path)) {
                    workspace.addToLibraries(ClassUtils.readAsDependency(in));
                } catch (IOException _) {}
            });
        } catch (IOException _) {}
    }

    private void parseJar(File path) {
        try (var zip = new ZipFile(path)) {
            for (var entry : Collections.list(zip.entries()))
                handleEntry(zip, entry);
        } catch (IOException _) {}
    }

    private void handleEntry(ZipFile zip, ZipEntry entry) {
        var name = entry.getName();
        if (!name.endsWith(".class"))
            return;

        try (var in = zip.getInputStream(entry)) {
            workspace.addToLibraries(ClassUtils.readAsDependency(in));
        } catch (IOException _) {}
    }

    private FileSystem getJRTFS() throws IOException {
        var uri = URI.create("jrt:/");
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            return FileSystems.newFileSystem(uri, Collections.emptyMap());
        }
    }
}
