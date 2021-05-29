package org.codehaus.plexus.resource.loader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;


@RunWith( MockitoJUnitRunner.class )
public class URLResourceLoaderTest
{
    @Mock
    private Logger logger;

    @InjectMocks
    private ResourceLoader resourceLoader = new URLResourceLoader();

    @Test
    public void testMalformedURL()
    {
        try
        {
            resourceLoader.getResource( "LICENSE.txt" );
            fail();
        }
        catch ( ResourceNotFoundException e )
        {
            verify( logger ).debug( "URLResourceLoader: No valid URL '{}'", "LICENSE.txt" );
            verifyNoMoreInteractions( logger );
            assertEquals( "Could not find resource 'LICENSE.txt'.", e.getMessage() );
        }

    }
}
