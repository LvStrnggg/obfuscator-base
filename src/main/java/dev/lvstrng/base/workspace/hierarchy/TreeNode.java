package dev.lvstrng.base.workspace.hierarchy;

import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TreeNode {
    private final ClassNode owner;

    private final Set<ClassNode> parents = new HashSet<>();
    private final Set<ClassNode> children = new HashSet<>();

    public TreeNode(ClassNode owner, Hierarchy hierarchy) {
        this.owner = owner;
    }

    public ClassNode getOwner() {
        return owner;
    }

    public Set<ClassNode> getParents() {
        return parents;
    }

    public Set<ClassNode> getChildren() {
        return children;
    }
}
