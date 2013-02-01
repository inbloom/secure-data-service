package org.slc.sli.ingestion.parser;

public interface TypeProvider {
	public boolean isComplexType(String elementName);
	public boolean isReference(String elementName);
	public Object convertType(String elementName, String value);
	boolean existsInSchema(String parentName, String name);
    public String getTypeFromInterchange(String interchange, String eventName);
    public String getTypeFromParentType(String xsdType, String eventName);
}
