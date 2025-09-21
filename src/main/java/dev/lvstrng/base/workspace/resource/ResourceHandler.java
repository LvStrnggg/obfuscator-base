package dev.lvstrng.base.workspace.resource;

import dev.lvstrng.base.utils.ZipUtils;
import dev.lvstrng.base.workspace.Workspace;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

public class ResourceHandler {
    private final Workspace workspace;
    private final Map<String, byte[]> resources;

    private final Map<String, HandledResource> handledResources = Map.of();

    public ResourceHandler(Workspace workspace) {
        this.workspace = workspace;
        this.resources = new HashMap<>();
    }

    public void writeToOutput(ZipOutputStream zos) throws IOException {
        for(var resource : resources.entrySet()) {
            var name = resource.getKey();
            var bytes = resource.getValue();

            if(!handledResources.containsKey(name)) {
                ZipUtils.writeEntry(zos, bytes, name);
                continue;
            }

            handledResources.get(name).handle(workspace, name, bytes, zos);
        }
    }

    public void addResource(String name, byte[] bytes) {
        resources.put(name, bytes);
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public Map<String, byte[]> getResources() {
        return resources;
    }
}
