package org.slc.sli.dal.init;

import com.mongodb.CommandResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jstokes
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class AggregationLoaderTest {
    private static Logger log = LoggerFactory.getLogger(AggregationLoaderTest.class);

    @InjectMocks
    private AggregationLoader aggregationLoader = new AggregationLoader(); // class under test

    @Mock
    private MongoTemplate mongoTemplate;

    @Before
    public void setup() {

        // assume mongo template can do its job
        CommandResult cr = mock(CommandResult.class);
        when(mongoTemplate.executeCommand(anyString())).thenReturn(cr);
        when(cr.ok()).thenReturn(true);
    }

    @Test
    public void testInit() {
        // init method is called post construct - no errors and it loads the right files
        aggregationLoader.init();
    }

    @Test
    public void testLoadJavascriptFile() {
        aggregationLoader = new AggregationLoader();
        Boolean load = aggregationLoader.loadJavascriptFile("someFileThatDoesNotExist");
        assertFalse(load);
    }
}
