package dev.lvstrng.base.workspace.resource;

import dev.lvstrng.base.workspace.Workspace;

import java.util.zip.ZipOutputStream;

/**
 * Allows for handling of certain resources differently. For example MANIFEST.MF after renaming classes,
 * fabric.mod.json, etc. etc.
 */
public interface HandledResource {
    void handle(Workspace workspace, String name, byte[] bytes, ZipOutputStream zos);
}
