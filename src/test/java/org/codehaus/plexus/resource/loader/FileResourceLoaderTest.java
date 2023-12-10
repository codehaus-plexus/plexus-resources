package org.codehaus.plexus.resource.loader;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.File;

import org.codehaus.plexus.resource.PlexusResource;
import org.codehaus.plexus.testing.PlexusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
@PlexusTest
class FileResourceLoaderTest extends AbstractResourceLoaderTest {
    @Test
    void testLookupWithAAbsolutePathName() throws Exception {
        assertResource("/dir/file.txt", "file.txt");
    }

    @Test
    void testLookupWithARelativePath() throws Exception {
        assertResource("dir/file.txt", "file.txt");
    }

    @Test
    void testLookupWhenTheResourceIsMissing() throws Exception {
        assertMissingResource("/foo.txt");

        assertMissingResource("foo.txt");
    }

    @Test
    void testPlexusResource() throws Exception {
        PlexusResource resource = resourceLoader.getResource("/dir/file.txt");
        final File f = new File("src/test/file-resources", "/dir/file.txt");
        assertEquals(f.getAbsolutePath(), resource.getFile().getPath());
        assertEquals(f.toURI(), resource.getURI());
        assertEquals(f.toURI().toURL(), resource.getURL());
        assertEquals(f.getAbsolutePath(), resource.getName());
    }
}
