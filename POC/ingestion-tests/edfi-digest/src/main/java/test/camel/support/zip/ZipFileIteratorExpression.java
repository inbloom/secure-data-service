package test.camel.support.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.support.ExpressionAdapter;

public class ZipFileIteratorExpression extends ExpressionAdapter {

	public ZipFileIteratorExpression() {
	}

	public Object evaluate(Exchange exchange) {
		try {
			String batchId = exchange.getIn().getHeader("batchId", String.class);
			File zipFile = exchange.getIn().getMandatoryBody(File.class);

			return createIterator(zipFile, batchId);
		} catch (InvalidPayloadException e) {
			exchange.setException(e);
			return null;
		} catch (IOException e) {
			exchange.setException(e);
			return null;
		}
	}

	private Iterator<?> createIterator(File zipFile, String batchId) throws IOException {
		return new ZipFileIterator(zipFile, batchId);
	}

}
