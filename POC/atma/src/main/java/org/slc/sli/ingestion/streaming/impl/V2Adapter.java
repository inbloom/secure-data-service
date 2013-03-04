package org.slc.sli.ingestion.streaming.impl;

import java.util.Map;

import org.slc.sli.ingestion.streaming.VersionAwareAdapter;
import org.springframework.stereotype.Component;

/**
 * Adapts incoming data to fit V2 rockets
 * 
 * @author dkornishev
 *
 */
@Component
public class V2Adapter implements VersionAwareAdapter {

    @Override
    public Map<String, Object> adapt(Map<String, Object> incoming) {
        return incoming;
    }

}
