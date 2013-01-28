package test.camel.support.stax;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;

public class StAXAntPathBytesIterator extends StAXAntPathIterator<OutputStream> {

    public StAXAntPathBytesIterator() {
    }

    protected OutputStream grabContent() throws XMLStreamException {
        ByteArrayOutputStream out = null;
        XMLEventWriter writer = null;

        try {
            out = new ByteArrayOutputStream();

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

            return out;
        } catch (IOException e) {
            closeQuietly(writer);
            IOUtils.closeQuietly(out);

            return null;
        } finally {
            closeQuietly(writer);
            // IOUtils.closeQuietly(out);
        }
    }
}
