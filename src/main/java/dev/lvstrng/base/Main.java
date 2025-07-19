package dev.lvstrng.base;

import dev.lvstrng.base.transform.impl.ExampleTransformer;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        DependencyAnalyzer.analyzeDependencies(new File("libs"));
        var obfuscator = new Obfuscator("in.jar", true);
        obfuscator.read();

        obfuscator.obfuscate(new ExampleTransformer());
        obfuscator.save("out.jar");
    }
}
