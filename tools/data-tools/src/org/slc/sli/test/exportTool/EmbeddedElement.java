package org.slc.sli.test.exportTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmbeddedElement {
    public String name;

    public String template;
    public List<String> valuePlaceholders = new ArrayList<String>();
    public List<String> embeddedPlaceholders = new ArrayList<String>();
    public Map<String, EmbeddedElement> embeddedElementMap = new HashMap<String, EmbeddedElement>();

    public String query;
    public List<String> columnNames = new ArrayList<String>();
    public List<String> joinKeys = new ArrayList<String>();
}
