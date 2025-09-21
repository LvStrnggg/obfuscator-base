package dev.lvstrng.base.utils;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    public static void writeEntry(ZipOutputStream zipOut, byte[] data, String name) throws IOException {
        if(data == null)
            return;

        zipOut.putNextEntry(new ZipEntry(name));
        zipOut.write(data);
        zipOut.closeEntry();
    }
}
