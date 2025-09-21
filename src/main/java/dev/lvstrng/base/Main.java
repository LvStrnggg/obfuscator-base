package dev.lvstrng.base;

import dev.lvstrng.base.transform.impl.optimize.DeadCodeCleanTransformer;
import dev.lvstrng.base.workspace.Workspace;

public class Main {
    public static void main(String[] args) {
        var workspace = new Workspace("in.jar", "libs/", true);
        workspace.readInput();
        workspace.transform(
                new DeadCodeCleanTransformer()
        );
        workspace.exportJar("out.jar");
    }
}
