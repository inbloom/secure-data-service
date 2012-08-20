package org.slc.sli.ingestion.streaming.typeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.slc.sli.ingestion.streaming.TypeProvider;
import org.springframework.stereotype.Component;

@Component
public class XsdTypeProvider implements TypeProvider {

	private static final Namespace XS_NAMESPACE = Namespace.getNamespace("xs", "http://www.w3.org/2001/XMLSchema");

	private String schemaLocation = "sliXsd-R1/ComplexTypes-R1.xsd";

	private List<String> complexTypes = new ArrayList<String>();
	private Map<String,String> typeMap = new HashMap<String,String>();

	@PostConstruct
	@SuppressWarnings("unused")
	private void init() throws Exception {
		SAXBuilder b = new SAXBuilder();
		Document doc = b.build(ClassLoader.getSystemResourceAsStream(schemaLocation));
		Iterable<Element> complexTypes = doc.getDescendants(Filters.element("complexType", XS_NAMESPACE));

		for (Element e : complexTypes) {
			this.complexTypes.add(e.getAttributeValue("name"));
		}

		Iterable<Element> elements = doc.getDescendants(Filters.element("element", XS_NAMESPACE));

		for (Element e : elements) {
			this.typeMap.put(e.getAttributeValue("name"), e.getAttributeValue("type"));
		}
	}

	@Override
	public boolean isComplexType(String elementName) {
		return this.complexTypes.contains(elementName) || this.complexTypes.contains(this.typeMap.get(elementName));
	}

	@Override
	public Object convertType(String elementName, String value) {
		return value;
	}

}
