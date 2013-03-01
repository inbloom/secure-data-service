package org.slc.sli.search.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfigStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Helper class to maintain search related subscriptions for sarje
 *
 */
public class SarjeSubscriptionAdmin {
    private static final Logger LOG = LoggerFactory.getLogger(SarjeSubscriptionAdmin.class);
    public static final String SEARCH_EVENT_ID = "oplog:search";
    public static final String SEARCH_EVENT_ID_FIELD = "eventId";

    // dedicated search queue
    private String searchQueue;

    // collections that stores subscription info
    private String dbJobsCollection;

    private IndexConfigStore indexConfigStore;

    private JmsTemplate jmsTemplate;
    // topic oplog agents listen to for subscriptions
    private Topic subscriptionBroadcastTopic;

    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private MongoOperations mongoTemplate;

    public void init() {
        if (jmsTemplate == null) {
            throw new SearchIndexerException("JmsTemplate must be set");
        }
        bootstrapSearchSubscription();
    }

    /**
     * Generate search subscription from the config file
     * @return Subscription doc
     */
    public Subscription getSearchSubscription() {
        List<SubscriptionTrigger> st = new ArrayList<SubscriptionTrigger>();
        for (IndexConfig config : indexConfigStore.getConfigs()) {
            st.add(new SubscriptionTrigger(config.getCollectionName()));
        }
        return new Subscription(searchQueue, st);
    }

    /**
     * Get all subscriptions by event id
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Object>> getAllSubscriptions() {
        // we cannot use findAll because document structure can deviate from
        // search job definition and result in extra fields we can't ignore.
        Map<String, Map<String, Object>> subscriptions = new HashMap<String, Map<String, Object>>();

        DBCursor cursor = null;
        try {
            cursor = getJobsCursor();
            DBObject obj;
            while (cursor.hasNext()) {
                obj = cursor.next();
                subscriptions.put((String)obj.get(SEARCH_EVENT_ID_FIELD), obj.toMap());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return subscriptions;
    }
    
    public DBCursor getJobsCursor() {
        DBCollection collection = mongoTemplate.getCollection(this.dbJobsCollection);
        return collection.find();
    }

    /**
     * Update subscriptions collection and broadcast the change if the search subscription changed
     */
    private void bootstrapSearchSubscription() {
        Subscription existing = objectMapper.convertValue(getAllSubscriptions().get(SEARCH_EVENT_ID), Subscription.class);
        Subscription current = getSearchSubscription();
        if (!current.equals(existing)) {
            mongoTemplate.save(current, dbJobsCollection);
            publishSubscriptions();
        }
    }

    public void publishOnDemand(String message) {
        publishSubscriptions();
    }

    /**
     * Publish all subscriptions to the subscription topic.
     * Sarje expects a full collection
     */
    public void publishSubscriptions() {
        LOG.info("Publishing subscriptions to " + subscriptionBroadcastTopic);
        jmsTemplate.send(subscriptionBroadcastTopic, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                try {
                    return session.createTextMessage(objectMapper.writeValueAsString(getAllSubscriptions().values()));
                } catch (Exception e) {
                    throw new SearchIndexerException("Unable to publish subscriptions", e);
                }
              }
          });
    }

    public void setSearchQueue(String searchQueue) {
        this.searchQueue = searchQueue;
    }

    public void setSubscriptionBroadcastTopic(Topic subscriptionBroadcastTopic) {
        this.subscriptionBroadcastTopic = subscriptionBroadcastTopic;
    }

    public void setDbJobsCollection(String dbJobsCollection) {
        this.dbJobsCollection = dbJobsCollection;
    }

    public void setIndexConfigStore(IndexConfigStore indexConfigStore) {
        this.indexConfigStore = indexConfigStore;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setMongoTemplate(MongoOperations mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Document representing a subscription
     *
     */
    @Document
    public static class Subscription {
        @Id
        private String id;
        private String eventId;
        private String queue;
        private boolean publishOplog;
        private List<SubscriptionTrigger> triggers;

        public Subscription() {
            // Empty Constructor, because the way we create this object is using reflection.
        }

        public Subscription(String eventId, String queue, boolean publishOplog, List<SubscriptionTrigger> triggers) {
            this.id = eventId;
            this.eventId = eventId;
            this.queue = queue;
            this.publishOplog = publishOplog;
            this.triggers = triggers;
        }

        public Subscription(String queue, List<SubscriptionTrigger> triggers) {
            // make a const
            this(SEARCH_EVENT_ID, queue, true, triggers);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            Subscription other = (Subscription) obj;
            if (!eventId.equals(other.eventId) || publishOplog != other.publishOplog ||
                (queue == null && other.queue != null || queue != null && !queue.equals(other.queue))) {
                return false;
            }
            return triggers != null && triggers.containsAll(other.triggers) && triggers.size() == other.triggers.size();
        }
        
        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                    append(eventId).
                    append(queue).
                    toHashCode();
            }

    }

    @Document
    public static class SubscriptionTrigger {
        private static final String REG_PREFIX = "^(?!staging)^[^.]+[.]";
        private static final String REG_SUFFIX = "$";
        private String ns;

        public SubscriptionTrigger(){
            // Empty Constructor, because the way we create this object is using reflection.
        }

        public SubscriptionTrigger(String ns) {
            if (ns == null) {
                throw new IllegalArgumentException("ns must not be null");
            }
            this.ns = REG_PREFIX + ns + REG_SUFFIX;
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && ns.equals(((SubscriptionTrigger) obj).ns);
        }
        
        @Override
        public int hashCode() {
            return new HashCodeBuilder(13, 37). // two randomly chosen prime numbers
                    // if deriving: appendSuper(super.hashCode()).
                    append(ns).
                    toHashCode();
            }

    }
}
