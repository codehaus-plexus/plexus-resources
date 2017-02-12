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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.resource.PlexusResource;

/**
 * @author Jason van Zyl
 */
@Component( role = ResourceLoader.class, hint = "url", instantiationStrategy = "per-lookup" )
public class URLResourceLoader
    extends AbstractResourceLoader
{

    public static final String ID = "url";

    protected Map<String, String> templateRoots = new HashMap<String, String>();

    /**
     * Get an InputStream so that the Runtime can build a template with it.
     * 
     * @param name name of template to fetch bytestream of
     * @return InputStream containing the template
     * @throws ResourceNotFoundException if template not found in the file template path.
     */
    public PlexusResource getResource( String name )
        throws ResourceNotFoundException
    {
        if ( name == null || name.length() == 0 )
        {
            throw new ResourceNotFoundException( "URLResourceLoader : No template name provided" );
        }

        for ( String path : paths )
        {
            try
            {
                URL u = new URL( path + name );

                final InputStream inputStream = u.openStream();

                if ( inputStream != null )
                {
                    if ( getLogger().isDebugEnabled() )
                    {
                        getLogger().debug( "URLResourceLoader: Found '" + name + "' at '" + path + "'" );
                    }

                    // save this root for later re-use
                    templateRoots.put( name, path );

                    return new URLPlexusResource( u )
                    {
                        private boolean useSuper;

                        public synchronized InputStream getInputStream()
                            throws IOException
                        {
                            if ( !useSuper )
                            {
                                useSuper = true;
                                return inputStream;
                            }
                            return super.getInputStream();
                        }
                    };
                }
            }
            catch( MalformedURLException mue )
            {
                if ( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "URLResourceLoader: No valid URL '" + path + name + '\'' );
                }
            }
            catch ( IOException ioe )
            {
                if ( getLogger().isDebugEnabled() )
                {
                    getLogger().debug(
                                       "URLResourceLoader: Exception when looking for '" + name + "' at '" + path + "'",
                                       ioe );
                }
            }
        }
        
        // here we try to download without any path just the name which can be an url
        try
        {
            URL u = new URL( name );

            final InputStream inputStream = u.openStream();

            if ( inputStream != null )
            {
                return new URLPlexusResource( u )
                {
                    private boolean useSuper;

                    public synchronized InputStream getInputStream()
                        throws IOException
                    {
                        if ( !useSuper )
                        {
                            useSuper = true;
                            return inputStream;
                        }
                        return super.getInputStream();
                    }
                };
            }
        }
        catch( MalformedURLException mue )
        {
            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "URLResourceLoader: No valid URL '" + name + '\'' );
            }
        }
        catch ( IOException ioe )
        {
            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "URLResourceLoader: Exception when looking for '" + name + '\'', ioe );
            }
       }

        // convert to a general Velocity ResourceNotFoundException
        throw new ResourceNotFoundException( name );
    }
}
