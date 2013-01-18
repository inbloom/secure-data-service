package org.slc.sli.aspect;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.common.util.logging.LoggerCarrier;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public aspect LoggerCarrierAspect {
	declare parents : (org.slc.sli.ingestion..* &&
//    declare parents : (org.slc.sli.ingestion.processors.* && org.slc.sli.ingestion..* &&
            !java.lang.Enum+)  implements LoggerCarrier;
    
    private MongoTemplate template;
    
    public MongoTemplate getTemplate() {
        return template;
    }
    
    public void setTemplate(MongoTemplate template) {
        this.template = template;
    }
    
    @Value("${sli.ingestion.securityEvent.capSize}")
    private String capSize;
    
    public String getCapSize() {
        return capSize;
    }
    
    public void setCapSize(String capSize) {
        this.capSize = capSize;
    }
    
    public void LoggerCarrier.audit(SecurityEvent event) {
        MongoTemplate mongoTemplate = LoggerCarrierAspect.aspectOf().getTemplate();
        String capSizeStr = LoggerCarrierAspect.aspectOf().getCapSize();
        
        if (capSizeStr != null && !capSizeStr.isEmpty()) {
            int capSizeInt = Integer.parseInt(capSizeStr);
            // UN: If the collection is not present, create the capped collection.
            // However, if it is present and uncapped, change it to capped collection
            // 496 bytes is size of 1 security event document * capsize ~ the size that we want.
            if (!mongoTemplate.collectionExists("securityEvent")) {
                CollectionOptions collectionOptions = new CollectionOptions(capSizeInt * 496, capSizeInt, true);
                mongoTemplate.createCollection("securityEvent", collectionOptions);
            } else {
                if (!mongoTemplate.getCollection("securityEvent").isCapped()) {
                    DBObject cmd = new BasicDBObject();
                    cmd.put("convertToCapped", "securityEvent");
                    cmd.put("size", capSizeInt * 496);
                    mongoTemplate.getDb().command(cmd);
                }
            }
        }
        
        mongoTemplate.save(event);
        
        switch (event.getLogLevel()) {
            case TYPE_DEBUG:
                LoggerFactory.getLogger("SecurityMonitor").debug(event.toString());
                break;
            
            case TYPE_WARN:
                LoggerFactory.getLogger("SecurityMonitor").warn(event.toString());
                break;
            
            case TYPE_INFO:
                LoggerFactory.getLogger("SecurityMonitor").info(event.toString());
                break;
            
            case TYPE_ERROR:
                LoggerFactory.getLogger("SecurityMonitor").error(event.toString());
                break;
            
            case TYPE_TRACE:
                LoggerFactory.getLogger("SecurityMonitor").trace(event.toString());
                break;
            
            default:
                LoggerFactory.getLogger("SecurityMonitor").info(event.toString());
                break;
        }
    }
}
