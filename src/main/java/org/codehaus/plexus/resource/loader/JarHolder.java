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
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.codehaus.plexus.resource.PlexusResource;

/**
 * A small wrapper around a Jar
 *
 * @author <a href="mailto:daveb@miceda-data.com">Dave Bryson</a>
 * @author Jason van Zyl
 * @version $Id$
 */
public class JarHolder {
    private final String urlpath;

    private final Set<String> entries;

    public JarHolder(String urlpath) {
        this.urlpath = urlpath;

        Set<String> names = Collections.emptySet();
        // The jar is indexed once and every resource is read from a connection of its own, all without the
        // JVM-wide jar: cache: a cached JarFile is never closed, so the jar stays locked on Windows and is read
        // stale once it is rewritten (JDK-8239054).
        try (JarFile jar = openJar(new URL(urlpath))) {
            names = new LinkedHashSet<>();
            Enumeration<JarEntry> all = jar.entries();
            while (all.hasMoreElements()) {
                JarEntry je = all.nextElement();

                // We don't map plain directory entries
                if (!je.isDirectory()) {
                    names.add(je.getName());
                }
            }
        } catch (IOException ioe) {
        }
        this.entries = names;
    }

    private static JarFile openJar(URL url) throws IOException {
        JarURLConnection conn = (JarURLConnection) url.openConnection();
        conn.setUseCaches(false);
        return conn.getJarFile();
    }

    private InputStream openEntry(String name) throws IOException {
        URLConnection conn = new URL(urlpath + name).openConnection();
        conn.setUseCaches(false);
        return conn.getInputStream();
    }

    /**
     * Kept for compatibility: the jar is no longer held open, so there is nothing to close.
     */
    public void close() {}

    public InputStream getResource(String theentry) throws ResourceNotFoundException {
        if (!entries.contains(theentry)) {
            return null;
        }
        try {
            return openEntry(theentry);
        } catch (Exception fnfe) {
            throw new ResourceNotFoundException(fnfe.getMessage());
        }
    }

    public Hashtable<String, String> getEntries() {
        Hashtable<String, String> allEntries = new Hashtable<>(559);
        for (String name : entries) {
            allEntries.put(name, this.urlpath);
        }
        return allEntries;
    }

    public String getUrlPath() {
        return urlpath;
    }

    public PlexusResource getPlexusResource(final String name) {
        if (!entries.contains(name)) {
            return null;
        }
        return new PlexusResource() {
            @Override
            public File getFile() {
                return null;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return openEntry(name);
            }

            @Override
            public String getName() {
                return urlpath + name;
            }

            @Override
            public URI getURI() {
                return null;
            }

            @Override
            public URL getURL() throws IOException {
                return new URL(urlpath + name);
            }
        };
    }
}
