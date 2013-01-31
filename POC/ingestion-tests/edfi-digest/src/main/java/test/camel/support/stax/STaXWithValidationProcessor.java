package test.camel.support.stax;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.util.EventReaderDelegate;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.springframework.core.io.Resource;

public class STaXWithValidationProcessor extends EventReaderDelegate implements Processor, ObjectReadyNotifier {
    private Resource schema;
    private int group;
    private ProducerTemplate producer;
    private ThreadLocal<List<Object>> batch = new ThreadLocal<List<Object>>();
    private ThreadLocal<Exchange> originalMessage = new ThreadLocal<Exchange>();

    public STaXWithValidationProcessor() {
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        originalMessage.set(exchange);
        batch.set(new ArrayList<Object>());

        XMLEventReader reader = exchange.getIn().getMandatoryBody(XMLEventReader.class);

        XmlParser.parse(reader, getSchema(), this);

        if (batch.get().size() > 0) {
            producer.sendBodyAndHeaders(batch.get(), originalMessage.get().getIn().getHeaders());
        }
    }

    @Override
    public void objectIsReady(Map<String, Object> object) {
        batch.get().add(object);

        if (batch.get().size() >= group) {
            producer.sendBodyAndHeaders(batch.get(), originalMessage.get().getIn().getHeaders());
            batch.set(new ArrayList<Object>());
        }
    }

    public Resource getSchema() {
        return schema;
    }

    public void setSchema(Resource schema) {
        this.schema = schema;
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
