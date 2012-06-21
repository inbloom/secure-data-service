/**
 *
 */
package org.slc.sli.ingestion.xml.idref;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import com.google.common.io.LimitInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author okrook
 *
 */
public final class SnippetFile implements Closeable {
    public static final Logger LOG = LoggerFactory.getLogger(SnippetFile.class);

    private File snippet;
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    private Map<String, SnippetBlock> snippets;

    /**
     * @author okrook
     *
     */
    class SnippetBlock {
        protected long position;
        protected long size;
    }

    private SnippetFile() {
    }

    public static SnippetFile create(String prefix, String suffix, File directory) throws IOException {
        SnippetFile inst = new SnippetFile();

        inst.snippet = File.createTempFile(prefix, suffix, directory);
        inst.raf = new RandomAccessFile(inst.snippet, "rw");
        inst.fileChannel = inst.raf.getChannel();
        inst.fileChannel.position(0);
        inst.snippets = new HashMap<String, SnippetBlock>();

        return inst;
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(raf);

        raf = null;
        fileChannel = null;
    }

    public void delete() {
        close();

        FileUtils.deleteQuietly(snippet);

        snippet = null;
    }

    public boolean contains(String id) {
        return snippets.containsKey(id);
    }

    public InputStream get(String id) throws IOException {
        if (!contains(id) || snippets.get(id).size == 0) {
            return null;
        }

        fileChannel.position(snippets.get(id).position);

        //LOG.debug(String.format("Input %s: ch - %d, p - %d. Size: %d. ID: %s", snippet.getName(), fileChannel.size(), fileChannel.position(), snippets.get(id).size, id));

        return new LimitInputStream(new NonClosingInputStream(Channels.newInputStream(fileChannel)), snippets.get(id).size);
    }

    public OutputStream allocateOutputStream(String id) throws IOException {
        SnippetBlock sb = new SnippetBlock();
        sb.position = fileChannel.size();
        fileChannel.position(sb.position); // sync up the file position with the channel size
        snippets.put(id, sb);

        //LOG.debug(String.format("Output %s: ch - %d, p - %d. Size: %d. ID: %s", snippet.getName(), fileChannel.size(), fileChannel.position(), snippets.get(id).size, id));

        return new NonClosingOutputStream(Channels.newOutputStream(fileChannel));
    }

    public void commitSnippet(String id) throws IOException {
        snippets.get(id).size = fileChannel.size() - snippets.get(id).position;

        //LOG.debug(String.format("Commit %s: ch - %d, p - %d. Size: %d. ID: %s", snippet.getName(), fileChannel.size(), fileChannel.position(), snippets.get(id).size, id));
    }
}
