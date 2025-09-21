package dev.lvstrng.base.workspace.output;

import dev.lvstrng.base.workspace.Workspace;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.HashSet;

public class HierarchyClassWriter extends ClassWriter {
    private final Workspace workspace;

    public HierarchyClassWriter(Workspace workspace, int flags) {
        super(flags);
        this.workspace = workspace;
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        if(type1.equals("java/lang/Object") || type2.equals("java/lang/Object"))
            return "java/lang/Object";

        var first = getCommonSuperName(type1, type2);
        var second = getCommonSuperName(type2, type1);
        if(!first.equals("java/lang/Object"))
            return first;

        if(!second.equals("java/lang/Object"))
            return second;

        return getCommonSuperClass(
                workspace.getClass(type1).superName,
                workspace.getClass(type2).superName
        );
    }

    private String getCommonSuperName(String type1, String type2) {
        var first = workspace.getClass(type1);
        var second = workspace.getClass(type2);

        if(isAssignableFrom(type1, type2))
            return type1;

        if(this.isAssignableFrom(type2, type1))
            return type2;

        if(Modifier.isInterface(first.access) || Modifier.isInterface(second.access))
            return "java/lang/Object";

        String type;
        do {
            first = workspace.getClass(type = first.superName);
        } while (!isAssignableFrom(type, type2));
        return type;
    }

    private boolean isAssignableFrom(String parent, String child) {
        if(parent.equals("java/lang/Object") || parent.equals(child))
            return true;

        var hierarchy = workspace.getHierarchy();
        var parentTree = hierarchy.getTree(parent);

        var checked = new HashSet<ClassNode>();
        var queue = new ArrayDeque<>(parentTree.getChildren());

        while (!queue.isEmpty()) {
            var type = queue.poll();
            if(!checked.add(type))
                continue;

            var tree = hierarchy.getTree(type);
            queue.addAll(tree.getChildren());
        }

        return checked.contains(workspace.getClass(child));
    }
}
