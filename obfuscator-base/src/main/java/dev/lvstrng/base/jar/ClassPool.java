package dev.lvstrng.base.jar;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lvstrng
 *
 * This class stores all the classes loaded from the input jar.
 * This will also contain artificial classes added by the base itself.
 */
public class ClassPool {
    private static int classFileMajor = -1;
    private static String mainClass;
    private static List<ClassNode> classes = new ArrayList<>();
    private static final List<ClassNode> artificials = new ArrayList<>();

    /**
     * @return Every class originally loaded from the input jar
     */
    public static List<ClassNode> getClasses() {
        return classes;
    }

    /**
     * @return Every class added to the jar by the base
     */
    public static List<ClassNode> getArtificials() {
        return artificials;
    }

    /**
     * Add normal class to the jar. Intended for use only by the {@link dev.lvstrng.base.Obfuscator} class
     */
    public static void add(ClassNode classNode) {
        classes.add(classNode);
    }

    public static int commonClassFileMajor() {
        return classFileMajor == -1 ? Opcodes.V24 : classFileMajor;
    }

    /**
     * Sets the common class file major version to maintain compatibility
     */
    public static void setClassFileMajor(int major) {
        classFileMajor = major;
    }

    /**
     * Adds a base-generated class to the artificials list
     */
    public static void addArtificial(ClassNode classNode) {
        artificials.add(classNode);
    }

    public static void setClasses(List<ClassNode> classList) {
        classes = classList;
    }

    /**
     * Set the main class name
     */
    public static void setMain(String name) {
        mainClass = name;
    }

    /**
     * Get the main class name
     */
    public static String getMain() {
        return mainClass;
    }

    /**
     * Returns all the class names in the jar
     */
    public static List<String> allClassesAsStrings() {
        var newList = new ArrayList<>(classes);
        newList.addAll(artificials);

        return newList.stream().map(e -> e.name).toList();
    }

    /**
     * Returns all the field names in the class
     */
    public static List<String> allFieldsAsStrings(ClassNode owner) {
        var list = new ArrayList<>(owner.fields);
        return list.stream().map(e -> e.name).toList();
    }

    /**
     * Returns all the method names in the class
     */
    public static List<String> allMethodsAsStrings(ClassNode owner) {
        var list = new ArrayList<>(owner.methods);
        return list.stream().map(e -> e.name).toList();
    }
}
