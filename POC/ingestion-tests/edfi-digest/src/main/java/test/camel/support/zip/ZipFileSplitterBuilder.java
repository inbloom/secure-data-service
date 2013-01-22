package test.camel.support.zip;

import org.apache.camel.Expression;

public final class ZipFileSplitterBuilder {

	private ZipFileSplitterBuilder() {
	}

	public static Expression unzip() {
		return new ZipFileIteratorExpression();
	}
}
