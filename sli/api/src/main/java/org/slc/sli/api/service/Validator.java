package org.slc.sli.api.service;

import java.util.Map;

public interface Validator {
    public boolean validate(Map<String, Object> entity);
}
