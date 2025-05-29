package dev.lvstrng.base.utils;

import org.objectweb.asm.ClassWriter;

/**
 * @author lvstrng
 * tries to fix the "Type not present" errors
 */
public class CustomClassWriter extends ClassWriter {
    public CustomClassWriter(int flags) {
        super(flags);
    }

    @Override
    public String getCommonSuperClass(String type1, String type2) {
        var classLoader = getClassLoader();
        Class<?> class1;
        try {
            class1 = Class.forName(type1.replace('/', '.'), false, classLoader);
        } catch (ClassNotFoundException e) {
            class1 = Object.class;
        }

        Class<?> class2;
        try {
            class2 = Class.forName(type2.replace('/', '.'), false, classLoader);
        } catch (ClassNotFoundException e) {
            class2 = Object.class;
        }

        if (class1.isAssignableFrom(class2)) {
            return type1;
        }

        if (class2.isAssignableFrom(class1)) {
            return type2;
        }

        if (class1.isInterface() || class2.isInterface()) {
            return "java/lang/Object";
        } else {
            do {
                class1 = class1.getSuperclass();
            } while (!class1.isAssignableFrom(class2));

            return class1.getName().replace('.', '/');
        }
    }
}

