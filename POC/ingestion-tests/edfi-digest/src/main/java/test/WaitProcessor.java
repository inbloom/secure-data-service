package test;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class WaitProcessor implements Processor {

	@Override
	public void process(Exchange arg0) throws Exception {
		Thread.sleep(20000);
	}

}
