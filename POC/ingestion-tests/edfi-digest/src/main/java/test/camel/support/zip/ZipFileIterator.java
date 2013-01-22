package test.camel.support.zip;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import test.camel.support.ZipFileEntry;

public class ZipFileIterator implements Iterator<ZipFileEntry>, Closeable {

    private String zipFilePath;
    private ZipFile zipFile;
    private Enumeration<ZipArchiveEntry> zipEntries;

    public ZipFileIterator(File file, String batchId) throws IOException {
        this.zipFilePath = file.getAbsolutePath();
        this.zipFile = new ZipFile(file);
        zipEntries = this.zipFile.getEntries();
    }

    public boolean hasNext() {
        return zipEntries.hasMoreElements();
    }

    public ZipFileEntry next() {
        ZipArchiveEntry zipEntry = zipEntries.nextElement();

        return new ZipFileEntry(zipFilePath, zipEntry.getName());
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        zipFile.close();
    }
}
