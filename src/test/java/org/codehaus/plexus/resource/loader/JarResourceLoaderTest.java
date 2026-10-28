package org.codehaus.plexus.resource.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.codehaus.plexus.resource.PlexusResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JarResourceLoaderTest {
    @TempDir
    Path tempDir;

    @Test
    void readsRewrittenJar() throws Exception {
        Path jar = tempDir.resolve("resources.jar");
        writeJar(jar, "first");
        assertEquals("first", read(jar));

        // Rewritten in place, as a reactor module or a reinstalled artifact is.
        writeJar(jar, "second, and longer than the first");
        assertEquals("second, and longer than the first", read(jar));

        // The jar is not held open, so it can be deleted (fails on Windows while it is).
        Files.delete(jar);
    }

    @Test
    void urlPointsIntoJar() throws Exception {
        Path jar = tempDir.resolve("resources.jar");
        writeJar(jar, "content");
        assertEquals(
                "jar:" + jar.toUri() + "!/config/rules.txt",
                resource(jar).getURL().toString());
    }

    private static PlexusResource resource(Path jar) throws Exception {
        JarResourceLoader loader = new JarResourceLoader();
        loader.addSearchPath("jar:" + jar.toUri());
        return loader.getResource("config/rules.txt");
    }

    private static String read(Path jar) throws Exception {
        PlexusResource resource = resource(jar);
        try (InputStream in = resource.getInputStream()) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] buffer = new byte[256];
            for (int n; (n = in.read(buffer)) != -1; ) {
                bytes.write(buffer, 0, n);
            }
            return new String(bytes.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private static void writeJar(Path jar, String content) throws IOException {
        try (OutputStream out = Files.newOutputStream(jar);
                JarOutputStream jarOut = new JarOutputStream(out)) {
            jarOut.putNextEntry(new ZipEntry("config/rules.txt"));
            jarOut.write(content.getBytes(StandardCharsets.UTF_8));
            jarOut.closeEntry();
        }
    }
}
