package org.codehaus.plexus.resource.loader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class URLResourceLoaderTest {
    @Mock
    private Logger logger;

    @InjectMocks
    private ResourceLoader resourceLoader = new URLResourceLoader();

    @Test
    void testMalformedURL() {
        try {
            resourceLoader.getResource("LICENSE.txt");
            fail();
        } catch (ResourceNotFoundException e) {
            verify(logger).debug("URLResourceLoader: No valid URL '{}'", "LICENSE.txt");
            verifyNoMoreInteractions(logger);
            assertEquals("Could not find resource 'LICENSE.txt'.", e.getMessage());
        }
    }
}
