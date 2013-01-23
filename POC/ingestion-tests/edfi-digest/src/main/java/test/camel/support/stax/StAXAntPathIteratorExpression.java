package test.camel.support.stax;

import java.util.Iterator;

import javax.xml.stream.XMLEventReader;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.core.xml.scan.AntPathMatcher;
import org.apache.camel.support.ExpressionAdapter;

public class StAXAntPathIteratorExpression extends ExpressionAdapter {
	private String antPath;
	private int group;

	public StAXAntPathIteratorExpression(String antPath, int group) {
		this.antPath = antPath;
		this.group = group;

		if (!new AntPathMatcher().isPattern(antPath)) {
			throw new RuntimeCamelException(
					"Provided antPath argument is not a valid pattern");
		}
	}

	public Object evaluate(Exchange exchange) {
		try {
			XMLEventReader reader = exchange.getIn().getMandatoryBody(
					XMLEventReader.class);

			return createIterator(reader);
		} catch (InvalidPayloadException e) {
			exchange.setException(e);
			return null;
		}
	}

	private Iterator<?> createIterator(XMLEventReader reader) {
		return new StAXAntPathJAXBIterator(reader, antPath, group);
	}

}
