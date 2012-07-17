//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import openadk.library.ADK;
import openadk.library.ADKSchemaException;
import openadk.library.ADKTypeParseException;
import openadk.library.DefaultValueBuilder;
import openadk.library.Element;
import openadk.library.ElementDef;
import openadk.library.SIFDataObject;
import openadk.library.SIFElement;
import openadk.library.SIFFormatter;
import openadk.library.SIFMessageInfo;
import openadk.library.SIFParser;
import openadk.library.SIFSimpleType;
import openadk.library.SIFString;
import openadk.library.SIFTypeConverter;
import openadk.library.SIFTypeConverters;
import openadk.library.SIFVersion;
import openadk.library.SIFWriter;
import openadk.library.ValueBuilder;
import openadk.library.impl.BuildOptions;
import openadk.library.impl.ProfilerUtils;
import openadk.library.tools.cfg.ADKConfigException;
import openadk.library.tools.xpath.SIFXPathContext;
import openadk.util.XMLUtils;

import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.Variables;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Manages a set of mapping rules that define how to tranform a set of
 * application field values into SIF Data Objects. The Mappings class is a
 * powerful facility for programmatically transforming sets of data into
 * SIFDataObject instances based on XPath-like mapping rules. These rules can be
 * read from any DOM Document or configuration file read and written by the
 * AgentConfig class, which makes them easily customizable by the end-user.
 * <p>
 * 
 * Mappings are arranged into a selection hierarchy so that different sets of
 * rules can be created for individual zones, agent SourceIds, and versions of
 * SIF. Rules at lower levels in the hierarchy are inherited from higher levels.
 * Thus, you could create a default set of rules describing how to produce
 * StudentPersonal objects for all zones, and extend or override these rules in
 * a set of child rules that are specific to an individual zone. When selecting
 * the Mappings object for the SIF Message being processed, the
 * <code>select</code> method would choose the zone-specific set of rules over
 * the default rules, but would inherit any default field mappings not
 * explicitly overridden. This technique is very useful when the mapping rules
 * of a SIF Agent differ from zone-to-zone or from one version of SIF to the
 * next.
 * <p>
 * 
 * To use the Mappings class,
 * <p>
 * 
 * <ul>
 * <li>
 * Create a Mappings instance and populate it with Mappings children. The
 * easiest way to accomplish this is to call the <code>read</code> method to
 * read an XML configuration file or the <code>populate</code> method to
 * populate a Mappings instance from a DOM <i>Document</i>. Refer to the ADK
 * Developer Guide for more information. <br/>
 * <br/>
 * </li>
 * <li>
 * When your agent needs to map local application field values to SIF Data
 * Object elements and attributes, call the <code>select</code> method to select
 * the appropriate Mappings instance based on ZoneId, SourceId, and SIF Version.
 * The <code>select</code> method returns a Mappings instance. <br/>
 * <br/>
 * </li>
 * <li>
 * Call the <code>map</code> method to produce a SIFDataObject from a set of
 * field values prepared by your application (i.e. when preparing outbound
 * messages), or to populate your data from a SIFDataObject (i.e. when
 * processing inbound messages). <br/>
 * <br/>
 * </li>
 * <li>
 * The set of field values being mapped to or from SIF is maintained by an
 * instance of a class implementing the <code>FieldAdaptor</code> interface. The
 * ADK provides a default set of classes that implement this interface and that
 * can map data to a Map (as strings or as native data types), as well as to a
 * Java <code>ResultSet</code> object. <br/>
 * <br/>
 * </li>
 * <li>
 * The ADK allows for custom data mapping implementations to be created. To
 * create your own custom implementation, create a class that implements the
 * <code>FieldAdaptor</code> interface.</li>
 * </ul>
 * 
 * <b>Overview</b>
 * <p>
 * 
 * Each Mappings object is comprised of one or more of the following:
 * <p>
 * 
 * <ul>
 * <li>
 * <code>ObjectMappings</code> that defines how to map fields of the application
 * to a SIF Data Object's elements and attributes. Create one ObjectMapping per
 * SIF Data Object type (e.g. for "StudentPersonal", "BusInfo", etc.) An
 * <code>ObjectMapping</code> instance is in turn comprised of one or more
 * <code>FieldMapping</code> objects, each of which is comprised of a
 * <code>Rule</code>. Two Rule implementations are currently defined:
 * <code>XPathRule</code>, which uses an XPath-like query string to describe how
 * to interpret or build a SIF element or attribute; and
 * <code>OtherIdRule</code>, which describes how to interpret or build
 * <code>&lt;OtherId&gt;</code> values. <br/>
 * <br/>
 * </li>
 * <li>
 * <code>ValueSets</code> that define a simple mapping from codes and constants
 * used in the application to equivalent codes and constants used by SIF. A
 * <code>ValueSet</code> could be used, for example, tofield define a mapping
 * table for Grade Levels, Ethnicity Codes, English Proficiency Codes, etc.</li>
 * <li>
 * User-defined properties comprised of a name and value pair.</li>
 * </ul>
 * 
 * <b>Mappings Hierarchy</b>
 * <p>
 * 
 * Mappings objects form a hierarchy comprised of a <i>root</i> Mappings object
 * -- which is always present and does not have an ID -- and one or more child
 * Mappings of the root. The root serves as a container for all other Mappings
 * objects. Each child of the root container must be assigned a unique ID.
 * Mappings may also be assigned an optional ZoneId filter, SourceId filter, an
 * SIF Version filter.
 * <p>
 * 
 * The unique ID is used to select a Mappings object at runtime when multiple
 * groups of Mappings are present. If you only have one Mappings object, it is
 * recommended that it be given an ID of "default". However, agents that both
 * provide and consume objects will usually define two groups of Mappings
 * objects: one used in the translation of incoming messages and another used in
 * the production of outgoing messages. In this case it is recommended that two
 * Mappings objects be defined, one with an ID of "incoming" or similar and the
 * other with an ID of "outgoing" or similar.
 * <p>
 * 
 * When Mappings are nested, child Mappings inherit the filters, ObjectMappings,
 * ValueSets, and properties of the parent.
 * <p>
 * 
 * <b>SourceId Filter</b>
 * <p>
 * 
 * When a SourceId filter is assigned to a Mappings object, the
 * <code>select</code> method will exclude any Mappings object that does not
 * include the SourceId passed to that method. If you have tested your agent
 * with several Student Information Systems, for example, you may wish to define
 * the unique mapping rules for each SIS in a separate Mappings object where the
 * SourceId filter includes the SourceId of the SIS agent.
 * <p>
 * 
 * <b>ZoneId Filter</b>
 * <p>
 * 
 * When a ZoneId filter is assigned to a Mappings object, the
 * <code>select</code> method will exclude any Mappings instance that does not
 * include the ZoneId passed to that method. The ZoneId filter can be used to
 * customize mappings on a zone-by-zone basis.
 * <p>
 * 
 * <b>SIFVersion Filter</b>
 * <p>
 * 
 * When a SIFVersion filter is assigned to a Mappings object, the
 * <code>select</code> method will exclude any Mappings instance that does not
 * include the SIF Version passed to that method. Such a filter can be used to
 * customize mappings for a specific version of SIF. This is often necessary if
 * the tag names of elements or attributes have changed from one version of SIF
 * to the next. In this case, the XPath-like rules that you include in a
 * Mappings object must reflect the element and attribute names of each version
 * of SIF.
 * <p>
 * 
 * <b>Mappings Rules</b>
 * <p>
 * 
 * Mapping <i>rules</i> are comprised of one or more ObjectMapping and
 * FieldMapping objects that define how to map a field of the local
 * application's database to a SIF Data Object element or attribute. When
 * multiple mappings are defined for a field, the first one that evaluates true
 * is selected. The <code>map</code> method that accepts a
 * <code>SIFElement</code> object evaluates the rules of a Mappings object
 * against a SIF Data Object instance to produce a set of of values. Each entry
 * in the <code>FieldAdaptor</code> is a key/value pair where the key is the
 * name of the local application field and the value is the value from the SIF
 * Data Object that mapped to that field. Thus, an agent can call the
 * <code>map</code> method to obtain a table of values for each SIF Data Object
 * it is processing. The Mappings class can also be used to produce a SIF Data
 * Object instance from a <code>FieldAdaptor</code> prepared by the agent. Call
 * the <code>map</code> method that accepts a <code>FieldAdaptor</code> instance
 * to return a <code>SIFElement</code> object.
 * <p>
 * 
 * Mappings are comprised of a hierarchy of ObjectMapping and FieldMapping
 * object. The ObjectMapping class encapsulates a SIF Data Object type such as
 * StudentPersonal and BusInfo. Each ObjectMapping contains one or more
 * FieldMapping objects to define a field-level mapping. Mapping rules are
 * specified in an XPath-like format relative to the SIF Data Object type named
 * in the ObjectMapping.
 * <p>
 * <p>
 * 
 * <b>XML Configuration</b>
 * <p>
 * 
 * The <code>populate</code> method constructs a Mappings hierarchy from a
 * parsed DOM Document. The populate method can only be called on the root
 * Mappings container. Consult the Mappings.dtd file in the ADK's Extras
 * directory for the expected schema.
 * <p>
 * <p>
 * 
 * <b>Notes for ADK 1.x users</b>
 * <p>
 * 
 * In the 1.x version of the ADK, all of the overloads of the <code>map</code>
 * method expected a <code>java.util.Map</code> to be passed in. ADK 2.0
 * supports mapping to other storage types of data, and as such, the
 * <code>java.util.Map</code> parameter was replaced with a
 * {@link openadk.library.tools.mapping.FieldAdaptor}.
 * <p>
 * 
 * The ADK does provides the
 * {@link openadk.library.tools.mapping.StringMapAdaptor}
 * implementation class that matches the mappings behavior of the 1.x version of
 * the ADK. To convert your ADK 1.x code to use the ADK 2.x
 * <code>Mappings</code> class, create an instance of
 * <code>StringMapAdaptor</code> to wrap your <code>Map</code> instance and pass
 * that to the <code>Mappings.Map(...)</code) method. The default implementation
 * of <code>StringMapAdaptor</code> formats data from SIF using SIF 1.x formats
 * and stores them in the Map as a text value.
 * <p>
 * 
 * Here is an example...
 * <p>
 * 
 * <pre>
 * // ADK 1.x code...
 * HashMap values;
 * SIFDataObject sdo;
 * Mappings.Map(values, sdo);
 * 
 * // ADK 2.x equivalent....
 * HashMap values;
 * SIFDataObject sdo;
 * StringMapAdaptor fieldAdaptor = new StringMapAdaptor(values);
 * Mappings.Map(fieldAdaptor, sdo);
 * </pre>
 * 
 * 
 * 
 * @author Eric Petersen
 * @version ADK 1.0
 */
public class Mappings {
	// These are not declared final so vendors can change them at runtime if
	// needed
	private static String XML_MAPPINGS = "mappings", XML_OBJECT = "object",
			XML_VALUESET = "valueset";

	static String XML_FIELD = "field";
	static String XML_LIST = "list";
	static String XML_LISTEND = "/list";

	/**
	 * The parent Mappings object
	 */
	protected Mappings fParent;

	/**
	 * Child Mappings objects keyed by ID
	 */
	protected HashMap<String, Mappings> fChildren = null;

	/**
	 * Optional DOM Node associated with this Mappings
	 */
	protected Node fNode;

	/**
	 * The ID of this Mappings object
	 */
	protected String fId;

	/**
	 * SourceId Filter. A list of SourceIds that define the SIF Agents to which
	 * this mapping applies. When null, this Mappings object applies to all
	 * SourceIds.
	 */
	protected String[] fSourceIds;

	/**
	 * ZoneId Filter. A list of ZoneIds that define the Zones to which this
	 * mapping applies. When null, this Mappings object applies to all ZoneIds.
	 */
	protected String[] fZoneIds;

	/**
	 * SIFVersion Filter. A list of SIFVersion instances that define the
	 * versions of SIF to which this mapping applies. When null, this Mappings
	 * object applies to all versions of SIF.
	 */
	protected SIFVersion[] fSifVersions;

	/**
	 * ObjectMappings. Each entry is an ObjectMapping keyed by object name.
	 */
	protected HashMap<String, ObjectMapping> fObjRules = null;

	/**
	 * ValueSetMappings. Each entry is a ValueSet keyed by a unique string ID.
	 */
	protected HashMap<String, ValueSet> fValueSets = null;

	/**
	 * User-defined properties for this Mappings object
	 */
	protected HashMap<String, String> fProps = null;

	/**
	 * Constructs the root-level Mappings container
	 */
	public Mappings() {
		this(null, null, null, null, null);
	}

	/**
	 * Constructs a child Mappings object with no filters.
	 * <p>
	 * 
	 * @param parent
	 *            The parent Mappings object
	 * @param id
	 *            A unique string ID for this Mappings object
	 */
	public Mappings(Mappings parent, String id) {
		this(parent, id, null, null, null);
	}

	/**
	 * Constructs a child Mappings object with optional filters.
	 * <p>
	 * 
	 * @param parent
	 *            The root Mappings object
	 * @param id
	 *            A unique identifier for this Mappings object
	 * @param sourceIdFilter
	 *            A comma-delimited list of SourceIds or null if no SourceId
	 *            filter should be applied to this Mappings object
	 * @param zoneIdFilter
	 *            A comma-delimited list of ZoneIds or null if no SourceId
	 *            filter should be applied to this Mappings object
	 * @param sifVersionFilter
	 *            A comma-delimited list of SIF Versions, in the form "1.0r1",
	 *            or null if no SIF Version filter should be applied to this
	 *            Mappings object
	 */
	public Mappings(Mappings parent, String id, String sourceIdFilter,
			String zoneIdFilter, String sifVersionFilter) {
		fParent = parent;
		fId = id;
		setSourceIdFilter(sourceIdFilter);
		setZoneIdFilter(zoneIdFilter);
		setSIFVersionFilter(sifVersionFilter);
	}

	/**
	 * Creates a copy of this Mappings object and adds the copy as a child of
	 * the specified parent. Note the root Mappings container cannot be copied.
	 * If this method is called on the root an exception is raised.
	 * <p>
	 * 
	 * This method performs a "deep copy", such that a copy is made of each
	 * child Mappings, ObjectMapping, FieldMapping, ValueSet, and Property
	 * instance.
	 * <p>
	 * 
	 * @param newParent
	 *            The parent Mappings instance
	 * @return A "deep copy" of this root Mappings object
	 * 
	 * @exception ADKMappingException
	 *                thrown if this method is not called on the root Mappings
	 *                container
	 */
	public Mappings copy(Mappings newParent) throws ADKMappingException {
		if (isRoot())
			throw new ADKMappingException(
					"Mappings.copy cannot be called on the root Mappings container",
					null);

		// Create a new Mappings instance
		Mappings m = new Mappings(newParent, fId);

		// Copy the DOM Node if there is one
		if (fNode != null && newParent.fNode != null) {
			m.fNode = newParent.fNode.getOwnerDocument().importNode(fNode,
					false);
		}
		newParent.addChild(m);

		// Copy the filters
		m.setSourceIdFilter(getSourceIdFilterString());
		m.setZoneIdFilter(getZoneIdFilterString());
		m.setSIFVersionFilter(getSIFVersionFilterString());

		// Copy all Mappings children
		if (fChildren != null) {
			for (Iterator it = fChildren.keySet().iterator(); it.hasNext();) {
				Mappings ch = fChildren.get(it.next());
				m.addChild(ch.copy(m));
			}
		}

		// Copy all ObjectMapping children
		if (fObjRules != null) {
			for (Iterator it = fObjRules.keySet().iterator(); it.hasNext();) {
				ObjectMapping ch = fObjRules.get(it.next());
				ObjectMapping copy = ch.copy(m);
				m.addRules(copy, false);
				// if( m.fNode != null )
				// m.fNode.appendChild( copy.fNode );
			}
		}

		// Copy fValueSets
		if (fValueSets != null) {
			for (Iterator it = fValueSets.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				ValueSet vs = fValueSets.get(key);
				m.addValueSet(vs.copy(m));
			}
		}

		// Copy properties
		if (fProps != null) {
			for (Iterator it = fProps.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				String val = fProps.get(key);
				m.setProperty(key, val);
			}
		}

		return m;
	}

	/**
	 * Sets the optional DOM Node associated with this Mappings instance. The
	 * DOM Node is set when a Mappings object is populated from a DOM Document.
	 * 
	 * @param node
	 *            The DOM Node associated with this Mappings instance
	 */
	public void setNode(Node node) {
		fNode = node;
	}

	/**
	 * Gets the optional DOM Node associated with this Mappings instance. The
	 * DOM Node is set when a Mappings object is populated from a DOM Document.
	 * 
	 * @return The DOM Node associated with this Mappings instance
	 */
	public Node getNode() {
		return fNode;
	}

	/**
	 * Gets the unique ID of this Mappings instance
	 * 
	 * @return The unique ID of this Mappings instance
	 */
	public String getId() {
		return fId;
	}

	/**
	 * Gets the parent Mappings instance
	 * 
	 * @return The parent Mappings instance or NULL if this is the top-level
	 *         Mappings instance
	 */
	public Mappings getParent() {
		return fParent;
	}

	/**
	 * Determines if this is the root Mappings container. Applications may call
	 * the <code>select</code> and <code>map</code> methods on the root
	 * container only.
	 * 
	 * @return true if this is the root Mappings container, otherwise false
	 */
	public boolean isRoot() {
		return fParent == null;
	}

	/**
	 * Gets the root Mappings container.
	 * 
	 * @return The root Mappings container instance
	 */
	public Mappings getRoot() {
		Mappings parent = this;
		while (parent.fParent != null)
			parent = parent.fParent;

		return parent;
	}

	/**
	 * Adds a Mappings child
	 * 
	 * @param m
	 *            The child Mappings instance to add to this parent
	 */
	public void addChild(Mappings m) {
		if (fChildren == null)
			fChildren = new HashMap<String, Mappings>();

		fChildren.put(m.getId(), m);

		// If there is a DOM Node associated with this Mappings, and the new
		// child
		// also has a Node, attach it
		if (fNode != null && m.fNode != null)
			fNode.appendChild(m.fNode);
	}

	/**
	 * Create a Mappings child
	 * 
	 * @param id
	 *            The ID of the new Mappings instance
	 * @return The newly-created child Mappings instance with the specified ID
	 * @throws ADKMappingException
	 *             If the child cannot be created
	 */
	public Mappings createChild(String id) throws ADKMappingException {
		Mappings m = new Mappings(this, id);

		try {
			if (fNode != null) {
				m.fNode = m.toDOM(fNode.getOwnerDocument());
			}
		} catch (SAXException se) {
			throw new ADKMappingException(se.getMessage(), null, se);
		}

		addChild(m);

		return m;
	}

	/**
	 * Removes a Mappings child
	 * 
	 * @param m
	 *            The child Mappings instance to remove
	 */
	public void removeChild(Mappings m) {
		if (fChildren != null)
			fChildren.remove(m.getId());

		// If there is a DOM Node associated with this Mappings, and the new
		// child
		// also has a Node, detatch it
		if (fNode != null && m.fNode != null)
			fNode.removeChild(m.fNode);
	}

	/**
	 * Return an array of all Mappings children
	 * 
	 * @return An array of all Mappings children
	 */
	public Mappings[] getChildren() {
		Mappings[] arr = new Mappings[fChildren == null ? 0 : fChildren.size()];

		if (fChildren != null) {
			int i = 0;
			for (Iterator it = fChildren.values().iterator(); it.hasNext();) {
				arr[i++] = (Mappings) it.next();
			}
		}

		return arr;
	}

	/**
	 * Count the number of Mappings children
	 * 
	 * @return The number of Mappings children
	 */
	public int getChildCount() {
		return fChildren == null ? 0 : fChildren.size();
	}

	/**
	 * Sets a SourceId filter on this Mappings object.
	 * <p>
	 * 
	 * @param filter
	 *            A comma-delimited list of SourceIds that define the agents
	 *            this Mappings object applies to. If the filter includes an
	 *            asterisk, the Mappings object will have no SourceId filter
	 *            regardless of whether any other SourceIds are specified in the
	 *            <i>filter</i> string.
	 */
	public void setSourceIdFilter(String filter) {
		if (filter != null) {
			StringTokenizer tok = new StringTokenizer(filter, ",");

			fSourceIds = new String[tok.countTokens()];

			for (int i = 0; i < fSourceIds.length; i++) {
				fSourceIds[i] = (String) tok.nextElement();
				if (fSourceIds[i].equals("*")) {
					fSourceIds = null;
					return;
				}
			}
		} else
			fSourceIds = null;

		if (fNode != null)
			XMLUtils.setAttribute(fNode, "sourceId", filter);
	}

	/**
	 * Sets a ZoneId filter on this Mappings object.
	 * <p>
	 * 
	 * @param filter
	 *            A comma-delimited list of ZoneIds that define the zones this
	 *            Mappings object applies to. If the filter includes an
	 *            asterisk, the Mappings object will have no ZoneId filter
	 *            regardless of whether any other ZoneIds are specified in the
	 *            <i>filter</i> string.
	 */
	public void setZoneIdFilter(String filter) {
		if (filter != null) {
			StringTokenizer tok = new StringTokenizer(filter, ",");

			fZoneIds = new String[tok.countTokens()];

			for (int i = 0; i < fZoneIds.length; i++) {
				fZoneIds[i] = (String) tok.nextElement();
				if (fZoneIds[i].equals("*")) {
					fZoneIds = null;
					return;
				}
			}
		} else
			fZoneIds = null;

		if (fNode != null)
			XMLUtils.setAttribute(fNode, "zoneId", filter);
	}

	/**
	 * Sets a SIFVersion filter on this Mappings object.
	 * <p>
	 * 
	 * @param filter
	 *            A comma-delimited list of SIFVersion strings, in the form
	 *            "1.0r1", that define the zones this Mappings object applies
	 *            to. If the filter includes an asterisk, the Mappings object
	 *            will have no SIFVersion filter regardless of whether any other
	 *            SIFVersions are specified in the <i>filter</i> string.
	 */
	public void setSIFVersionFilter(String filter) {
		if (filter != null) {
			StringTokenizer tok = new StringTokenizer(filter, ",");

			fSifVersions = new SIFVersion[tok.countTokens()];

			for (int i = 0; i < fSifVersions.length; i++) {
				String verStr = (String) tok.nextElement();
				if (verStr.equals("*")) {
					fSifVersions = null;
					return;
				}

				fSifVersions[i] = SIFVersion.parse(verStr);
			}
		} else
			fSifVersions = null;

		if (fNode != null)
			XMLUtils.setAttribute(fNode, "sifVersion", filter);
	}

	/**
	 * Set a user-defined property.
	 * <p>
	 * 
	 * @param name
	 *            The property name
	 * @param value
	 *            The property value
	 */
	public void setProperty(String name, String value) {
		if (fProps == null)
			fProps = new HashMap<String, String>();

		if (name != null)
			fProps.put(name, value);

		if (fNode != null) {
			List<Node> v = XMLUtils.getElementsByTagName(fNode, "property",
					false);

			// Look for existing property with this name, update its value
			for (int i = 0; i < v.size(); i++) {
				Node n = v.get(i);
				if (XMLUtils.getAttribute(n, "name").equals(name)) {
					XMLUtils.setAttribute(n, "value", value);
					return;
				}
			}

			// No existing property found, add a new one
			Node n = fNode.getOwnerDocument().createElement("property");
			XMLUtils.setAttribute(n, "name", name);
			XMLUtils.setAttribute(n, "value", value);
			fNode.appendChild(n);
		}
	}

	/**
	 * Gets a user-defined property.
	 * <p>
	 * 
	 * @param name
	 *            The property name
	 * @param defaultValue
	 *            The value to return if the property is not defined
	 * 
	 * @return The value of the property
	 */
	public String getProperty(String name, String defaultValue) {
		if (fProps == null || name == null)
			return defaultValue;

		String s = fProps.get(name);

		return s != null ? s : defaultValue;
	}

	/**
	 * Determines if a user-defined property is currently set on this Mappings
	 * instance.
	 * <p>
	 * 
	 * @param name
	 *            The property name
	 * 
	 * @return true if the property is set; otherwise false
	 */
	public boolean hasProperty(String name) {
		return fProps != null && fProps.containsKey(name);
	}

	/**
	 * Gets the names of all user-defined properties currently set on this
	 * Mappings instance.
	 * <p>
	 * 
	 * @return An array of property names
	 */
	public String[] getPropertyNames() {
		String[] names = new String[fProps != null ? fProps.size() : 0];
		if (fProps != null) {
			int i = 0;
			for (Iterator it = fProps.keySet().iterator(); it.hasNext();) {
				names[i++] = (String) it.next();
			}
		}

		return names;
	}

	/**
	 * Populate the Mappings hierarchy from an XML Document.
	 * <p>
	 * 
	 * This method can only be called on the root Mappings object or an
	 * exception is raised. It reads all &lt;mapping&gt; elements from the
	 * Document to build the Mappings hierarchy.
	 * <p>
	 * 
	 * @param doc
	 *            A DOM <i>Document</i> that defines one or more
	 *            &lt;mappings&gt; elements from which to populate the Mappings
	 *            hierarchy. Note the root Mappings object is a virtual object
	 *            and therefore not represented by a Node in this Document.
	 * 
	 * @param parent
	 *            A DOM <i>Node</i> in the source document that should be
	 *            considered the parent Node of this Mappings object. This
	 *            parameter is required because the root Mappings object is a
	 *            virtual object that is not represented in the DOM graph, so
	 *            some other node usually serves as its parent (e.g. the
	 *            &lt;agent&gt; node in an AgentConfig configuration file)
	 * 
	 * @exception ADKConfigException
	 *                is thrown if a required element or attribute is missing or
	 *                has an invalid value
	 * 
	 * @exception ADKMappingException
	 *                is thrown if this method is called on a Mappings instance
	 *                other than the root Mappings container
	 */
	public void populate(Document doc, Node parent) throws ADKConfigException,
			ADKMappingException {
		if (fParent != null)
			throw new ADKMappingException(
					"Mappings.populate can only be called on the root Mappings container",
					null);

		fNode = parent;

		populate(doc, this);
	}

	protected void populate(Node node, Mappings parent)
			throws ADKConfigException, ADKMappingException {
		NodeList nl = node.getChildNodes();

		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				if (n.getNodeName().equalsIgnoreCase(XML_MAPPINGS)) {
					// Get the ID
					String id = getAttribute(n, "id");

					// Create a new child Mappings object
					Mappings mappings = new Mappings(parent, id);
					mappings.setNode(n);
					if (parent.fChildren == null)
						parent.fChildren = new HashMap<String, Mappings>();
					parent.fChildren.put(mappings.getId(), mappings);

					// Set a SIFVersion filter if present
					String ver = getAttribute(n, "sifVersion");
					if (ver != null && ver.trim().length() > 0)
						mappings.setSIFVersionFilter(ver);

					// Set a ZoneId filter if present
					String zoneIds = getAttribute(n, "zoneId");
					if (zoneIds != null && zoneIds.trim().length() > 0)
						mappings.setZoneIdFilter(zoneIds);

					// Set a SourceId filter if present
					String sourceIds = getAttribute(n, "sourceId");
					if (sourceIds != null && sourceIds.trim().length() > 0)
						mappings.setSourceIdFilter(sourceIds);

					// Populate the Mappings object with rules
					populate(n, mappings);
				} else if (n.getNodeName().equalsIgnoreCase(XML_OBJECT)) {
					if (n.getParentNode().getNodeName().equals(XML_MAPPINGS)) {
						String obj = getAttribute(n, XML_OBJECT);
						if (obj == null)
							throw new ADKConfigException(
									"<object> element must have an object attribute");

						ObjectMapping om = new ObjectMapping(obj);
						om.setNode(n);
						parent.addRules(om, false);
						populateObject(n, om);
						NamedNodeMap nnm = n.getAttributes();
						for (int nnmIdx = 0; nnmIdx < nnm.getLength(); nnmIdx++) {
							Attr attr = (Attr)nnm.item(nnmIdx);
							if (attr.getNodeName().startsWith("xmlns:")) {
								om.setNamespaceURIForPrefix(attr.getNodeName().substring(6), attr.getNodeValue(), false);
							}
						}
					}
				} else if (n.getNodeName().equalsIgnoreCase("property")) {
					if (n.getParentNode().getNodeName().equals(XML_MAPPINGS)) {
						parent.setProperty(getAttribute(n, "name"), getAttribute(n, "value"));
					}
				} else if (n.getNodeName().equalsIgnoreCase(XML_VALUESET)) {
					if (n.getParentNode().getNodeName().equals(XML_MAPPINGS)) {
						ValueSet set = new ValueSet(getAttribute(n, "id"), getAttribute(n, "title"), n);
						parent.addValueSet(set);
						populateValueSet(n, set);
					}
				} else
					populate(n, parent);
			}
		}
	}

	/**
	 * Parse the <code>&lt;field&gt;</code> children of an
	 * <code>&lt;object&gt;</code> element.
	 * <p>
	 * 
	 * @param element
	 *            A DOM <i>Node</i> encapsulating an <code>&lt;object&gt;</code>
	 *            element
	 * @param parent
	 *            The parent ObjectMapping instance
	 */
	protected void populateObject(Node element, ObjectMapping parent)
			throws ADKConfigException, ADKMappingException {
		NodeList nl = element.getChildNodes();

		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				// JEN - List Element
				if (n.getNodeName().equalsIgnoreCase(XML_FIELD)) {
					FieldMapping fm = FieldMapping.fromXML(parent,
							(org.w3c.dom.Element) n);
					parent.addRule(fm, false);
				} else if (n.getNodeName().equalsIgnoreCase(XML_LIST)) {
					parent.addListingMapping(populateList(n, parent));
				}
			}
		}
	}

	protected ListMapping populateList(Node node, ObjectMapping parent)
			throws ADKConfigException, ADKMappingException {
		// <List node
		NamedNodeMap nnm = node.getAttributes();
		String listObjectName = "";
		Node n = null;
		if ((n = nnm.getNamedItem("listObjectName")) != null)
			listObjectName = n.getNodeValue();

		String child = "";
		if ((n = nnm.getNamedItem("child")) != null)
			child = n.getNodeValue();

		String xpath = "";
		if ((n = nnm.getNamedItem("xPath")) != null)
			xpath = n.getNodeValue();

		ListMapping listMapping = new ListMapping(listObjectName, child, xpath);

		if ((n = nnm.getNamedItem("reference")) != null)
			listMapping.setReference(n.getNodeValue());

		node = node.getFirstChild();

		while (node != null) {

			if (node.getNodeType() == Node.ELEMENT_NODE) {

				if (node.getNodeName().equalsIgnoreCase(XML_LIST)) {
					listMapping.addMapping(populateList(node, parent));
				} else {
					FieldMapping fieldMapping = FieldMapping.fromXML(parent,
							(org.w3c.dom.Element) node);
					listMapping.addMapping(fieldMapping);
				}
			}
			node = node.getNextSibling();
		}

		return listMapping;
	}

	/**
	 * Parse the <code>&lt;value&gt;</code> children of a
	 * <code>&lt;valueset&gt;</code> element.
	 * <p>
	 * 
	 * @param element
	 *            A DOM <i>Node</i> encapsulating a
	 *            <code>&lt;valueset&gt;</code> element
	 * @param parent
	 *            The parent ValueSet instance
	 */
	protected void populateValueSet(Node element, ValueSet parent)
			throws ADKConfigException, ADKMappingException {
		NodeList nl = element.getChildNodes();

		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				if (n.getNodeName().equalsIgnoreCase("value")) {
					String name = getAttribute(n, "name");
					if (name == null)
						throw new ADKConfigException(
								"<value> name= attribute is required");
					String title = getAttribute(n, "title");

					String sifValue = XMLUtils.getText(n);

					parent.define(name, sifValue, title, n);

					String def = XMLUtils.getAttribute(n, "default");
					if (def != null) {
						String ifNull = XMLUtils.getAttribute(n, "ifnull");
						boolean renderIfDefault = false;
						if (ifNull != null) { // cool phrase!
							renderIfDefault = ifNull
									.equalsIgnoreCase("default");
						}
						boolean inboundDefault = false;
						boolean outboundDefault = false;
						if (def.equalsIgnoreCase("both")
								|| def.equalsIgnoreCase("true")) {
							inboundDefault = true;
							outboundDefault = true;
						}
						if (!inboundDefault && def.equalsIgnoreCase("inbound")) {
							inboundDefault = true;
						}
						if (!outboundDefault
								&& def.equalsIgnoreCase("outbound")) {
							outboundDefault = true;
						}

						if (inboundDefault) {
							parent.setAppDefault(name, renderIfDefault);
						}
						if (outboundDefault) {
							parent.setSifDefault(sifValue, renderIfDefault);
						}

					}

				}
			}
		}
	}

	/**
	 * Gets the Mappings object with the specified ID.
	 * <p>
	 * 
	 * Unlike the <code>select</code> methods, <code>getMappings</code> should
	 * be called when the agent knows the specific Mappings object it is going
	 * to use to perform a mapping operation. Conversely, the
	 * <code>select</code> methods select the most appropriate Mappings object
	 * based on the ZoneId, SourceId, and SIFVersion values passed to those
	 * methods.
	 * <p>
	 * 
	 * @param id
	 *            The unique string identifier of the Mappings object to return.
	 * 
	 * @return The Mappings instance with the specified ID, or <code>null</code>
	 *         if no match was found.
	 * 
	 * @exception ADKMappingException
	 *                thrown if this method is not called on the root Mappings
	 *                container
	 */
	public Mappings getMappings(String id) throws ADKMappingException {
		Mappings[] ch = getChildren();
		for (int i = 0; i < ch.length; i++) {
			if (ch[i].fId != null && ch[i].fId.equals(id))
				return ch[i];
		}

		return null;
	}

	/**
	 * Selects the appropriate Mappings object to use for <code>map</code>
	 * operations. Call this method on the root Mappings object to obtain a
	 * Mappings instance, then call its <code>map</code> method to perform a
	 * mapping operation.
	 * <p>
	 * 
	 * The selection process is as follows:
	 * 
	 * <ul>
	 * <li>
	 * All children of this Mappings are evaluated as a group in a <b>flat
	 * list</b>. The group is ordered by "restrictiveness". The more filters
	 * defined for a Mappings the more restrictive it is considered. Thus, a
	 * Mappings that defines a ZoneId, SourceId, and SIFVersion filter will be
	 * evaluated before a less-restrictive Mappings that defines only a SourceId
	 * filter. Because children inherit the filters of their parent, a child can
	 * never be less restrictive than its parent. Two or more Mappings with
	 * equal restrictiveness are evaluated in natural order, so care should be
	 * taken to not organize child Mappings objects with equal filters or the
	 * first one present will always be selected. <br/>
	 * <br/>
	 * </li>
	 * <li>
	 * The ordered Mappings are evaluated sequentially. For each, the ZoneId,
	 * SourceId, and Version are compared against any filters in effect for the
	 * Mappings instance. Any Mappings that does not pass the criteria is
	 * eliminated from the list of candidates. <br/>
	 * <br/>
	 * </li>
	 * <li>
	 * If no child Mappings pass the selection process, the Mappings on which
	 * this method is called is returned (i.e. self is returned) This ensures
	 * that this method always returns a non-null Mappings instance to the
	 * caller. <br/>
	 * <br/>
	 * </li>
	 * </ul>
	 * 
	 * @param zoneId
	 *            Restricts the selection of a Mappings object to only those
	 *            that allow this ZoneId. This parameter may be null.
	 * @param sourceId
	 *            Restricts the selection of a Mappings object to only those
	 *            that allow this SourceId. This parameter may be null.
	 * @param version
	 *            Restricts the selection of a Mappings object to only those
	 *            that allow this version of SIF. This parameter may be null.
	 * 
	 * @return The first Mappings child instance that matches the criteria or
	 *         null if no match was found
	 * @throws ADKMappingException
	 *             If this method is called on Mappings instance other than
	 *             those directly under the Root instance
	 */
	public Mappings select(String zoneId, String sourceId, SIFVersion version)
			throws ADKMappingException {
		if (fParent == null)
			throw new ADKMappingException(
					"Mappings.select cannot be called on the root-level Mappings container; it can only be called on one of the root's children",
					null);
		if (fParent != getRoot())
			throw new ADKMappingException(
					"Mappings.select can only be called on a top-level Mappings object (i.e. a child of the root Mappings container)",
					null);

		// Optimization: If no children, return self
		if (getChildCount() == 0)
			return this;

		// Group all of the child Mappings, if any, in a flat list
		List<Mappings> v = new Vector<Mappings>();
		v.add(this);
		groupDescendents(this, v);

		// Order all Mappings by "restrictiveness"
		Candidate[] candidates = new Candidate[v.size()];
		for (int i = 0; i < candidates.length; i++)
			candidates[i] = new Candidate(v.get(i));

		Arrays.sort(candidates,

		new Comparator<Candidate>() {
			public int compare(Candidate c1, Candidate c2) {
				if (c1.restrictiveness < c2.restrictiveness)
					return 1;
				else if (c1.restrictiveness > c2.restrictiveness)
					return -1;

				return 0;
			}

			public boolean equals(Object o) {
				return o.equals(this);
			}
		});

		Mappings selection = this;
		int eval = 0, highScore = 0;

		for (int i = 0; i < candidates.length; i++) {
			int score = 0;

			if (zoneId != null) {
				eval = candidates[i].fMapping.allowsZoneId(zoneId);
				if (eval == -1)
					continue;
				score += eval;
			}

			if (sourceId != null) {
				eval = candidates[i].fMapping.allowsSourceId(sourceId);
				if (eval == -1)
					continue;
				score += eval;
			}

			if (version != null) {
				eval = candidates[i].fMapping.allowsVersion(version);
				if (eval == -1)
					continue;
				score += eval;
			}

			if (score > highScore) {
				highScore = score;
				selection = candidates[i].fMapping;
			}
		}

		return selection;
	}

	/**
	 * Selects an appropriate MappingsContext object to use for an inbound
	 * <code>map</code> operation. Call this method on the root Mappings object
	 * to obtain a MappingsContext instance, then call its <code>map</code>
	 * method to perform an inbound mapping operation.
	 * 
	 * @param elementDef
	 *            The ElementDef of the Element being mapped to
	 * @param version
	 *            The SIFVersion to use when evaluating mappings XPaths.
	 * @param zoneId
	 *            The ID of the zone that this mappings operation is being
	 *            performed on
	 * @param sourceId
	 *            The Source ID of the destination agent that this mappings is
	 *            being performed for
	 * 
	 * @return a MappingsContext that can be used for evaluating mappings
	 * @throws ADKMappingException
	 */
	public MappingsContext selectInbound(ElementDef elementDef,
			SIFVersion version, String zoneId, String sourceId)
			throws ADKMappingException {
		return selectContext(MappingsDirection.INBOUND, elementDef, version,
				zoneId, sourceId);
	}

	/**
	 * Selects an appropriate MappingsContext object to use for an inbound
	 * <code>map</code> operation. Call this method on the root Mappings object
	 * to obtain a MappingsContext instance, then call its <code>map</code>
	 * method to perform an inbound mapping operation.
	 * 
	 * @param elementDef
	 *            The ElementDef of the Element being mapped to
	 * @param message
	 *            The SIF Message that is being mapped
	 * @return a MappingsContext that can be used for evaluating mappings
	 * @throws ADKMappingException
	 */
	public MappingsContext selectInbound(ElementDef elementDef,
			SIFMessageInfo message) throws ADKMappingException {
		return selectContext(MappingsDirection.INBOUND, elementDef,
				message.getSIFVersion(), message.getZone().getZoneId(),
				message.getSourceId());
	}

	/**
	 * Selects an appropriate MappingsContext object to use for an outbound
	 * <code>map</code> operation. Call this method on the root Mappings object
	 * to obtain a MappingsContext instance, then call its <code>map</code>
	 * method to perform an outbound mapping operation.
	 * 
	 * @param elementDef
	 *            The ElementDef of the Element being mapped to
	 * @param version
	 *            The SIFVersion to use when evaluating mappings XPaths.
	 * @param zoneId
	 *            The ID of the zone that this mappings operation is being
	 *            performed on
	 * @param sourceId
	 *            The Source ID of the destination agent that this mappings is
	 *            being performed for
	 * 
	 * @return a MappingsContext that can be used for evaluating mappings
	 * @throws ADKMappingException
	 */
	public MappingsContext selectOutbound(ElementDef elementDef,
			SIFVersion version, String zoneId, String sourceId)
			throws ADKMappingException {
		return selectContext(MappingsDirection.OUTBOUND, elementDef, version,
				zoneId, sourceId);
	}

	/**
	 * Selects an appropriate MappingsContext object to use for an outbound
	 * <code>map</code> operation. Call this method on the root Mappings object
	 * to obtain a MappingsContext instance, then call its <code>map</code>
	 * method to perform an outbound mapping operation.
	 * 
	 * @param elementDef
	 *            The ElementDef of the Element being mapped to
	 * @param message
	 *            The SIF Message that is being used to determine targets for
	 *            this mapping
	 * @return a MappingsContext that can be used for evaluating mappings
	 * @throws ADKMappingException
	 */
	public MappingsContext selectOutbound(ElementDef elementDef,
			SIFMessageInfo message) throws ADKMappingException {
		return selectContext(MappingsDirection.OUTBOUND, elementDef,
				message.getLatestSIFRequestVersion(), message.getZone()
						.getZoneId(), message.getSourceId());
	}

	/**
	 * Selects an appropriate MappingsContext object to use for <code>map</code>
	 * operations. Call this method on the root Mappings object to obtain a
	 * MappingsContext instance, then call its <code>map</code> method to
	 * perform a mapping operation.
	 * 
	 * @param direction
	 *            The MappingsDirection that this mapping will use
	 * @param elementDef
	 *            The ElementDef of the Element being mapped to
	 * @param version
	 *            The SIFVersion to use when evaluating mappings XPaths.
	 * @param zoneId
	 *            The ID of the zone that this mappings operation is being
	 *            performed on
	 * @param sourceId
	 *            The Source ID of the destination agent that this mappings is
	 *            being performed for
	 * @return a MappingsContext that can be used for evaluating mappings
	 * @throws ADKMappingException
	 */
	private MappingsContext selectContext(MappingsDirection direction,
			ElementDef elementDef, SIFVersion version, String zoneId,
			String sourceId) throws ADKMappingException {
		// Select the mappings instance
		Mappings m = select(zoneId, sourceId, version);
		// Create a mappings context, that retains the filters and metadata
		// associated
		// with this mappings operation
		MappingsContext mc = MappingsContext.create(m, direction, version,
				elementDef);
		return mc;
	}

	/**
	 * Recursively adds all children of the Mappings to the supplied Vector
	 */
	private void groupDescendents(Mappings m, List<Mappings> v) {
		if (m.fChildren != null) {
			for (Iterator it = m.fChildren.values().iterator(); it.hasNext();) {
				Mappings ch = (Mappings) it.next();
				v.add(ch);
				groupDescendents(ch, v);
			}
		}
	}

	/**
	 * Produce a table of field values from a SIF Data Object using an inbound
	 * mapping operation.
	 * <p>
	 * 
	 * This <code>map</code> method populates the supplied data adaptor with
	 * element or attribute values from the SIFDataObject by evaluating all
	 * field rules defined by this Mappings object for the associated SIF Data
	 * Object type.
	 * <p>
	 * 
	 * This method is intended to obtain element and attribute values from a
	 * SIFDataObject <i>consumed</i> by the agent when processing SIF Events or
	 * SIF Responses. In contrast, the other form of the <code>map</code> method
	 * is intended to populate a new SIFDataObject instance when an agent is
	 * <i>publishing</i> objects to a zone.
	 * <p>
	 * 
	 * 
	 * @param dataObject
	 *            The SIFDataObject from which to retrieve element and attribute
	 *            values from when performing the mapping operation.
	 * 
	 * @param results
	 *            A FieldAdaptor to receive the results of the mapping,
	 * 
	 * @exception ADKMappingException
	 *                thrown if an error occurs while evaluating a field rule
	 */
	public void mapInbound(SIFDataObject dataObject, FieldAdaptor results)
			throws ADKMappingException {
		mapInbound(dataObject, results, ADK.getSIFVersion());
	}

	/**
	 * Produce a table of field values from a SIF Data Object using an inbound
	 * mapping operation.
	 * <p>
	 * 
	 * This form of the <code>map</code> method allows the client to specify
	 * whether it is performing an inbound or outbound mapping operation.
	 * Currently, the direction flag is used to invoke automatic ValueSet
	 * translations on fields that have a <i>ValueSet</i> attribute.
	 * <p>
	 * 
	 * @param data
	 *            The SIFDataObject from which to retrieve element and attribute
	 *            values from when performing the mapping operation.
	 * 
	 * @param results
	 *            A <code>FieldAdaptor</code> instance that stores fields as the
	 *            result of the mapping operation.
	 * 
	 * @param version
	 *            The SIFVersion associated with the mapping operation. For
	 *            inbound SIF_Event and SIF_Response messages, this value should
	 *            be obtained by calling <code>getSIFVersion</code> on the
	 *            <i>SIFMessageInfo</i> parameter passed to the message handler.
	 *            For inbound SIF_Request messages, it should be obtained by
	 *            calling the <code>SIFMessageInfo.getSIFRequestVersion</code>
	 *            method. For outbound messages, this value should be obtained
	 *            by calling <code>ADK.getSIFVersion</code> to get the version
	 *            of SIF the class framework was initialized with. Note when
	 *            this parameter is <code>null</code>, no SIF Version filtering
	 *            will be applied to field mapping rules.
	 * 
	 * @exception ADKMappingException
	 *                thrown if an error occurs while evaluating a field rule
	 * 
	 * @since ADK 1.5
	 */
	public void mapInbound(SIFDataObject data, FieldAdaptor results,
			SIFVersion version) throws ADKMappingException {

		ObjectMapping om = getRules(
				data.getElementDef().tag(data.getSIFVersion()), true);
		if (om != null) {
			SIFXPathContext xpathContext = SIFXPathContext.newSIFContext(data,
					version);
			for (Map.Entry<String, String> prefixMapping : om.getAllNamespaceURIs(true).entrySet()) {
				xpathContext.registerNamespace(prefixMapping.getKey(), prefixMapping.getValue());
			}
			if (results instanceof Variables) {
				xpathContext.setVariables((Variables) results);
			}

			List<Mapping> list = getRulesList(version, om,
					MappingsDirection.INBOUND);
			mapInbound(xpathContext, results, data, list, version);
		}
	}

	/**
	 * Do an internal inbound mapping operation
	 * 
	 * @param xpathContext
	 *            A the SIFXPathContext to use
	 * @param results
	 *            The FieldAdaptor to store the mapped fields into
	 * @param fields
	 *            An iterable collection of FieldMappings. IMPORTANT! The
	 *            MappingsFilter needs to be already evaluated for this
	 *            collection. This method does not evaluate the filter
	 * @param version
	 *            The SIFVersion to use
	 * @throws ADKMappingException
	 */
	void mapInbound(SIFXPathContext xpathContext, FieldAdaptor results,
			SIFElement inboundObject, List<Mapping> fields, SIFVersion version)
			throws ADKMappingException {

//		if (BuildOptions.PROFILED) {
//			ProfilerUtils
//					.profileStart(
//							String.valueOf(openadk.profiler.api.OIDs.ADK_INBOUND_TRANSFORMATIONS),
//							inboundObject.getElementDef(), null);
//		}
		try {
			mapInboundInternal(xpathContext, results, fields, version);
		} finally {
			if (BuildOptions.PROFILED)
				ProfilerUtils.profileStop();
		}
	}

	/**
	 * @param xpathContext
	 * @param results
	 * @param fields
	 * @param version
	 * @throws ADKMappingException
	 */
	private void mapInboundInternal(SIFXPathContext xpathContext,
			FieldAdaptor results, List<Mapping> fields, SIFVersion version)
			throws ADKMappingException {
		Mapping lastRule = null;
		try {
			for (Mapping mapping : fields) {
				lastRule = mapping;
				// JEN ListMapping
				if (mapping instanceof FieldMapping) {
					FieldMapping rule = (FieldMapping) mapping;
					SIFSimpleType val = mapInboundRule(rule, results,
							xpathContext, version);
					if (val != null)
						results.setSIFValue(rule.getFieldName(), val, rule);
				} else if (mapping instanceof ListMapping) {
					if (results instanceof ComplexFieldAdaptor) {
						ComplexFieldAdaptor complexFieldAdaptor = (ComplexFieldAdaptor) results;
						// Create Initial list
						mapInboundList((ListMapping) mapping,
								complexFieldAdaptor, xpathContext, version);
					} else {
						ADK.getLog()
								.warn("List Mapping ["
										+ mapping.toString()
										+ "] defined, but "
										+ "a ComplexFieldAdaptor was not used to perform the mapping");
					}
				}
			}
		} catch (Exception e) {
			if (lastRule != null) {
				throw new ADKMappingException("Unable to evaluate field rule: "
						+ lastRule.toString() + " : " + e.getMessage(), null, e);
			}
			throw new ADKMappingException(e.toString(), null, e);
		}
	}

	private void mapInboundList(ListMapping listMapping,
			ComplexFieldAdaptor adaptor, SIFXPathContext context,
			SIFVersion version) throws ADKSchemaException, ADKMappingException {

		// Use XPath to get a list of matchable nodes
		CompiledExpression ce = SIFXPathContext.compile(listMapping.getXPath());
		Iterator iterator = ce.iteratePointers(context);

		if (iterator != null && iterator.hasNext()) {
			IterableFieldAdaptor ifa = adaptor.addChildRelationship(listMapping
					.getChild());
			while (iterator.hasNext()) {
				Pointer p = (Pointer) iterator.next();
				SIFXPathContext rowContext = (SIFXPathContext) SIFXPathContext
						.newContext(context, p.getNode());
				FieldAdaptor row = ifa.addRow();
				List<Mapping> listRules = getListMappingRules(listMapping,
						version, MappingsDirection.OUTBOUND);
				if (listRules != null) {
					mapInboundInternal(rowContext, row, listRules, version);
				}
			}
		}

		/*
		 * 
		 * 
		 * SIFList listElements = xpathRule.evaluate(xpathContext);
		 * 
		 * if (listElements != null) { // String name = baseName + "/" +
		 * listMapping.getXPath(); String name = listMapping.getXPath();
		 * 
		 * IterableFieldAdaptor table = null; if (parent == null) table =
		 * adaptor.addChildRelationship(name); else table =
		 * parent.addChildRelationship(name);
		 * 
		 * for (int i = 0; i < listElements.size(); ++ i) { ComplexFieldAdaptor
		 * row = (ComplexFieldAdaptor) table.addRow(); //SIFElement element =
		 * listElements.get(i); //elementName =
		 * element.getElementDef().getClassName();
		 * 
		 * // Create new XPathContext based on this list SIFXPathContext
		 * currentContext = SIFXPathContext.newSIFContext(listElements.get(i));
		 * 
		 * for (Mapping mapping : listMapping.getMappings()) { if (mapping
		 * instanceof ListMapping) { mapInboundList((ListMapping)mapping,
		 * adaptor, currentContext, version, name, row); } else { FieldMapping
		 * fieldMapping = (FieldMapping) mapping; SIFSimpleType value =
		 * mapInboundRule(fieldMapping, adaptor, currentContext, version); if
		 * (value != null) row.setSIFValue( fieldMapping.getKey(), value, null
		 * ); } } } }
		 */
	}

	private SIFSimpleType mapInboundRule(FieldMapping rule,
			FieldAdaptor adaptor, SIFXPathContext xpathContext,
			SIFVersion version) throws ADKSchemaException {
		SIFSimpleType retval = null;

		if (!adaptor.hasField(rule.getFieldName())) {
			retval = rule.evaluate(xpathContext, version, true);
			if (retval != null) {
				if (rule.getValueSetID() != null && retval instanceof SIFString) {
					String currentValue = retval.toString();
					// Perform automatic ValueSet translation
					// TT 199. Perform a more detailed valueset translation.
					// If there is a default value set, use it if there is
					// no match found in the value set
					ValueSet vs = getValueSet(rule.getValueSetID(), true);
					if (vs != null) {
						currentValue = vs.translateReverse(currentValue,
								rule.getDefaultValue());
					}
					retval = new SIFString(currentValue);
				}
			}
		}
		return retval;
	}

	/**
	 * Populate a SIFDataObject from values in the supplied HashMap by
	 * evaluating all field rules for the associated SIF Data Object type. For
	 * each key in the <code>FieldAdaptor</code>, the corresponding field rule
	 * is evaluated to assign the <code>FieldAdaptor</code> value to the
	 * appropriate element or attribute in the SIFDataObject. If a field is not
	 * represented in the <code>FieldAdaptor</code>, its associated rule will
	 * not be evaluated.
	 * <p>
	 * 
	 * This method is intended to populate a new SIFDataObject instance when an
	 * agent is <i>publishing</i> objects to a zone. The other form of the
	 * <code>map</code> method is intended to obtain element and attribute
	 * values from a SIFDataObject <i>consumed</i> by the agent when processing
	 * SIF Events or SIF Responses.
	 * <p>
	 * 
	 * To use this method,
	 * <p>
	 * 
	 * <ol>
	 * <li>
	 * Create a <code>FieldAdaptor</code> and populate it with all known field
	 * values. The key of each entry must be the local application-defined name
	 * of the field -- the same field name used in field rules -- and the value
	 * is the string value to assign to the corresponding element or attribute
	 * in the SIFDataObject when a field rule matches.</li>
	 * <li>
	 * Create a SIFDataObject instance of the appropriate type (e.g. a
	 * com.edustructures.sifworks.student.StudentPersonal instance if the
	 * mapping will be applied to an incoming &lt;StudentPersonal&gt; message).</li>
	 * <li>
	 * Call this <code>map</code> method to apply all field values in the
	 * <code>FieldAdaptor</code> to corresponding elements and/or attributes in
	 * the SIFDataObject. The method first looks up the ObjectMapping instance
	 * corresponding to the SIF Data Object type. If no ObjectMapping has been
	 * defined for the object type, no action is taken and the method returns
	 * successfully without exception. Otherwise, all field rules defined by the
	 * ObjectMapping are evaluated in order.</li>
	 * </ol>
	 * 
	 * @param adaptor
	 *            A <code>FieldAdaptor</code> instance which returns field
	 *            values used to populate individual elements within the
	 *            supplied <code>SIFDataObject</code>.
	 * 
	 * @param dataObject
	 *            The SIFDataObject to assign field values to
	 * 
	 * @exception ADKMappingException
	 *                thrown if an error occurs while evaluating a field rule
	 */
	public void mapOutbound(FieldAdaptor adaptor, SIFDataObject dataObject)
			throws ADKMappingException {
		mapOutbound(adaptor, dataObject, new DefaultValueBuilder(adaptor));
	}

	/**
	 * Populate a SIFDataObject from values in the supplied
	 * <code>FieldAdaptor</code>.
	 * <p>
	 * 
	 * This form of the <code>map</code> method that accepts a custom
	 * <code>ValueBuilder</code> implementation to evaluate value expressions in
	 * XPath-like query strings. The <code>map</code> method uses the
	 * DefaultValueBuilder class as its built-in implementation, but you can
	 * supply your own by calling this method instead.
	 * <p>
	 * 
	 * @param adaptor
	 *            A <code>FieldAdaptor</code> instance which returns field
	 *            values used to populate individual elements within the
	 *            supplied <code>SIFDataObject</code>.
	 * 
	 * @param dataObject
	 *            The SIFDataObject to assign field values to
	 * 
	 * @param valueBuilder
	 *            A custom ValueBuilder implementation to evaluate value
	 *            expressions in XPath-like query strings
	 * 
	 * @exception ADKMappingException
	 *                thrown if an error occurs while evaluating a field rule
	 */
	public void mapOutbound(FieldAdaptor adaptor, SIFDataObject dataObject,
			ValueBuilder valueBuilder) throws ADKMappingException {
		mapOutbound(adaptor, dataObject, valueBuilder, ADK.getSIFVersion());
	}

	/**
	 * Populate a SIFDataObject from values in the supplied
	 * <code>FieldAdaptor</code>.
	 * <p>
	 * 
	 * This form of the <code>map</code> method allows the client to specify the
	 * SIFVersion that is in effect for this mapping operation
	 * 
	 * @param adaptor
	 *            A <code>FieldAdaptor</code> instance which returns field
	 *            values used to populate individual elements within the
	 *            supplied <code>SIFDataObject</code>.
	 * @param dataObject
	 *            The SIFDataObject to assign field values to
	 * @param version
	 *            The SIFVersion that should be used when evaluating this
	 *            mappings operation. The SIFVersion influences the XPaths that
	 *            are used to set elements in the data object
	 * 
	 * @throws ADKMappingException
	 */
	public void mapOutbound(FieldAdaptor adaptor, SIFDataObject dataObject,
			SIFVersion version) throws ADKMappingException {
		mapOutbound(adaptor, dataObject, new DefaultValueBuilder(adaptor, ADK
				.DTD().getFormatter(version)), version);
	}

	/**
	 * Populate a SIFDataObject from values in the supplied
	 * <code>FieldAdaptor</code>.
	 * <p>
	 * 
	 * This form of the <code>map</code> method allows the caller to specify
	 * whether it is performing an inbound or outbound mapping operation, as
	 * well as the version of SIF associated with the SIF Data Object that's
	 * being mapped. These values are used to filter field mapping rules. The
	 * direction flag is also used to invoke automatic ValueSet translations on
	 * fields that have a <i>ValueSet</i> attribute.
	 * <p>
	 * 
	 * @param adaptor
	 *            A <code>FieldAdaptor</code> instance which returns field
	 *            values used to populate individual elements within the
	 *            supplied <code>SIFDataObject</code>.
	 * @param element
	 *            The SIFDataObject to assign field values to
	 * @param valueBuilder
	 *            A custom ValueBuilder implementation to evaluate value
	 *            expressions in XPath-like query strings
	 * @param version
	 *            The SIFVersion associated with the mapping operation. For
	 *            inbound SIF_Event and SIF_Response messages, this value should
	 *            be obtained by calling <code>getSIFVersion</code> on the
	 *            <i>SIFMessageInfo</i> parameter passed to the message handler.
	 *            For inbound SIF_Request messages, it should be obtained by
	 *            calling the <code>SIFMessageInfo.getSIFRequestVersion</code>
	 *            method. For outbound messages, this value should be obtained
	 *            by calling <code>ADK.getSIFVersion</code> to get the version
	 *            of SIF the class framework was initialized with. Note when
	 *            this parameter is <code>null</code>, no SIF Version filtering
	 *            will be applied to field mapping rules.
	 * 
	 * @exception ADKMappingException
	 *                thrown if an error occurs while evaluating a field rule
	 * 
	 * @since ADK 1.5
	 */
	public void mapOutbound(FieldAdaptor adaptor, SIFElement element,
			ValueBuilder valueBuilder, SIFVersion version)
			throws ADKMappingException {

		ObjectMapping om = getRules(element.getElementDef().tag(version), true);
		if (om != null) {
			SIFXPathContext xpathContext = SIFXPathContext.newSIFContext(
					element, version);
			for (Map.Entry<String, String> prefixMapping : om.getAllNamespaceURIs(true).entrySet()) {
				xpathContext.registerNamespace(prefixMapping.getKey(), prefixMapping.getValue());
			}
			if (adaptor instanceof Variables) {
				xpathContext.setVariables((Variables) adaptor);
			}

			List<Mapping> list = getRulesList(version, om,
					MappingsDirection.OUTBOUND);
			mapOutbound(xpathContext, adaptor, element, list, valueBuilder,
					version);
		}

	}

	private List<Mapping> getRulesList(SIFVersion version, ObjectMapping om,
			MappingsDirection direction) {
		List<Mapping> list = om.getAllRulesList(true);
		// Remove any items that should be filtered out
		ListIterator<Mapping> it = list.listIterator();
		while (it.hasNext()) {
			Mapping fm = it.next();
			MappingsFilter filt = fm.getFilter();
			// Filter out this rule?
			if (filt != null) {
				if (!filt.evalDirection(direction)
						|| !filt.evalVersion(version)) {
					it.remove();
				}
			}
		}
		return list;
	}

	void mapOutbound(SIFXPathContext context, FieldAdaptor adaptor,
			SIFElement dataObject, List<Mapping> fields,
			ValueBuilder valueBuilder, SIFVersion version)
			throws ADKMappingException {

//		if (BuildOptions.PROFILED) {
//			ProfilerUtils
//					.profileStart(
//							String.valueOf(openadk.profiler.api.OIDs.ADK_OUTBOUND_TRANSFORMATIONS),
//							dataObject.getElementDef(), null);
//		}

		try {
			mapOutboundInternal(context, adaptor, dataObject, fields,
					valueBuilder, version);
		} finally {
			if (BuildOptions.PROFILED)
				ProfilerUtils.profileStop();

		}
	}

	/**
	 * @param context
	 * @param adaptor
	 * @param dataObject
	 * @param fields
	 * @param valueBuilder
	 * @param version
	 * @throws ADKMappingException
	 */
	private void mapOutboundInternal(SIFXPathContext context,
			FieldAdaptor adaptor, SIFElement dataObject, List<Mapping> fields,
			ValueBuilder valueBuilder, SIFVersion version)
			throws ADKMappingException {
		SIFFormatter textFormatter = ADK.getTextFormatter();
		Mapping lastRule = null;
		try {
			for (Mapping mapping : fields) {
				lastRule = mapping;
				if (mapping instanceof FieldMapping) {
					FieldMapping fm = (FieldMapping) mapping;

					SIFSimpleType mappedValue = mapOutBoundRule(fm, context,
							adaptor, dataObject, valueBuilder, version);
					if (mappedValue != null) {
						XPathRule rule = (XPathRule) fm.getRule();

						// At this point, mappedValue should not be null. We are
						// committed
						// to building out the path and setting the value.
						NodePointer pointer = null;
						// JEN Fix Element/Element path create error (i.e.
						// AlertMessage/AlertMessage)
						/*
						 * if
						 * (dataObject.getElementDef().name().equals(fm.getRule
						 * ().toString())) {
						 * dataObject.setField(dataObject.getElementDef(),
						 * mappedValue); } else {
						 */
						pointer = rule.createPath(context, version);
						/* } */

						// If the element/attribute does not have a value,
						// assign one.
						// If it does have a value, it was already assigned by
						// the XPath
						// rule in the lookupByXPath method above and should not
						// be
						// changed.
						//
						if (pointer != null) {
							Object pointedValue = pointer.getValue();

							if (pointedValue instanceof Element) {

								Element pointedElement = (Element) pointer
										.getValue();
								SIFSimpleType elementValue = pointedElement
										.getSIFValue();
								if (elementValue == null
										|| elementValue.getValue() == null) {
									if (mappedValue != null
											&& mappedValue instanceof SIFString) {
										// Now that we have the actual element,
										// we may need to create convert the
										// data if we were unable to resolve the
										// TypeConverter above. This only
										// happens
										// in cases involving surrogates where
										// the rule.lookupTargetDef(
										// dataObject.getElementDef() );
										// fails to find the target ElementDef
										SIFTypeConverter converter = pointedElement.getElementDef().getTypeConverter();
										if (converter != null && converter.getDataType() != mappedValue.getDataType()) {
											mappedValue = converter.parse(
													textFormatter,mappedValue.toString());
										}
									}

									// This check for null should really not be
									// necessary,
									// however keepingit in for now
									if (mappedValue != null) {
										pointedElement.setSIFValue(mappedValue);
									}
								}
							}
							else if ( pointer.getNode() instanceof org.w3c.dom.Node){
								if ( mappedValue != null ) {
									org.w3c.dom.Node node = (org.w3c.dom.Node)pointer.getNode();
									node.setTextContent(mappedValue.toString());
								}
							}
						}
					}
				} else if (mapping instanceof ListMapping) {
					System.out.println("Mapping is " + mapping);

					ListMapping listMapping = (ListMapping) mapping;
					if (adaptor instanceof ComplexFieldAdaptor)
						mapOutBoundList(context, (ComplexFieldAdaptor) adaptor,
								listMapping, valueBuilder, version);
				}
			}
		} catch (Exception e) {
			if (lastRule != null) {
				throw new ADKMappingException("Unable to evaluate field rule: "
						+ lastRule.toString() + " : " + e.getMessage(), null, e);
			}
			throw new ADKMappingException(e.toString(), null, e);
		}

	}

	/*
	 * Start of Andys Pseudo code
	 * 
	 * private void mapOutBoundList(SIFXPathContext context, ComplexFieldAdaptor
	 * adaptor, SIFElement dataObject, ListMapping listMapping ,ValueBuilder
	 * valueBuilder,SIFVersion version ) throws ADKMappingException {
	 * 
	 * IterableFieldAdaptor rowList =
	 * adaptor.getChildRelationship(listMapping.getChild()); if (rowList !=
	 * null) { for (FieldAdaptor row : rowList) { NodePointer pointer =
	 * listRule.createPath(context, version); } }
	 * 
	 * }
	 */
	private void mapOutBoundList(SIFXPathContext context,
			ComplexFieldAdaptor adaptor, ListMapping listMapping,
			ValueBuilder valueBuilder, SIFVersion version)
			throws ADKMappingException {

		IterableFieldAdaptor rowList = adaptor.getChildRelationship(listMapping
				.getChild());
		if (rowList != null) {
			for (FieldAdaptor row : rowList) {
				// for each row, execute the Xpath on the parent, which returns
				// individual rows
				// TODO: The following addition to the XPath forces the
				// SIFXPathContext class to
				// Create additional child rows each time. This should be
				// cleaned up using a more
				// elegant manner for readability. We also may need to support
				// xPath predicate
				// conditions in the List/@XPath attribute in mappings
				String fixedUp = listMapping.getXPath()
						+ "[adk:x() and adk:x()]";
				Pointer p = context.createPath(fixedUp);
				if (!(p.getNode() instanceof SIFElement)) {
					throw new IllegalStateException(
							context.getContextBean().getClass().getName()
									+ "/"
									+ listMapping.getXPath()
									+ ": Does not result in a complex type, which is required for list mappings.");
				}
				JXPathContext jContext = SIFXPathContext.newContext(context,
						p.getNode());
				SIFXPathContext rowContext = (SIFXPathContext) jContext;
				// Determine if this list mapping references an external node or
				// an embedded list of fields
				List<Mapping> listRules = getListMappingRules(listMapping,
						version, MappingsDirection.OUTBOUND);
				if (listRules != null) {
					mapOutbound(rowContext, row, (SIFElement) p.getNode(),
							listRules, valueBuilder, version);
				}
			}
		}

	}

	/**
	 * @param listMapping
	 * @param version
	 * @param direction
	 * @return
	 */
	private List<Mapping> getListMappingRules(ListMapping listMapping,
			SIFVersion version, MappingsDirection direction) {
		List<Mapping> listFields = null;

		String reference = listMapping.getReference();
		if (reference != null && reference.length() > 0) {
			ObjectMapping om = this.getRules(reference, true);
			if (om != null) {
				listFields = this.getRulesList(version, om, direction);
			}
		} else {
			listFields = listMapping.getMappings();
		}
		return listFields;
	}

	private SIFSimpleType mapOutBoundRule(FieldMapping fm,
			SIFXPathContext context, FieldAdaptor adaptor,
			SIFElement dataObject, ValueBuilder valueBuilder, SIFVersion version)
			throws ADKMappingException, ADKTypeParseException {

		SIFSimpleType mappedValue = null;
		SIFFormatter textFormatter = ADK.getTextFormatter();
		String fieldName = fm.getAlias();
		if (fieldName == null) {
			fieldName = fm.getFieldName();
		}

		if (fieldName == null || fieldName.length() == 0) {
			throw new ADKMappingException("Mapping rule for "
					+ dataObject.getElementDef().name() + "["
					+ fm.getFieldName() + "] must specify a field name", null);
		}

		if (adaptor.hasField(fieldName) || fm.hasDefaultValue()) {
			//
			// For outbound mapping operations, only process
			// XPathRules. All other rule types, like OtherIdRule,
			// are only intended to be used for inbound mappings.
			//
			if (fm.getRule() instanceof XPathRule) {
				XPathRule rule = (XPathRule) fm.getRule();
				// Lookup or create the element/attribute referenced by the rule
				String ruledef = rule.getXPath();
				if (ruledef == null || ruledef.trim().length() == 0) {
					throw new ADKMappingException(
							"Mapping rule for "
									+ dataObject.getElementDef().name()
									+ "[\""
									+ fieldName
									+ "\"] must specify a path to an element or attribute",
							null);
				}

				// TT 199 If the FieldMapping has an "ifnull" value of
				// "suppress",
				// don't render a result

				// Determine if this element should be created before attempting
				// to create it
				// If the value resolves to null and the IFNULL_SUPPRESS flag is
				// set, the element
				// should not be created. That's why we have to look up the
				// ElementDef first
				SIFTypeConverter typeConverter = null;
				ElementDef def = rule.lookupTargetDef(dataObject
						.getElementDef());
				if (def != null) {
					typeConverter = def.getTypeConverter();
				}
				if (typeConverter == null) {
					typeConverter = SIFTypeConverters.STRING;
					// TODO: Perhaps the following exception should be thrown
					// when
					// in STRICT mode
					// throw new ADKMappingException( "Element {" + def.name() +
					// "} from rule \"" + ruledef +
					// "\" does not have a data type definition.", null );
				}

				try {
					mappedValue = adaptor.getSIFValue(fieldName, typeConverter,
							fm);
				} catch (NumberFormatException nfe) {
					// TT 2998 Add support for ignoring type parse exceptions
					// on Outbound data. If it fails to parse, ignore
					if ((ADK.debug & ADK.DBG_MESSAGING_DETAILED) > 0) {
						// TODO: This decision should really be based upon the
						// adk.messaging.strictTypeParsing property. However,
						// mappings
						// don't currently have access to ADK Properties.
						// Therefore, this
						// needs to be set somewhere on the Mappings class
						String identifier = dataObject.getXmlId();
						ADK.getLog().warn(
								"Unable to parse value for outbound mapping for element"
										+ identifier == null ? "" : "("
										+ identifier + ")"
										+ ". Ignored error: "
										+ nfe.getMessage(), nfe);
					}
					return null;
				}

				// Perform a valueset translation, if applicable
				if (mappedValue != null && mappedValue instanceof SIFString
						&& fm.getValueSetID() != null) {
					String textValue = mappedValue.toString();
					// Perform automatic ValueSet translation
					ValueSet vs = getValueSet(fm.getValueSetID(), true);
					if (vs != null) {
						// TT 199. Perform a more detailed valueset translation.
						// If there is a default value for this field, use it if
						// there is
						// no match found in the value set
						textValue = vs.translate(textValue,
								fm.getDefaultValue());
					}
					mappedValue = new SIFString(textValue);
				}

				boolean usedDefault = false;
				if (mappedValue == null) {
					// If the FieldMapping has a Default value, use that, unless
					// it is explicitly suppressed
					if (fm.getNullBehavior() != FieldMapping.IFNULL_SUPPRESS
							&& fm.hasDefaultValue()) {
						mappedValue = fm.getDefaultValue(typeConverter,
								textFormatter);
						usedDefault = true;
					} else {
						return null;
					}
				}

				if (!usedDefault) {
					String valueExpression = rule.getValueExpression();
					if (valueExpression != null) {
						// This XPath rule has a value assignment expression at
						// the end of it
						String value = valueBuilder.evaluate(valueExpression);
						mappedValue = typeConverter.parse(textFormatter, value);
					}
				}

				// If we have a null value to assign at this point, move on to
				// the next rule
				if (mappedValue == null) {
					return null;
				} else if (mappedValue.getValue() == null
						&& fm.getNullBehavior() == FieldMapping.IFNULL_SUPPRESS) {
					return null;
				}

			}
		}
		return mappedValue;

	}

	/**
	 * Returns the SourceId filters in effect for this Mappings instance. A
	 * Mappings instances always inherits the filters of its ancestry.
	 * <p>
	 * 
	 * @return An array of SourceIds or null if this Mappings object applies to
	 *         all SIF Agents
	 * @see #setSourceIdFilter
	 */
	public String[] getSourceIdFilter() {
		if (fParent == getRoot())
			return fSourceIds;

		HashMap<String, Object> m = new HashMap<String, Object>();
		Mappings parent = this;
		while (parent.fParent != null) {
			String[] list = parent.fSourceIds;
			if (list != null) {
				for (int i = 0; i < list.length; i++)
					m.put(list[i], null);
			}

			parent = parent.fParent;
		}

		int i = 0;
		String[] arr = new String[m.size()];
		for (Iterator it = m.keySet().iterator(); it.hasNext();)
			arr[i++] = (String) it.next();

		return arr;
	}

	/**
	 * Returns the SourceId filters in effect for this Mappings instance as a
	 * comma-delimited string.
	 * <p>
	 * 
	 * @return A comma-delimited string of the SourceId in the filter
	 */
	public String getSourceIdFilterString() {
		StringBuffer b = new StringBuffer();
		if (fSourceIds != null) {
			for (int i = 0; i < fSourceIds.length; i++) {
				if (b.length() > 0)
					b.append(",");
				b.append(fSourceIds[i]);
			}
		}

		return b.toString();
	}

	/**
	 * Returns the Zone ID filters in effect for this Mappings instance as a
	 * comma-delimited string.
	 * <p>
	 * 
	 * @return A comma-delimited string of the SourceId in the filter
	 */
	public String getZoneIdFilterString() {
		StringBuffer b = new StringBuffer();
		if (fZoneIds != null) {
			for (int i = 0; i < fZoneIds.length; i++) {
				if (b.length() > 0)
					b.append(",");
				b.append(fZoneIds[i]);
			}
		}

		return b.toString();
	}

	/**
	 * Returns the SIF Version filters in effect for this Mappings instance as a
	 * comma-delimited string.
	 * <p>
	 * 
	 * @return A comma-delimited string of the SourceId in the filter
	 */
	public String getSIFVersionFilterString() {
		StringBuffer b = new StringBuffer();
		if (fSifVersions != null) {
			for (int i = 0; i < fSifVersions.length; i++) {
				if (b.length() > 0)
					b.append(",");
				b.append(fSifVersions[i].toString());
			}
		}

		return b.toString();
	}

	/**
	 * Returns the ZoneId filters in effect for this Mappings instance. A
	 * Mappings instances always inherits the filters of its ancestry.
	 * <p>
	 * 
	 * @return An array of ZoneIds or null if this Mappings object applies to
	 *         all zones
	 * @see #setZoneIdFilter
	 */
	public String[] getZoneIdFilter() {
		if (fParent == getRoot())
			return fZoneIds;

		HashMap<String, Object> m = new HashMap<String, Object>();

		Mappings parent = this;
		while (parent.fParent != null) {
			String[] list = parent.fZoneIds;
			if (list != null) {
				for (int i = 0; i < list.length; i++)
					m.put(list[i], null);
			}

			parent = parent.fParent;
		}

		int i = 0;
		String[] arr = new String[m.size()];
		for (Iterator it = m.keySet().iterator(); it.hasNext();)
			arr[i++] = (String) it.next();

		return arr;
	}

	/**
	 * Returns the SIFVersion filters in effect for this Mappings instance. A
	 * Mappings instances always inherits the filters of its ancestry.
	 * <p>
	 * 
	 * @return An array of SIFVersion objects or null if this Mappings object
	 *         applies to all versions of SIF
	 * @see #setSIFVersionFilter
	 */
	public SIFVersion[] getSIFVersionFilter() {
		if (fParent == getRoot())
			return fSifVersions;

		HashMap<SIFVersion, Object> m = new HashMap<SIFVersion, Object>();

		Mappings parent = this;
		while (parent.fParent != null) {
			SIFVersion[] list = parent.fSifVersions;
			if (list != null) {
				for (int i = 0; i < list.length; i++)
					m.put(list[i], null);
			}

			parent = parent.fParent;
		}

		int i = 0;
		SIFVersion[] arr = new SIFVersion[m.size()];
		for (Iterator it = m.keySet().iterator(); it.hasNext();)
			arr[i++] = (SIFVersion) it.next();

		return arr;
	}

	/**
	 * Determines if this nested Mappings instance allows the specified
	 * SourceId.
	 * <p>
	 * 
	 * This method is only called on nested children of a Mappings object, never
	 * on a top-level Mappings object because top-level parents always allow all
	 * ZoneIds, SourceIds, and SIF Versions.
	 * <p>
	 * 
	 * @param sourceId
	 *            An agent's SourceId
	 * 
	 * @return true if the SourceId is permitted by the SourceId filter in
	 *         effect and can therefore be considered for selection by the
	 *         <code>select</code> method; false if the SourceId is not included
	 *         in the SourceId filter and should therefore not be considered for
	 *         selection
	 */
	public int allowsSourceId(String sourceId) {
		// Allows all SourceIds?
		if (fSourceIds == null || fSourceIds.length == 0)
			return 0;

		for (int i = 0; i < fSourceIds.length; i++) {
			if (fSourceIds[i].equals(sourceId))
				return 1;
		}

		return -1;
	}

	/**
	 * Determines if this nested Mappings instance allows the specified ZoneId.
	 * <p>
	 * 
	 * This method is only called on nested children of a Mappings object, never
	 * on a top-level Mappings object because top-level parents always allow all
	 * ZoneIds, SourceIds, and SIF Versions.
	 * <p>
	 * 
	 * @param zoneId
	 *            A ZoneId
	 * 
	 * @return true if the ZoneId is permitted by the ZoneId filter in effect
	 *         and can therefore be considered for selection by the
	 *         <code>select</code> method; false if the ZoneId is not included
	 *         in the ZoneId filter and should therefore not be considered for
	 *         selection
	 */
	public int allowsZoneId(String zoneId) {
		// Allows all zones?
		if (fZoneIds == null || fZoneIds.length == 0)
			return 0;

		for (int i = 0; i < fZoneIds.length; i++) {
			if (fZoneIds[i].equals(zoneId))
				return 1;
		}

		return -1;
	}

	/**
	 * Determines if this nested Mappings instance allows the specified version
	 * of SIF
	 * <p>
	 * 
	 * This method is only called on nested children of a Mappings object, never
	 * on a top-level Mappings object because top-level parents always allow all
	 * ZoneIds, SourceIds, and SIF Versions.
	 * <p>
	 * 
	 * @param version
	 *            A SIFVersion instance describing a version of SIF
	 * 
	 * @return true if the version is permitted by the SIFVersion filter in
	 *         effect and can therefore be considered for selection by the
	 *         <code>select</code> method; false if the version is not included
	 *         in the SIFVersion filter and should therefore not be considered
	 *         for selection
	 */
	public int allowsVersion(SIFVersion version) {
		// Allows all versions?
		if (fSifVersions == null || fSifVersions.length == 0)
			return 0;

		for (int i = 0; i < fSifVersions.length; i++) {
			if (fSifVersions[i].equals(version))
				return 1;
		}

		return -1;
	}

	/**
	 * Add a ValueSet to this Mappings instance
	 * 
	 * @param vset
	 *            The ValueSet instance to add to this Mappings instance
	 */
	public void addValueSet(ValueSet vset) {
		if (fValueSets == null)
			fValueSets = new HashMap<String, ValueSet>();

		fValueSets.put(vset.fId, vset);
		if (vset.fNode == null && fNode != null) {
			// Create a new <valueset>
			org.w3c.dom.Element element = fNode.getOwnerDocument()
					.createElement(XML_VALUESET);
			vset.fNode = element;
			fNode.appendChild(element);
			vset.toXml(element);
		}
	}

	/**
	 * Remove a ValueSet from this Mappings instance.
	 * <p>
	 * 
	 * If the ValueSet has a DOM Node attached to it, it is removed from its
	 * parent Node and dereferenced.
	 * <p>
	 * 
	 * @param vset
	 *            The ValueSet to remove
	 */
	public void removeValueSet(ValueSet vset) {
		if (fValueSets != null) {
			if (vset.fNode != null) {
				// Remove the ValueSet's node from its parent's DOM Node
				vset.fNode.getParentNode().removeChild(vset.fNode);

				// Remove all children and dereference the entrys' Nodes
				ValueSetEntry[] entries = vset.getEntries();
				for (int i = 0; i < entries.length; i++) {
					if (entries[i].node != null
							&& entries[i].node.getParentNode() != null) {
						entries[i].node.getParentNode().removeChild(
								entries[i].node);
					}
					entries[i].node = null;
				}
			}

			// Remove from the lookup table
			fValueSets.remove(vset.fId);

			// Dereference the node
			vset.fNode = null;
		}
	}

	/**
	 * Remove a ValueSet from this Mappings instance.
	 * <p>
	 * 
	 * If a ValueSet with the specified ID is found, it is removed from this
	 * Mappings and returned; otherwise no action is taken. If the ValueSet has
	 * a DOM Node attached to it, it is removed from its parent Node and
	 * dereferenced.
	 * <p>
	 * 
	 * @param id
	 *            The ID of the ValueSet to remove
	 * @return The ValueSet that was removed from this Mappings instance or NULL
	 */
	public ValueSet removeValueSet(String id) {
		ValueSet vs = getValueSet(id, false);
		if (vs != null) {
			removeValueSet(vs);
		}

		return vs;
	}

	/**
	 * Gets a ValueSet by ID.
	 * <p>
	 * 
	 * @param id
	 *            The unique ID of the ValueSet
	 * @param inherit
	 *            true to inherit the ValueSet from the Mappings ancestry if not
	 *            found as a child of this Mappings object; false to return null
	 *            if no ValueSet is found
	 * @return The ValueSet that was found or Null
	 */
	public ValueSet getValueSet(String id, boolean inherit) {
		if (!inherit)
			return fValueSets != null ? (ValueSet) fValueSets.get(id) : null;

		Mappings m = this;
		ValueSet vs = null;
		while (m != null && vs == null) {
			if (m.fValueSets != null)
				vs = m.fValueSets.get(id);
			m = m.getParent();
		}

		return vs;
	}

	/**
	 * Gets all ValueSets for this Mappings instance.
	 * <p>
	 * 
	 * @param inherit
	 *            true to include ValueSets from the Mappings ancestry in the
	 *            returned array, false to include only ValueSets defined by
	 *            this Mappings object
	 * @return The ValueSets that are defined by this Mappings instance
	 */
	public ValueSet[] getValueSets(boolean inherit) {
		HashMap<String, ValueSet> results = new HashMap<String, ValueSet>();

		Mappings m = this;

		do {
			if (m.fValueSets != null) {
				for (Iterator it = m.fValueSets.values().iterator(); it
						.hasNext();) {
					ValueSet vs = (ValueSet) it.next();
					if (vs != null && !results.containsKey(vs.fId))
						results.put(vs.fId, vs);
				}
			}

			m = m.getParent();
		} while (m != null && inherit);

		int i = 0;
		ValueSet[] arr = new ValueSet[results.size()];
		for (Iterator it = results.values().iterator(); it.hasNext();)
			arr[i++] = (ValueSet) it.next();

		return arr;
	}

	/**
	 * Add an ObjectMapping definition to this Mappings instance
	 * 
	 * @param om
	 *            The ObjectMapping to add to this Mappings instance
	 */
	public void addRules(ObjectMapping om) {
		addRules(om, true);
	}

	/**
	 * Add an ObjectMapping definition to this Mappings instance
	 */
	protected void addRules(ObjectMapping om, boolean buildDomTree) {
		if (om.fParent != null)
			throw new IllegalStateException(
					"ObjectMapping is already a child of a Mappings instance");

		om.fParent = this;

		if (fObjRules == null) {
			fObjRules = new HashMap<String, ObjectMapping>();
		}
		fObjRules.put(om.getObjectType(), om);

		if (om.fNode == null && buildDomTree && fNode != null) {
			om.fNode = fNode.getOwnerDocument().createElement(XML_OBJECT);
			if (om.getObjectType() != null)
				XMLUtils.setAttribute(om.fNode, "object", om.getObjectType());
			fNode.appendChild(om.fNode);
		}
	}

	/**
	 * Remove an ObjectMapping definition from this Mappings instance
	 * 
	 * @param om
	 *            The ObjectMapping definition to remove
	 */
	public void removeRules(ObjectMapping om) {
		if (fObjRules != null) {
			if (fNode != null && om.fNode != null) {
				fNode.removeChild(om.fNode);
			}

			fObjRules.remove(om.getObjectType());
		}
	}

	/**
	 * Gets all ObjectMappings defined for this Mappings instance, including
	 * those inherited by its parents.
	 * <p>
	 * 
	 * @return An array of all ObjectMappings
	 */
	public ObjectMapping[] getObjectMappings() {
		return getObjectMappings(true);
	}

	/**
	 * Gets all ObjectMappings defined for this Mappings instance, optionally
	 * including those inherited by its parents.
	 * <p>
	 * 
	 * @param inherit
	 *            True if ObjectMappings defined by parents should be included
	 *            as well.
	 * @return An array of all ObjectMappings
	 */
	public ObjectMapping[] getObjectMappings(boolean inherit) {
		HashMap<String, ObjectMapping> set = new HashMap<String, ObjectMapping>();

		Mappings m = this;
		while (m != null) {
			if (m.fObjRules != null) {
				for (Iterator it = m.fObjRules.values().iterator(); it
						.hasNext();) {
					ObjectMapping om = (ObjectMapping) it.next();
					if (!set.containsKey(om.getObjectType()))
						set.put(om.getObjectType(), om);
				}
			}

			if (inherit)
				m = m.fParent;
			else
				m = null;
		}

		int i = 0;
		ObjectMapping[] arr = new ObjectMapping[set.size()];
		for (Iterator it = set.values().iterator(); it.hasNext();) {
			arr[i++] = (ObjectMapping) it.next();
		}

		return arr;
	}

	/**
	 * Gets the ObjectMapping defined for the specified object type from this
	 * Mappings instance, optionally including those inherited by its parents.
	 * <p>
	 * 
	 * @param objectType
	 *            The object type, such as "StudentPersoanal
	 * @param inherit
	 *            True if an ObjectMapping from the parent Mappings instance can
	 *            be returned
	 * @return An ObjectMapping instance
	 */
	public ObjectMapping getObjectMapping(String objectType, boolean inherit) {
		if (!inherit) {
			return fObjRules == null ? null : (ObjectMapping) fObjRules
					.get(objectType);
		}

		Mappings m = this;

		while (m != null) {
			if (m.fObjRules != null) {
				if (m.fObjRules.containsKey(objectType)) {
					return m.fObjRules.get(objectType);
				}
			}

			m = m.fParent;
		}

		return null;
	}

	/**
	 * Return an ObjectMapping definition for a given object type.
	 * 
	 * @param object
	 *            A SIF Data Object (e.g. "StudentPersonal")
	 * @param inherit
	 *            True to inherit the ObjectMapping from the ancestry if this
	 *            Mappings instance does not define it
	 * @return an ObjectMapping definition for the given object type.
	 */
	public ObjectMapping getRules(SIFDataObject object, boolean inherit) {
		return object != null ? getRules(object.getElementDef().name(), inherit)
				: null;
	}

	/**
	 * Returns the ObjectMapping for a given object type.
	 * <p>
	 * 
	 * @param objType
	 *            The name of a SIF Data Object (e.g. "StudentPersonal")
	 * @param inherit
	 *            True to inherit the ObjectMapping from the ancestry if this
	 *            Mappings instance does not define it
	 * @return The ObjectMappints instance for the given object type
	 */
	public ObjectMapping getRules(String objType, boolean inherit) {
		Mappings m = this;
		while (m != null) {
			if (m.fObjRules != null) {
				for (Iterator it = m.fObjRules.values().iterator(); it
						.hasNext();) {
					ObjectMapping om = (ObjectMapping) it.next();
					if (om.getObjectType().equals(objType))
						return om;
				}
			}

			if (inherit)
				m = m.fParent;
			else
				m = null;
		}

		return null;
	}

	/**
	 * Gets a named attribute of a Node
	 * <p>
	 * 
	 * @param node
	 *            The node to search
	 * @param attr
	 *            The name of the attribute to get
	 */
	private String getAttribute(Node node, String attr) {
		if (node == null)
			return null;

		NamedNodeMap map = node.getAttributes();
		if (map != null) {
			Node an = map.getNamedItem(attr);
			if (an != null)
				return an.getNodeValue();
		}

		return null;
	}

	/**
	 * Stores all configuration information from this Mappings instance to the
	 * specified DOM document
	 * 
	 * @param doc
	 * @return The root DOM node created by this method
	 * @throws SAXException
	 */
	public Node toDOM(Document doc) throws SAXException {
		return toDOM(doc, false);
	}

	/**
	 * Produces a DOM Node graph of this Mappings object.</p>
	 * 
	 * NOTE: This method creates the node but does not append it to the document
	 * that is passed in. It is the responsibility of the caller to add the node
	 * where appropriate
	 * 
	 * @param doc
	 *            The parent document
	 * @param renderAsRuntimeMappings
	 *            true to inherit object and field rules so this Mappings is
	 *            rendered with dynamic content as it would be evaluated at
	 *            runtime. This can be useful for diagnostics (i.e. displaying
	 *            the current state of a Mappings object), but not for rendering
	 *            to a configuration file.
	 * @return A DOM Node graph representing this Mappings object
	 * @throws SAXException
	 */
	public Node toDOM(Document doc, boolean renderAsRuntimeMappings)
			throws SAXException {
		if (doc == null)
			return null;

		Node mappingsNode = doc.createElement(XML_MAPPINGS);
		XMLUtils.setAttribute(mappingsNode, "id", fId == null ? "" : fId);

		if (fSifVersions != null && fSifVersions.length > 0) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < fSifVersions.length; i++) {
				if (i != 0)
					buf.append(",");
				buf.append(fSifVersions[i]);
			}
			XMLUtils.setAttribute(mappingsNode, "sifVersion", buf.toString());
		}

		if (fSourceIds != null && fSourceIds.length > 0) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < fSourceIds.length; i++) {
				if (i != 0)
					buf.append(",");
				buf.append(fSourceIds[i]);
			}
			XMLUtils.setAttribute(mappingsNode, "sourceId", buf.toString());
		}

		if (fZoneIds != null && fZoneIds.length > 0) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < fZoneIds.length; i++) {
				if (i != 0)
					buf.append(",");
				buf.append(fZoneIds[i]);
			}
			XMLUtils.setAttribute(mappingsNode, "zoneId", buf.toString());
		}

		// Write out each <object>...
		ObjectMapping[] objects = getObjectMappings(renderAsRuntimeMappings);
		for (int i = 0; i < objects.length; i++) {
			Node objNode = doc.createElement(XML_OBJECT);
			mappingsNode.appendChild(objNode);
			XMLUtils.setAttribute(objNode, XML_OBJECT,
					objects[i].getObjectType());

			// Write out each <field>...
			Mapping[] rules = objects[i].getRules(renderAsRuntimeMappings);
			for (int f = 0; f < rules.length; f++) {
				org.w3c.dom.Element fieldNode = doc.createElement(XML_FIELD);
				objNode.appendChild(fieldNode);
				rules[f].toXML(fieldNode);
			}
		}
		// Write out each <valueset>...
		ValueSet[] valuesets = this.getValueSets(renderAsRuntimeMappings);
		for (int i = 0; i < valuesets.length; i++) {
			org.w3c.dom.Element valuesetNode = doc.createElement(XML_VALUESET);
			valuesets[i].toXml(valuesetNode);
			mappingsNode.appendChild(valuesetNode);
		}

		// Copy any children mappings
		Mappings[] children = this.getChildren();
		for (int a = 0; a < children.length; a++) {
			Node childNode = children[a].toDOM(doc);
			mappingsNode.appendChild(childNode);
		}

		return mappingsNode;
	}

	/**
	 * Returns the Mappings as a string in XML form
	 * 
	 * @return A string representing the XML form of this Mappings instance
	 */
	public String toXML() {
		return toXML(false);
	}

	/**
	 * Returns the Mappings as a string in XML form.
	 * <p>
	 * 
	 * @param renderAsRuntimeMappings
	 *            true to inherit object and field rules so this Mappings is
	 *            rendered with dynamic content as it would be evaluated at
	 *            runtime. This can be useful for diagnostics (i.e. displaying
	 *            the current state of a Mappings object), but not for rendering
	 *            to a configuration file.
	 * @return A string representing the XML form of this Mappings instance
	 */
	public String toXML(boolean renderAsRuntimeMappings) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.newDocument();

			Node n = this.toDOM(dom, renderAsRuntimeMappings);
			dom.appendChild(n);

			// Populate
			OutputFormat format = new OutputFormat(dom);
			format.setLineWidth(65);
			format.setIndenting(true);
			format.setIndent(4);
			format.setLineSeparator("\r\n");
			StringWriter sw = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(sw, format);
			serializer.serialize(dom);
			return sw.toString();
		} catch (Exception ex) {
			return "<error>" + ex + "</error>";
		}

	}

	class Candidate {
		Mappings fMapping;
		int restrictiveness;

		Candidate(Mappings candidateMapping) {
			this.fMapping = candidateMapping;

			// The "restrictiveness" score is based on a count of all filters
			String[] f = fMapping.getZoneIdFilter();
			if (f != null)
				restrictiveness += f.length;
			f = fMapping.getSourceIdFilter();
			if (f != null)
				restrictiveness += f.length;
			SIFVersion[] v = fMapping.getSIFVersionFilter();
			if (v != null)
				restrictiveness += v.length;
		}
	}

	/**
	 * Displays the Mappings defined by a configuration file
	 * 
	 * Usage: Mappings config.xml [[/zone=zoneId /sifVersion=version
	 * /sourceId=sourceId] /v]
	 * 
	 * When the /zone, /sifVersion, or /sourceId parameters are specified, the
	 * Mappings.select method is called to select the Mappings object that most
	 * closely matches the Zone(s), SIF Version, and SourceId value(s). All
	 * three options must be specified. When these options are not present, all
	 * Mappings are written to System.out
	 * 
	 * When the /v option is specified, the configuration file is parsed with a
	 * validating parser.
	 * 
	 * @param args
	 *            The command-line arguments, as documented, used by this method
	 */
	public static void main(String[] args) {
		try {
			if (args.length == 0 || args[0].equals("/?")
					|| args[0].equalsIgnoreCase("/h")) {
				_printUsage();
				return;
			}

			String id = null;
			String srcId = null;
			String zoneId = null;
			String version = null;
			String toSIF = null;
			String fromSIF = null;
			String file = null;

			boolean validate = false;
			boolean toTable = false;

			try {
				for (int i = 1; i < args.length; i++) {
					if (args[i].startsWith("/") && args[i].length() >= 2) {
						switch (Character.toUpperCase(args[i].charAt(1))) {
						case 'X':
							validate = true;
							break;
						case 'I':
							id = args[i].substring(3);
							break;
						case 'Z':
							zoneId = args[i].substring(3);
							break;
						case 'S':
							srcId = args[i].substring(3);
							break;
						case 'V':
							version = args[i].substring(3);
							break;
						case 'T':
							toSIF = args[i].substring(7);
							file = args[++i];
							break;
						case 'F':
							fromSIF = args[++i];
							break;
						case 'W':
							toTable = true;
							break;
						case 'H':
						case '?':
							_printUsage();
							return;

						default:
							System.out.println(args[i]
									+ " not a recognized option");
						}
					}
				}
			} catch (Throwable thr) {
				System.out.println(thr);
				_printUsage();
				return;
			}

			// Initialize the ADK
			ADK.initialize();

			// Parse the configuration file
			openadk.library.tools.cfg.AgentConfig config = new openadk.library.tools.cfg.AgentConfig();
			config.read(args[0], validate);

			// Select the Mappings object with the specified ID
			Mappings m = config.getMappings();
			if (m == null) {
				System.out
						.println("Configuration file does not define a <mappings> element");
				return;
			}
			m = m.getMappings(id == null ? "Default" : id);
			if (m == null) {
				System.out.println("Mappings[" + id + "] not found");
				return;
			}

			SIFVersion sifVersion = version == null ? null : SIFVersion
					.parse(version);
			// Select the best Mappings match based on the SourceId, ZoneId, and
			// SifVersion
			m = m.select(zoneId, srcId, sifVersion);

			if (toSIF != null) {
				// The "/toSIF:" option was used...
				Properties props = new Properties();
				FileInputStream in = new FileInputStream(file);
				props.load(in);
				in.close();

				// Create a new instance of the named SIFDataObject
				SIFDataObject sdo = ADK.DTD().createSIFDataObject(
						ADK.DTD().lookupElementDef(toSIF));

				// Apply the Mappings

				StringMapAdaptor propsMapper = new StringMapAdaptor(props);
				m.mapOutbound(propsMapper, sdo);

				// Render the SIFDataObject to the console
				SIFWriter out = new SIFWriter(System.out);
				out.write(sdo);
				out.close();
			} else if (fromSIF != null) {
				// The "/fromSIF" option was used...
				SIFParser p = SIFParser.newInstance();
				FileReader in = new FileReader(fromSIF);
				SIFDataObject sdo = (SIFDataObject) p.parse(in, null);
				in.close();

				// Apply the Mappings
				HashMap<String, String> map = new HashMap<String, String>();
				StringMapAdaptor table = new StringMapAdaptor(map);
				m.mapInbound(sdo, table);

				// Display the table
				for (Iterator<String> it = map.keySet().iterator(); it
						.hasNext();) {
					String name = it.next();
					String value = map.get(name);
					System.out.println(name + "=" + value);
				}
			}
			if (toTable = true) {
				PrintWriter pw = new PrintWriter(new FileWriter(
						"AgentMappings.html"));
				pw.println("<html>\n\t<head>\n\t\t<title>Agent Mappings for "
						+ config.getSourceId() + "</title>");
				pw.println("\t\t<style type='text/css'>");
				pw.println("\t\t\ttable{border: thin solid black;border-collapse: collapse;}");
				pw.println("\t\t\tth{background-color: black;color:white;padding-left:7px;padding-right:7px;padding-top:3px;padding-bottom:1px;}");
				pw.println("\t\t\ttd{border: thin solid black;padding-left:5px;padding-right:5px;}");
				pw.println("\t\t</style>");
				pw.println("\t</head>");
				pw.println("\t<body>");
				pw.println("<h1>Agent Mappings for " + config.getSourceId()
						+ "</h1>");
				for (ObjectMapping om : m.getObjectMappings()) {
					pw.println("\t<h3>" + om.getObjectType() + "</h3>");
					pw.println("\t<table>");
					pw.println("\t\t<tr>");
					pw.println("\t\t\t<th>Field Code</th>");
					pw.println("\t\t\t<th>" + om.getObjectType()
							+ " Element/Attribute" + "</th>");
					pw.println("\t\t\t<th>SIF Version</th>");
					pw.println("\t\t</tr>");
					for (FieldMapping rule : om.getRules(true)) {
						pw.println("\t\t<tr>");
						String fieldName = rule.getFieldName();
						if (rule.getAlias() != null) {
							fieldName = rule.getAlias();
						}
						pw.println("\t\t\t<td>" + fieldName + "</td>");
						pw.println("\t\t\t<td>" + rule.getRule().toString()
								+ "</td>");

						String renderVersion = "All";
						MappingsFilter filter = rule.getFilter();
						if (filter != null && filter.hasVersionFilter()) {
							String filterVersion = filter.fVersion;
							if (filterVersion.startsWith("=")) {
								renderVersion = filterVersion.substring(1);
							} else if (filterVersion.startsWith("+")) {
								renderVersion = "&gt;="
										+ filterVersion.substring(1);
							} else if (filterVersion.startsWith("-")) {
								renderVersion = "&lt;="
										+ filterVersion.substring(1);
							} else {
								renderVersion = version;
							}
						}
						pw.println("\t\t\t<td>" + renderVersion + "</td>");
						pw.println("\t\t</tr>");

					}
					pw.println("\t</table>");
				}
				pw.println("\t</body>");
				pw.println("</html>");
				pw.flush();
				pw.close();

			} else {
				// Render the Mappings to System.out
				System.out.println("Mappings["
						+ (m.getId() == null ? "" : m.getId()) + "]:");
				System.out.println(m.toXML(true));
				System.out.println();
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	private static void _printUsage() {
		System.out
				.println("\nThe Mappings class can be run as a program to display the Mappings that would");
		System.out
				.println("be selected at runtime given a set of ZoneId, SourceId, and SIF Version ");
		System.out
				.println("values. It can also be used to map a table of values to a SIFDataObject by");
		System.out
				.println("using the /toSIF option, or a SIFDataObject to a table of values by using the");
		System.out
				.println("/fromSIF option. Use this program to test Mappings configurations.");
		System.out.println();
		System.out
				.println("Usage: Mappings config.xml /i:MappingsId /z:ZoneId /v:SifVersion /s:SourceId [/x] [/toSIF:StudentPersonal table.properties] [/fromSIF:sdo.xml]");
		System.out.println();
		System.out
				.println("Example: The following example selects the Mappings object from the agent.cfg");
		System.out
				.println("configuration file that is the best match when the ZoneId is 'MyZone', the SIF");
		System.out
				.println("Version is 1.0r1, and the SourceId is 'MyAgent'. The contents of the Mappings");
		System.out.println("object is printed to the console:");
		System.out.println();
		System.out
				.println("Mappings agent.cfg /i:Default /z:MyZone /v:1.0r1 /s:MyAgent");
		System.out.println();
		System.out
				.println("Example: The following example reads a list of name=value pairs from a file");
		System.out
				.println("named values.prop to produce a <StudentPersonal> object.");
		System.out.println();
		System.out
				.println("Mappings agent.cfg /i:Default /toSIF:StudentPersonal values.prop");
		System.out.println();
		System.out
				.println("Example: The following example reads a <StudentPersonal> object from a file");
		System.out
				.println("named sdo.xml to produce a table of name/value pairs. The /s option is");
		System.out
				.println("used to select the Mappings object defined for the 'MyAgent' SourceId.");
		System.out.println();
		System.out
				.println("Mappings agent.cfg /i:Default /s:MyAgent /fromSIF sdo.xml");
		System.out.println();
		System.out
				.println("Example: The following creates a simple table, listing the");
		System.out.println("mappings configuration in HTML format");
		System.out.println();
		System.out.println("Mappings agent.cfg /W");
		System.out.println();
	}
}
