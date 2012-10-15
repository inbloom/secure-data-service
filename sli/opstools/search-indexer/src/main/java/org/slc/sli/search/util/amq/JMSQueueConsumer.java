package org.slc.sli.search.util.amq;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;

/**
 * ActiveMQ JMS Queue Subscriber
 * 
 * @author tosako
 * 
 */
public class JMSQueueConsumer extends JMSBase {
    private MessageConsumer consumer = null;

    public JMSQueueConsumer() {
        super(MessageType.QUEUE);
    }

    public MessageConsumer getConsumer() throws JMSException {
        if (this.consumer == null) {
            this.consumer = super.getConsumer();
        }
        return consumer;
    }
}
