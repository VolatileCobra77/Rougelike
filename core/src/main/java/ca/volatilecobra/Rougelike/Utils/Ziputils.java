package ca.volatilecobra.Rougelike.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Ziputils {

    public static void unzip(Path zipFilePath, Path targetDir) throws IOException {
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path resolvedPath = targetDir.resolve(entry.getName()).normalize();

                if (!resolvedPath.startsWith(targetDir)) {
                    throw new IOException("Entry is outside the target dir: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(resolvedPath);
                } else {
                    Files.createDirectories(resolvedPath.getParent());
                    copyStream(zis, Files.newOutputStream(resolvedPath));
                }
                zis.closeEntry();
            }
        }
    }

    private static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int length;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        out.close();
    }
}
