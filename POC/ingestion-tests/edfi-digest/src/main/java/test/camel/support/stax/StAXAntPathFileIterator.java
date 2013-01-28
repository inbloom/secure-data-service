package test.camel.support.stax;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class StAXAntPathFileIterator extends StAXAntPathIterator<File> {

    private File folder;
    private String prefix;
    private String suffix;

    public StAXAntPathFileIterator() {
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

    /**
     * @return the folder
     */
    public File getFolder() {
        return folder;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @return the suffix
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * @param folder the folder to set
     */
    public void setFolder(File folder) {
        this.folder = folder;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @param suffix the suffix to set
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
