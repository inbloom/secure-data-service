package test.camel.support.zip.nio;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StopWatch;

public class ZipFileIterator implements Iterator<InputStream>, Closeable {

    private ZipFile zipFile;
    private File unzipFolder;
    private Enumeration<ZipArchiveEntry> zipEntries;

    public ZipFileIterator(File file, String batchId) throws IOException {
        this.zipFile = new ZipFile(file);

        this.unzipFolder = new File(file.getParent(), batchId);
        FileUtils.forceMkdir(this.unzipFolder);

        zipEntries = this.zipFile.getEntries();
    }

    public boolean hasNext() {
        return zipEntries.hasMoreElements();
    }

    public InputStream next() {
        ZipArchiveEntry zipEntry = zipEntries.nextElement();

        StopWatch sw = new StopWatch(zipEntry.getName());

        InputStream feStream = null;

        try {
            return zipFile.getInputStream(zipEntry);
        } catch (IOException e) {
            IOUtils.closeQuietly(feStream);

            return null;
        } finally {
            IOUtils.closeQuietly(feStream);

            System.out.print(sw.prettyPrint());
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        zipFile.close();
    }

}