package org.slc.sli.ingestion.streaming;

import java.util.Map;

public interface VersionAwareAdapter {
    public Map<String, Object> adapt(Map<String,Object> incoming);
}
