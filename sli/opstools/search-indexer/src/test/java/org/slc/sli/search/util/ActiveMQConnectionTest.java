/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slc.sli.search.util;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.search.util.amq.ActiveMQConnection;
import org.slc.sli.search.util.amq.ActiveMQConnection.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context-test.xml" })
public class ActiveMQConnectionTest {

    @Autowired
    ActiveMQConnection activeMQConnection;

    @Before
    public void init() throws Exception {
        activeMQConnection.setQueue("test");
        

        MessageProducer queueProducer = activeMQConnection.getProducer(MessageType.QUEUE);
        queueProducer.send(activeMQConnection.getSession().createTextMessage("hello1"));
        queueProducer.send(activeMQConnection.getSession().createTextMessage("hello2"));
        queueProducer.send(activeMQConnection.getSession().createTextMessage("hello3"));
        queueProducer.send(activeMQConnection.getSession().createTextMessage("hello4"));
    }

    /**
     * Test JMS
     * 
     * @throws JMSException
     */
    @Test
    public void testJMS() throws JMSException {

        MessageConsumer consumer = activeMQConnection.getConsumer(MessageType.QUEUE);
        TextMessage message = (TextMessage) consumer.receive(100);
        Assert.assertEquals("hello1", message.getText());
        message = (TextMessage) consumer.receive(100);
        Assert.assertEquals("hello2", message.getText());
        message = (TextMessage) consumer.receive(100);
        Assert.assertEquals("hello3", message.getText());
        message = (TextMessage) consumer.receive(100);
        Assert.assertEquals("hello4", message.getText());
    }

    public void setActiveMQConnection(ActiveMQConnection activeMQConnection) {
        this.activeMQConnection = activeMQConnection;
    }
}
