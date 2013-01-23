package test.camel.support.zip;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;

import test.camel.support.ZipFileEntry;

public class ZipFileIterator implements Iterator<ZipFileEntry>, Closeable {

    private Iterator<ZipFileEntry> zipIterator;

    public ZipFileIterator(File file, String batchId) throws IOException {
        ZipArchiveInputStream zis = null;
        ArrayList<ZipFileEntry> zipEntries = new ArrayList<ZipFileEntry>();
        try {
            // Create input stream
            zis = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(file)));

            ArchiveEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    zipEntries.add(new ZipFileEntry(file.getAbsolutePath(), entry.getName()));
                }
            }
        } finally {
            IOUtils.closeQuietly(zis);
        }

        this.zipIterator = zipEntries.iterator();
    }

    public boolean hasNext() {
        return this.zipIterator.hasNext();
    }

    public ZipFileEntry next() {
        return this.zipIterator.next();
    }

    public void remove() {
        this.zipIterator.remove();
    }

    @Override
    public void close() throws IOException {
    }
}
