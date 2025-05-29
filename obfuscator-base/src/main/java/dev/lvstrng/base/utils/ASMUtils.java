package dev.lvstrng.base.utils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public class ASMUtils implements Opcodes {
    public static AbstractInsnNode pushString(String value) {
        return new LdcInsnNode(value);
    }

    public static AbstractInsnNode pushFloat(float value) {
        if (value == 0.0f || value == 1.0f || value == 2.0f) {
            return new InsnNode(FCONST_0 + (int) value);
        } else {
            return new LdcInsnNode(value);
        }
    }

    public static AbstractInsnNode pushDouble(double value) {
        if (value == 0.0 || value == 1.0) {
            return new InsnNode(DCONST_0 + (int) value);
        } else {
            return new LdcInsnNode(value);
        }
    }

    public static AbstractInsnNode pushInt(int value) {
        if (value >= -1 && value <= 5) {
            return new InsnNode(ICONST_0 + value);
        } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            return new IntInsnNode(BIPUSH, value);
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            return new IntInsnNode(SIPUSH, value);
        } else {
            return new LdcInsnNode(value);
        }
    }

    public static AbstractInsnNode pushLong(long value) {
        if (value == 0 || value == 1) {
            return new InsnNode(LCONST_0 + (int) value);
        } else {
            return new LdcInsnNode(value);
        }
    }
}
