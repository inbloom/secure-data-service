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
import javax.jms.TextMessage;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.search.util.amq.JMSQueueConsumer;
import org.slc.sli.search.util.amq.JMSQueueProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context-test.xml" })
public class JMSBaseTest {
    
    @Autowired
    JMSQueueConsumer queueConsumer;
    
    @Autowired
    JMSQueueProducer queueProducer;
    
    @Before
    public void init() throws Exception {
        queueConsumer.setQueue("test");
        MessageConsumer consumer=queueConsumer.getConsumer();
        //make sure queue is empty
        while(consumer.receive(1000)!=null) {
            System.out.println("out");
        }
        
        
        queueProducer.setQueue("test");
        queueProducer.send("hello1");
        queueProducer.send("hello2");
        queueProducer.send("hello3");
        queueProducer.send("hello4");
    }
    @After
    public void destory() throws Exception {
        this.queueProducer.destroy();
        this.queueConsumer.destroy();
    }

    /**
     * Test JMS
     * @throws JMSException
     */
    @Test
    public void testJMS() throws JMSException {

        MessageConsumer consumer=queueConsumer.getConsumer();
        TextMessage message=(TextMessage) consumer.receive(100);
        Assert.assertEquals("hello1", message.getText());
        message=(TextMessage) consumer.receive(100);
        Assert.assertEquals("hello2", message.getText());
        message=(TextMessage) consumer.receive(100);
        Assert.assertEquals("hello3", message.getText());
        message=(TextMessage) consumer.receive(100);
        Assert.assertEquals("hello4", message.getText());
    }
    
    public void setQueueConsumer(JMSQueueConsumer queueConsumer) {
        this.queueConsumer = queueConsumer;
    }
}
