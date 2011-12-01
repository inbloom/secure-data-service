package org.slc.sli.api.service;

import java.util.Map;

public interface Filter {
    public Map<String, Object> getFilterMap(Map<String, String> context);
}
