package dev.lvstrng.base;

import dev.lvstrng.base.transform.impl.ExampleTransformer;

public class Main {
    public static void main(String[] args) {
        var obfuscator = new Obfuscator("in.jar", true);
        obfuscator.read();

        obfuscator.obfuscate(new ExampleTransformer());
        obfuscator.save("out.jar");
    }
}
