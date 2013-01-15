package org.slc.sli.ingestion.test;

import java.io.FileReader;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StopWatch;

import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.streaming.StreamingLoader;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.EntityValidator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/ctx.xml", "/spring/db.xml" })
public class StreamingLoaderTest {

	// public static final Logger LOG = LoggerFactory.getLogger(StreamingLoaderTest.class);

	@Resource(name = "mongoEntityRepository")
	private MongoEntityRepository repo;

	@Resource
	private StreamingLoader sp;

	@Before
	public void init() {
		repo.setValidator(new EntityValidator() {

			@Override
			public boolean validateNaturalKeys(Entity entity, boolean fullOverwrite) throws EntityValidationException {
				return false;
			}

			@Override
			public boolean validate(Entity entity) throws EntityValidationException {
				return true;
			}

			@Override
			public void setReferenceCheck(String referenceCheck) {
			}

			@Override
			public boolean validatePresent(Entity entity) throws EntityValidationException {
				return false;
			}
		});
	}

	@Test
	public void parseSmallData() throws Exception {
		// Warm up
		sp.process(new FileReader("src/test/resources/xml/small/InterchangeSection.xml"));
		StopWatch sw = new StopWatch();
		sw.start();
		sp.process(new FileReader("src/test/resources/xml/small/InterchangeSectionBig.xml"));
		sw.stop();
		// LOG.info("Total time: "+sw.getTotalTimeMillis());
	}

	//@Test
	public void testParsing() throws Throwable {
		StopWatch sw = new StopWatch();
		sw.start();
		sp.process(new FileReader("src/test/resources/xml/interchange.xml"));
		sw.stop();

		// LOG.info(""+sw.getTotalTimeMillis());

		sw = new StopWatch();
		sw.start();
		sp.process(new FileReader("src/test/resources/xml/interchange.xml"));
		sw.stop();

		// LOG.info(""+sw.getTotalTimeMillis());

		sw = new StopWatch();
		sw.start();
		sp.process(new FileReader("src/test/resources/xml/interchange.xml"));
		sw.stop();

		// LOG.info(""+sw.getTotalTimeMillis());
	}
}
