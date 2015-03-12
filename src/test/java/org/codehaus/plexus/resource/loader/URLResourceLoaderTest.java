package org.codehaus.plexus.resource.loader;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import junit.framework.TestCase;

import org.codehaus.plexus.logging.Logger;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class URLResourceLoaderTest
    extends TestCase
{
    @Mock
    private Logger logger;

    @InjectMocks
    private ResourceLoader resourceLoader = new URLResourceLoader();

    @Override
    protected void setUp()
        throws Exception
    {
        MockitoAnnotations.initMocks( this );
    }

    public void testMalformedURL()
        throws Exception
    {
        when( logger.isDebugEnabled() ).thenReturn( true );

        try
        {
            resourceLoader.getResource( "LICENSE.txt" );
            fail();
        }
        catch ( ResourceNotFoundException e )
        {
            verify( logger ).isDebugEnabled();
            verify( logger ).debug( "URLResourceLoader: No valid URL 'LICENSE.txt'" );
            verifyNoMoreInteractions( logger );
            assertEquals( "Could not find resource 'LICENSE.txt'.", e.getMessage() );
        }

    }
}
