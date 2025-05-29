package dev.lvstrng.base.transform;

import org.objectweb.asm.Opcodes;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Every transformer should extend this.
 */
public abstract class Transformer implements Opcodes {
    public final ThreadLocalRandom random = ThreadLocalRandom.current();

    public abstract void transform();
}
