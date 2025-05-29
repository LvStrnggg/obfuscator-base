package dev.lvstrng.base.transform.impl;

import dev.lvstrng.base.jar.ClassPool;
import dev.lvstrng.base.transform.Transformer;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

/**
 * @author lvstrng
 * @implNote This itself serves no real purpose, but it's here just as an example for people who don't know anything about java bytecode or are new to the base.
 *
 * Converts an int push instruction into an LDC
 */
public class ExampleTransformer extends Transformer {

    @Override
    public void transform() {
        // Iterate through every class in the input JAR
        for(var classNode : ClassPool.getClasses()) {

            // Iterate through every method in the class
            for(var method : classNode.methods) {

                // Iterate through every instruction in the method
                for(var insn : method.instructions) {
                    // Check if the instruction node is an int push (bipush/sipush)
                    // This instruction can also be the creation of a new primitive type array
                    if(insn instanceof IntInsnNode intPush) {
                        if(intPush.getOpcode() == NEWARRAY)
                            continue; // We don't want to mess up arrays, so let's not do that with this line

                        // Set the instruction to a new LDC instruction with the value of the previous instruction
                        method.instructions.insertBefore(intPush, new LdcInsnNode(intPush.operand));
                        method.instructions.remove(intPush);

                        // insertBefore(AbstractInsnNode, ...) inserts either an instruction or a whole InsnList before said instruction
                        // insert(AbstractInsnNode) inserts an instruction at the start of the code
                        // insert(AbstractInsnNode, ...) inserts an instruction/InsnList after said instruction
                        // add(...) adds an instruction/InsnList at the end of the instruction list
                    }
                }
            }
        }
    }
}
