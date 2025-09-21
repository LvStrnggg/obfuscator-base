package dev.lvstrng.base.utils;

import dev.lvstrng.base.workspace.Workspace;
import dev.lvstrng.base.workspace.output.HierarchyClassWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;

public class ClassUtils {
    public static ClassNode readClass(byte[] bytes) {
        var node = new ClassNode();
        new ClassReader(bytes).accept(node, 0);
        return node;
    }

    public static ClassNode readClass(InputStream is) throws IOException {
        return readClass(is.readAllBytes());
    }

    public static ClassNode readAsDependency(InputStream in) throws IOException {
        var node = new ClassNode();
        new ClassReader(in).accept(node, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES);
        return node;
    }

    public static byte[] classToBytes(ClassNode node, int flags) {
        var writer = new ClassWriter(flags);
        node.accept(writer);
        return writer.toByteArray();
    }

    public static byte[] classBytesWithHierarchy(Workspace workspace, ClassNode classNode, int flags) {
        var writer = new HierarchyClassWriter(workspace, flags);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
