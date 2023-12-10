package org.codehaus.plexus.resource;

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

import javax.inject.Inject;
import javax.inject.Named;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.codehaus.plexus.resource.loader.FileResourceCreationException;
import org.codehaus.plexus.resource.loader.ResourceIOException;
import org.codehaus.plexus.resource.loader.ResourceLoader;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 */
@Named
public class DefaultResourceManager implements ResourceManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultResourceManager.class);

    private final Map<String, ResourceLoader> resourceLoaders;

    private File outputDirectory;

    @Inject
    public DefaultResourceManager(Map<String, ResourceLoader> resourceLoaders) {
        this.resourceLoaders = resourceLoaders;
    }

    // ----------------------------------------------------------------------
    // ResourceManager Implementation
    // ----------------------------------------------------------------------

    @Override
    public InputStream getResourceAsInputStream(String name) throws ResourceNotFoundException {
        PlexusResource resource = getResource(name);
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            throw new ResourceIOException("Failed to open resource " + resource.getName() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public File getResourceAsFile(String name) throws ResourceNotFoundException, FileResourceCreationException {
        return getResourceAsFile(getResource(name));
    }

    @Override
    public File getResourceAsFile(String name, String outputPath)
            throws ResourceNotFoundException, FileResourceCreationException {
        if (outputPath == null) {
            return getResourceAsFile(name);
        }
        PlexusResource resource = getResource(name);
        File outputFile;
        if (outputDirectory != null) {
            outputFile = new File(outputDirectory, outputPath);
        } else {
            outputFile = new File(outputPath);
        }
        createResourceAsFile(resource, outputFile);
        return outputFile;
    }

    @Override
    public File resolveLocation(String name, String outputPath) {
        // Honour what the original locator does and return null ...
        try {
            return getResourceAsFile(name, outputPath);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public File resolveLocation(String name) {
        // Honour what the original locator does and return null ...
        try {
            return getResourceAsFile(name);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void addSearchPath(String id, String path) {
        ResourceLoader loader = resourceLoaders.get(id);

        if (loader == null) {
            throw new IllegalArgumentException("unknown resource loader: " + id);
        }

        loader.addSearchPath(path);
    }

    @Override
    public PlexusResource getResource(String name) throws ResourceNotFoundException {
        for (ResourceLoader resourceLoader : resourceLoaders.values()) {
            try {
                PlexusResource resource = resourceLoader.getResource(name);

                LOGGER.debug("The resource '{}' was found as '{}'", name, resource.getName());

                return resource;
            } catch (ResourceNotFoundException e) {
                LOGGER.debug(
                        "The resource '{}' was not found with resourceLoader '{}'",
                        name,
                        resourceLoader.getClass().getName());
            }
        }

        throw new ResourceNotFoundException(name);
    }

    @Override
    public File getResourceAsFile(PlexusResource resource) throws FileResourceCreationException {
        try {
            File f = resource.getFile();
            if (f != null) {
                return f;
            }
        } catch (IOException e) {
            // Ignore this, try to make use of resource.getInputStream().
        }

        final File outputFile = FileUtils.createTempFile("plexus-resources", "tmp", outputDirectory);
        outputFile.deleteOnExit();
        createResourceAsFile(resource, outputFile);
        return outputFile;
    }

    @Override
    public void createResourceAsFile(PlexusResource resource, File outputFile) throws FileResourceCreationException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = resource.getInputStream();
            File dir = outputFile.getParentFile();
            if (!dir.isDirectory() && !dir.mkdirs()) {
                throw new FileResourceCreationException("Failed to create directory " + dir.getPath());
            }
            os = new FileOutputStream(outputFile);
            IOUtil.copy(is, os);
            is.close();
            is = null;
            os.close();
            os = null;
        } catch (IOException e) {
            throw new FileResourceCreationException("Cannot create file-based resource:" + e.getMessage(), e);
        } finally {
            IOUtil.close(is);
            IOUtil.close(os);
        }
    }
}
