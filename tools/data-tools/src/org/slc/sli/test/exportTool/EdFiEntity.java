package org.slc.sli.test.exportTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EdFiEntity {
    public String begin;
    public String end;
    public String mainTemplate;
    public String mainQuery;
    public List<String> valuePlaceholders = new ArrayList<String>();
    public List<String> embeddedElementPlaceholders = new ArrayList<String>();

    public Map<String, EmbeddedElement> EmbeddedElements = new HashMap<String, EmbeddedElement>();
}
