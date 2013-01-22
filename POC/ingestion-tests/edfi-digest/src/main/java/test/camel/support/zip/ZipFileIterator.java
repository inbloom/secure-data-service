package test.camel.support.zip;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.Iterator;

import org.apache.camel.component.file.FileConsumer;
import org.apache.camel.component.file.GenericFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StopWatch;

public class ZipFileIterator implements Iterator<GenericFile<File>>, Closeable {

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

	public GenericFile<File> next() {
		ZipArchiveEntry zipEntry = zipEntries.nextElement();

		StopWatch sw = new StopWatch(zipEntry.getName());

		File fe = new File(unzipFolder, zipEntry.getName());

		InputStream feStream = null;
		RandomAccessFile raf = null;

		try {
			sw.start("Creating a file");
			fe.createNewFile();
			(raf = new RandomAccessFile(fe, "rw")).setLength(zipEntry.getSize());
			sw.stop();

			feStream = zipFile.getInputStream(zipEntry);

			sw.start("Extracting");
			FileUtils.copyInputStreamToFile(feStream, fe);
			sw.stop();
			
			return null;

			//return FileConsumer.asGenericFile(unzipFolder.getPath(), fe);
		} catch (IOException e) {
			IOUtils.closeQuietly(feStream);

			IOUtils.closeQuietly(raf);

			FileUtils.deleteQuietly(fe);

			return null;
		} finally {
			IOUtils.closeQuietly(feStream);
			
			IOUtils.closeQuietly(raf);

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