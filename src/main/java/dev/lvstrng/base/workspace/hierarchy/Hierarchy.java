package dev.lvstrng.base.workspace.hierarchy;

import dev.lvstrng.base.workspace.Workspace;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.Map;

public class Hierarchy {
    private final Workspace workspace;
    private final Map<ClassNode, TreeNode> tree = new HashMap<>();

    public Hierarchy(Workspace workspace) {
        this.workspace = workspace;
        this.clearAndLoadTree();
    }

    public void clearAndLoadTree() {
        tree.clear();

        for(var classNode : workspace.getClasses()) createTree(classNode, null);
    }

    public void createTree(ClassNode parent, ClassNode child) {
        if(tree.get(parent) == null) {
            var node = new TreeNode(parent, this);

            if(parent.superName != null) {
                var sup = workspace.getClass(parent.superName);

                node.getParents().add(sup);
                createTree(sup, parent);
            }

            if(parent.interfaces != null) {
                parent.interfaces.forEach(itf -> {
                    var itfClass = workspace.getClass(itf);

                    node.getParents().add(itfClass);
                    createTree(itfClass, parent);
                });
            }

            tree.put(parent, node);
        }

        if(child != null)
            tree.get(parent).getChildren().add(child);
    }

    public TreeNode getTree(ClassNode parent) {
        if(tree.get(parent) == null)
            clearAndLoadTree(); //tree has changed, update it

        return tree.get(parent);
    }

    public TreeNode getTree(String parent) {
        return getTree(workspace.getClass(parent));
    }

    public Workspace getWorkspace() {
        return workspace;
    }
}
