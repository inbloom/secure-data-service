package org.slc.sli.api.cache;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

/**
 * JMS Replicated session cache
 * 
 * @author dkornishev
 * 
 */
@Component
public class SessionCache {

    private static final int TIME_TO_LIVE = 900;
    private static final int TIME_TO_IDLE = 300;
    private static final int MAX_ENTRIES = 10000;

    private static final String TOPIC_NAME = "sessionSync";
    private static final String CACHE_NAME = "session";

    private static final String PUT = "put";
    private static final String REMOVE = "remove";

    private static final String TOKEN_KEY = "token";
    private static final String ACTION_KEY = "action";

    @Value("${sli.ingestion.queue.brokerUrl}")
    private String url;

    private Cache sessions;

    private TopicSession jmsSession;
    private TopicPublisher tp;

    private Thread listener;
    private CacheManager manager;
    private boolean live = true;

    @PostConstruct
    @SuppressWarnings("unused")
    private void init() throws Exception {

        // Init Cache
        Configuration c = new Configuration();
        c.setName("sessionManager");
        manager = new CacheManager(c);
        CacheConfiguration config = new CacheConfiguration();
        config.eternal(false).name(CACHE_NAME).maxEntriesLocalHeap(MAX_ENTRIES).memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU).timeToIdleSeconds(TIME_TO_IDLE).timeToLiveSeconds(TIME_TO_LIVE);
        manager.addCache(new Cache(config));
        sessions = manager.getCache(CACHE_NAME);

        // Init JMS replication
        ConnectionFactory factory = new ActiveMQConnectionFactory(this.url);
        Connection conn = factory.createConnection();
        conn.start();
        jmsSession = (TopicSession) conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        final Topic topic = jmsSession.createTopic(TOPIC_NAME);
        tp = jmsSession.createPublisher(topic);

        listener = new Thread() { // Thread created once upon container startup
            public void run() {
                try {
                    MessageConsumer consumer = jmsSession.createConsumer(topic);
                    while (live) {

                        ObjectMessage msg = (ObjectMessage) consumer.receive();

                        debug("Received replication message: {}", msg);

                        if (PUT.equals(msg.getStringProperty(ACTION_KEY))) {
                            sessions.put(new Element(msg.getStringProperty(TOKEN_KEY), msg.getObject()));
                        } else if (REMOVE.equals(msg.getStringProperty(ACTION_KEY))) {
                            sessions.remove(msg.getStringProperty(TOKEN_KEY));
                        }

                    }
                } catch (JMSException e) {
                    error("Error reading replication message", e);
                }
            }
        };

        listener.start();
    }

    @PreDestroy
    @SuppressWarnings("unused")
    private void teardown() {
        this.live = false;
        this.manager.shutdown();
    }

    public void put(String token, OAuth2Authentication auth) {
        if (auth != null) {
            this.sessions.put(new Element(token, auth));
            replicate(token, auth);
        } else {
            warn("Attempting to cache null session!");
        }
    }

    public OAuth2Authentication get(String token) {

        Element element = this.sessions.get(token);
        if (element != null) {
            return (OAuth2Authentication) element.getObjectValue();
        } else {
            warn("Session cache MISS");
            return null;
        }
    }

    public void remove(String token) {
        this.sessions.remove(token);

        try {
            ObjectMessage msg = createMessage(token, null, REMOVE);
            tp.send(msg);
        } catch (JMSException e) {
            error("Failed to replicate session cache entry", e);
        }
    }
    
    public void clear() {
        this.sessions.removeAll();
    }

    private void replicate(String token, OAuth2Authentication auth) {
        try {
            ObjectMessage msg = createMessage(token, auth, PUT);
            tp.send(msg);
        } catch (JMSException e) {
            error("Failed to replicate session cache entry", e);
        }
    }

    private ObjectMessage createMessage(String token, OAuth2Authentication auth, String action) throws JMSException {
        ObjectMessage msg = jmsSession.createObjectMessage(auth);
        msg.setObjectProperty(ACTION_KEY, action);
        msg.setStringProperty(TOKEN_KEY, token);
        return msg;
    }
}
