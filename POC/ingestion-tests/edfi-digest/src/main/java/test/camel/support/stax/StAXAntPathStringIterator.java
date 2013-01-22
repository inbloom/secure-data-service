package test.camel.support.stax;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;

public class StAXAntPathStringIterator extends StAXAntPathIterator<String> {

	public StAXAntPathStringIterator(XMLEventReader reader, String antPath, int group) {
		super(reader, antPath, group);
	}

	protected String grabContent() throws XMLStreamException {
		StringWriter out = new StringWriter();
		XMLEventWriter writer = null;

		try {
			writer = OUTPUT_FACTORY.createXMLEventWriter(out);

			final XMLEventWriter w = writer;

			boolean retrieved = retrieveContent(new ContentRetriever() {

				@Override
				public void retrieve() throws XMLStreamException {
					writeContent(w);
				}
			});

			if (!retrieved) {
				throw new IOException("No more elements");
			}

			return out.toString();
		} catch (IOException e) {
			return null;
		} finally {
			closeQuietly(writer);
			IOUtils.closeQuietly(out);
		}
	}
}