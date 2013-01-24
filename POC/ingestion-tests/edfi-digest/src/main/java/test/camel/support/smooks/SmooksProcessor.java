package test.camel.support.smooks;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.delivery.sax.SAXVisitAfter;

public class SmooksProcessor implements Processor, SAXVisitAfter {
    private Smooks smooks;
    private int group;
    private ProducerTemplate producer;
    private ThreadLocal<List<Object>> batch = new ThreadLocal<List<Object>>();
    private ThreadLocal<Exchange> originalMessage = new ThreadLocal<Exchange>();

    public SmooksProcessor() {
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        originalMessage.set(exchange);
        batch.set(new ArrayList<Object>());

        smooks.filterSource(new StreamSource(exchange.getIn().getMandatoryBody(InputStream.class)));

        if (batch.get().size() > 0) {
            producer.sendBodyAndHeaders(batch.get(), originalMessage.get().getIn().getHeaders());
        }
    }

    @Override
    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws SmooksException, IOException {
        Object record = executionContext.getBeanContext().getBean("record");

        batch.get().add(record);

        if (batch.get().size() >= group) {
            producer.sendBodyAndHeaders(batch.get(), originalMessage.get().getIn().getHeaders());
            batch.set(new ArrayList<Object>());
        }
    }

    public Smooks getSmooks() {
        return smooks;
    }

    public void setSmooks(Smooks smooks) {
        this.smooks = smooks;
        smooks.addVisitor(this, "/*/*");
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public ProducerTemplate getProducer() {
        return producer;
    }

    public void setProducer(ProducerTemplate producer) {
        this.producer = producer;
    }
}
