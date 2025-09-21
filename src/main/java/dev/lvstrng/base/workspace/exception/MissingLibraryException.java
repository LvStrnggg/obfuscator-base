package dev.lvstrng.base.workspace.exception;

public class MissingLibraryException extends RuntimeException {
    public MissingLibraryException(String className) {
        super("Missing class '" + className + "' from classpath, install correct libraries to continue.");
    }
}

