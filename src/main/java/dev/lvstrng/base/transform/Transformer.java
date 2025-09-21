package dev.lvstrng.base.transform;

import dev.lvstrng.base.workspace.Workspace;
import org.objectweb.asm.Opcodes;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Every transformer should extend this.
 */
public abstract class Transformer implements Opcodes {
    public final SecureRandom random = new SecureRandom();

    /**
     * @param workspace Current workspace. All classes, libraries and resources provided.
     */
    public abstract void transform(Workspace workspace);
}
