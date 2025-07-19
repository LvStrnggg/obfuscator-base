package dev.lvstrng.base.utils;

import dev.lvstrng.base.DependencyAnalyzer;
import dev.lvstrng.base.jar.ClassPool;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lvstrng
 * tries to fix the "Type not present" errors
 */
public class CustomClassWriter extends ClassWriter {
    public CustomClassWriter(int flags) {
        super(flags);
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        var class1 = DependencyAnalyzer.libraryClasses.get(type1);
        var class2 = DependencyAnalyzer.libraryClasses.get(type2);

        if (class1 != null && class2 != null) {
            if (type1.equals(type2)) return type1;

            if (isAssignableFrom(class1, class2)) return type1;
            if (isAssignableFrom(class2, class1)) return type2;

            if (isInterface(class1) || isInterface(class2)) {
                return "java/lang/Object";
            }

            Set<String> superTypes = new HashSet<>();
            while (class1 != null) {
                superTypes.add(class1.name);
                class1 = DependencyAnalyzer.libraryClasses.get(class1.superName);
            }

            while (class2 != null) {
                if (superTypes.contains(class2.name)) {
                    return class2.name;
                }
                class2 = DependencyAnalyzer.libraryClasses.get(class2.superName);
            }

            return "java/lang/Object";
        }

        // Fallback to jvm classes
        try {
            ClassLoader classLoader = getClassLoader();

            Class<?> c1 = Class.forName(type1.replace('/', '.'), false, classLoader);
            Class<?> c2 = Class.forName(type2.replace('/', '.'), false, classLoader);

            if (c1.isAssignableFrom(c2)) return type1;
            if (c2.isAssignableFrom(c1)) return type2;

            if (c1.isInterface() || c2.isInterface()) return "java/lang/Object";

            do {
                c1 = c1.getSuperclass();
            } while (!c1.isAssignableFrom(c2));

            return c1.getName().replace('.', '/');
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found: " + type1 + " or " + type2);
            return "java/lang/Object";
        }
    }

    private boolean isAssignableFrom(ClassNode parent, ClassNode child) {
        if (parent.name.equals(child.name)) return true;

        Set<String> visited = new HashSet<>();
        var current = child;

        while (current != null && visited.add(current.name)) {
            if (parent.name.equals(current.superName)) return true;
            if (current.interfaces != null && current.interfaces.contains(parent.name)) return true;

            HashMap<String, ClassNode> classes = DependencyAnalyzer.libraryClasses;
            ClassPool.getClasses().forEach(c -> classes.put(c.name, c));
            current = classes.get(current.superName);
        }

        return false;
    }

    private boolean isInterface(ClassNode node) {
        return (node.access & Opcodes.ACC_INTERFACE) != 0;
    }
}
