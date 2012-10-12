package org.slc.sli.search.util.amq;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;

/**
 * ActiveMQ JMS Queue Subscriber
 * @author tosako
 *
 */
public class JMSQueueConsumer extends JMSBase {
    public JMSQueueConsumer() {
        super(MessageType.QUEUE);
    }
    public MessageConsumer getConsumer() throws JMSException {
        return super.getConsumer();
    }
}
