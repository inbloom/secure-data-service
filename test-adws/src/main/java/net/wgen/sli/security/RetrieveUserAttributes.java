package net.wgen.sli.security;

import java.util.Map;

public interface RetrieveUserAttributes {
    public Map<String, String> getAttributes(String username);
    public String getUserCN(String username);
}