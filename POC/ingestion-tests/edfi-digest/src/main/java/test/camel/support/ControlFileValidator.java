package test.camel.support;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.support.ExpressionAdapter;

public class ControlFileValidator extends ExpressionAdapter {

	public Object evaluate(Exchange exchange) {

		try {
			File file = exchange.getIn().getMandatoryBody(File.class);

			return file.isFile() && file.getName().toLowerCase().endsWith(".ctl");
		} catch (InvalidPayloadException e) {
			exchange.setException(e);
			return Boolean.FALSE;
		}
	}

}
