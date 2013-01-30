package test.camel.support.stax;

import javax.xml.stream.XMLEventReader;

import org.apache.camel.CamelExchangeException;
import org.apache.camel.Exchange;
import org.apache.camel.support.ExpressionAdapter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class StAXAntPathIteratorExpression extends ExpressionAdapter implements ApplicationContextAware {

    private ApplicationContext appContext;
    private String iteratorBeanId;

    public StAXAntPathIteratorExpression() {
    }

    public Object evaluate(Exchange exchange) {
        try {
            XMLEventReader reader = exchange.getIn().getMandatoryBody(XMLEventReader.class);

            return createIterator(reader);
        } catch (CamelExchangeException e) {
            exchange.setException(e);
            return null;
        }
    }

    private StAXAntPathIterator<?> createIterator(XMLEventReader reader) {
        StAXAntPathIterator<?> it = appContext.getBean(iteratorBeanId, StAXAntPathIterator.class);
        it.setReader(reader);

        return it;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }

    public void setIteratorBeanId(String iteratorBeanId) {
        this.iteratorBeanId = iteratorBeanId;
    }
}
