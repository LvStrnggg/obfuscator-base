package dev.lvstrng.base.transform.impl.optimize;

import dev.lvstrng.base.transform.Transformer;
import dev.lvstrng.base.workspace.Workspace;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;

import java.util.ArrayList;

/**
 * @author lvstrng
 * Example transformer. Removes unreachable parts of code.
 */
public class DeadCodeCleanTransformer extends Transformer {
    @Override
    public void transform(Workspace workspace) {
        workspace.getClasses().forEach(classNode -> classNode.methods.forEach(method -> {
            try {
                var frames = new Analyzer<>(new BasicInterpreter()).analyzeAndComputeMaxs(classNode.name, method);

                var toRemove = new ArrayList<AbstractInsnNode>();
                for(int i = 0; i < method.instructions.size(); i++) {
                    if(frames[i] != null) //code is not dead, continue
                        continue;

                    //dead code found, add to remove list
                    toRemove.add(method.instructions.get(i));
                }

                toRemove.forEach(method.instructions::remove);
                method.localVariables.clear(); //cleaner also deletes labels 🙄
            } catch (AnalyzerException _) {}
        }));
    }
}
