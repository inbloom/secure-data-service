package test.camel.support.stax;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class StAXAntPathFileIterator extends StAXAntPathIterator<File> {

	private final File folder = null;
	private final String prefix = null;
	private final String suffix = null;

	public StAXAntPathFileIterator(XMLEventReader reader, String antPath, int group) {
		super(reader, antPath, group);
	}

	protected File grabContent() throws XMLStreamException {
		File snippet = null;

		OutputStream out = null;
		XMLEventWriter writer = null;

		try {
			snippet = File.createTempFile(prefix, suffix, folder);

			out = new BufferedOutputStream(new FileOutputStream(snippet));

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

			out.flush();
		} catch (IOException e) {
			closeQuietly(writer);
			IOUtils.closeQuietly(out);

			FileUtils.deleteQuietly(snippet);

			snippet = null;
		} finally {
			closeQuietly(writer);
			IOUtils.closeQuietly(out);
		}

		return snippet;
	}
}