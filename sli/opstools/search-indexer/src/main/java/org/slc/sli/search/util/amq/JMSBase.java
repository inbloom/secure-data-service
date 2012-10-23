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
package org.slc.sli.search.util.amq;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

/**
 * Base class to create ActiveMQ JMS Connection for either Topic or Queue
 * 
 * @author Takashi Osako
 * 
 */
public abstract class JMSBase {

    private String mqURL;

    private String mqUsername;

    private String mqPswd;

    private String queue;

    private String brokerURI;
    private Session session;
    private Connection connection;
    private Destination destination;
    private MessageType messageType;

    private boolean embeddedBroker = false;
    private static BrokerService broker = null;

    private static final String STOMP_URL = "stomp://localhost:61613";
    private static final String JMS_URL = "tcp://localhost:61616";

    enum MessageType {
        QUEUE, TOPIC;
    }

    public JMSBase(MessageType messageType) {
        this.messageType = messageType;
    }

    public void init() throws Exception {
        if (this.embeddedBroker) {
            // start embedded broker
            if (broker == null) {
                broker = new BrokerService();
                broker.setPersistent(false);
                broker.setUseJmx(true);

                broker.addConnector(STOMP_URL);
                broker.addConnector(JMS_URL);
                broker.getSystemUsage().getTempUsage().setLimit(1024 * 1024 * 1024);
                broker.start();
            }
            // use localhost and port 61616 for embedded broker to access
            this.brokerURI = JMS_URL;

        } else {
            this.brokerURI = this.mqURL;
        }

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.brokerURI);
        if ((this.mqUsername == null && this.mqPswd == null) || this.embeddedBroker) {
            this.connection = connectionFactory.createConnection();
        } else {
            this.connection = connectionFactory.createConnection(this.mqUsername, this.mqPswd);
        }

        this.connection.start();

        // set true if it requires acknowledge
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        if (this.messageType == MessageType.QUEUE) {
            this.destination = session.createQueue(this.queue);
        } else if (this.messageType == MessageType.TOPIC) {
            this.destination = this.session.createTopic(this.queue);
        }
    }

    public void destroy() throws JMSException {
        session.close();
        connection.close();
    }

    /**
     * get MessageProducer (for publisher)
     * 
     * @return
     * @throws JMSException
     */
    protected MessageProducer getProducer() throws JMSException {
        MessageProducer producer = session.createProducer(this.destination);
        return producer;
    }

    /**
     * get MessageConsumer (for subscriber)
     * 
     * @return
     * @throws JMSException
     */
    protected MessageConsumer getConsumer() throws JMSException {
        MessageConsumer consumer = session.createConsumer(this.destination);
        return consumer;
    }

    protected Session getSession() {
        return this.session;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public void setBrokerURI(String brokerURI) {
        this.brokerURI = brokerURI;
    }

    public void setMqURL(String mqURL) {
        this.mqURL = mqURL;
    }

    public void setMqPswd(String mqPswd) {
        this.mqPswd = mqPswd;
    }

    public void setMqUsername(String mqUsername) {
        this.mqUsername = mqUsername;
    }

    public void setEmbeddedBroker(boolean embeddedBroker) {
        this.embeddedBroker = embeddedBroker;
    }

}
