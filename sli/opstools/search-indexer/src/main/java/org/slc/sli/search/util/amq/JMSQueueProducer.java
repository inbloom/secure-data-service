package org.slc.sli.search.util.amq;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

/**
 * ActiveMQ JMS Queue Publisher
 * @author tosako
 *
 */
public class JMSQueueProducer extends JMSBase {

    private MessageProducer producer;
    
    public JMSQueueProducer() {
        super(MessageType.QUEUE);
    }

    public void init() throws Exception {
        super.init();
        this.producer = super.getProducer();
    }

    /**
     * Send Message
     * @param text
     * @throws JMSException
     */
    public void send(String text) throws JMSException {
        TextMessage message = super.getSession().createTextMessage(text);
        this.producer.send(message);
    }
    
    public MessageProducer getProducer() {
        return this.producer;
    }
}
