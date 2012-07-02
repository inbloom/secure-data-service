//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.xpath;

import openadk.library.Element;
import openadk.library.SIFElement;
import openadk.library.SIFVersion;
import openadk.library.SIFWriter;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.ClassFunctions;
import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.FunctionLibrary;
import org.apache.commons.jxpath.Functions;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathContextFactory;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.apache.commons.jxpath.ri.axes.InitialContext;
import org.apache.commons.jxpath.ri.axes.RootContext;
import org.apache.commons.jxpath.ri.compiler.CoreOperationAnd;
import org.apache.commons.jxpath.ri.compiler.CoreOperationEqual;
import org.apache.commons.jxpath.ri.compiler.Expression;
import org.apache.commons.jxpath.ri.compiler.ExtensionFunction;
import org.apache.commons.jxpath.ri.compiler.LocationPath;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.compiler.Path;
import org.apache.commons.jxpath.ri.compiler.Step;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.beans.NullPointer;
import org.apache.commons.jxpath.ri.model.beans.NullPropertyPointer;


import org.apache.commons.jxpath.ri.Compiler;
import org.w3c.dom.Node;
/**
 * SIFXPathContext provides APIs for the traversal of SIF Data Objects (SDO)
 * using the XPath syntax.
 * <p>
 * 
 * <h2>SIFXPath Interprets XPath Syntax on SIF Data Objects</h2>
 * 
 * SIFXPath is based on JXPath and uses an intuitive interpretation of the XPath
 * syntax in the traversal of SIF Data Objects.
 * 
 * Here are some examples:
 * 
 * <h3>Example 1: Simple Property Access</h3>
 * 
 * SIFXPath can be used to access simple properties of SIF Data Objects.
 * 
 * <blockquote>
 * <pre>
 *  StudentPersonal student = new StudentPersonal();
 *  student.setRefId(  ADK.makeGUID() );
 *  student.setName( new Name( NameType.LEGAL, &quot;Johnson&quot;, &quot;Tim&quot; ) );
 * 
 *  SIFXPathContext context = SIFXPathContext.newSIFContext( student );
 *  // Print out the RefId of the Student
 *  System.out.println( &quot;RefId: &quot; + context.getValue( &quot;@RefId&quot; ) );
 * 	    
 *  // Print out the Last Name of the Student
 *  System.out.println( &quot;Last Name: &quot; + context.getValue( &quot;Name/LastName&quot; ) );
 * </pre>
 * </blockquote>
 * 
 * <h3>Example 2: Iterating an array using XPath</h3>
 * 
 * SIFXPath can be used to iterate over arrays within SIF Data Objects.
 * 
 * <pre>
 * &lt;blockquote&gt;
 *  StudentPersonal student = new StudentPersonal();
 *  PhoneNumberList pnl = new PhoneNumberList();
 *  pnl.add( new PhoneNumber( PhoneNumberType.ANSWERING_SERVICE, &quot;555-555-2224&quot; ) );
 *  pnl.add( new PhoneNumber( PhoneNumberType.PRIMARY, &quot;555-555-6521&quot; ) );
 *  pnl.add( new PhoneNumber( PhoneNumberType.FAX, &quot;555-555-8745&quot; ) );
 *  pnl.add( new PhoneNumber( PhoneNumberType.MEDIA_CONFERENCE, &quot;555-555-3215&quot; ) );
 *  student.setPhoneNumberList( pnl );
 * 
 *  SIFXPathContext context = SIFXPathContext.newSIFContext( student );
 *  // Print out the phone numbers of the student
 *  Iterator phones = context.iterate(&quot;PhoneNumberList/PhoneNumber&quot;);
 *  System.out.println( &quot;Phone Numbers....&quot; );
 *  while( phones.hasNext() ) {
 *  	PhoneNumber phone = (PhoneNumber)phones.next();
 *  	System.out.println( &quot;Type: &quot; + phone.getType() + &quot; Number: &quot; + phone.getNumber() );
 *  }
 * </pre>
 * 
 * </blockquote>
 * 
 * <h3>Example 3: Using advanced iterating</h3>
 * 
 * SIFXPath can be used to find elements that match specific criteria. In this
 * example, all elements that have a text value are returned
 * 
 * <pre>
 * &lt;blockquote&gt;
 *  StudentPersonal student = new StudentPersonal();
 *  student.setRefId( ADK.makeGUID() );
 *  student.setName( new Name( NameType.LEGAL, &quot;Johnson&quot;, &quot;Tim&quot; ) );
 *  PhoneNumberList pnl = new PhoneNumberList();
 *  pnl.add( new PhoneNumber( PhoneNumberType.ANSWERING_SERVICE, &quot;555-555-2224&quot; ) );
 *  pnl.add( new PhoneNumber( PhoneNumberType.PRIMARY, &quot;555-555-6521&quot; ) );
 *  pnl.add( new PhoneNumber( PhoneNumberType.FAX, &quot;555-555-8745&quot; ) );
 *  pnl.add( new PhoneNumber( PhoneNumberType.MEDIA_CONFERENCE, &quot;555-555-3215&quot; ) );
 *  student.setPhoneNumberList( pnl );
 *  SIFXPathContext context = SIFXPathContext.newSIFContext( student );
 *  // Print out all elements that have a text value
 *  Iterator textNodes = context.iterate(&quot;//&#42;/text()&quot;);&quot;
 *  &quot;System.out.println( &quot;Text Values....&quot; );
 *  while( textNodes.hasNext() ) {
 *  	Element value = (Element)textNodes.next();
 *  	System.out.println( &quot;Tag: &quot; + value.getElementDef().tag( SIFVersion.SIF20) + &quot; = &quot; + value.getTextValue() );
 *  }
 * </pre>
 * 
 * </blockquote>
 * 
 * <h3>Example 4: Using XPath Functions</h3>
 * 
 * Most standard XPath functions can be used to return and find values. Examples
 * of useful functions are the 'concat' and 'substring' functions, which can be
 * used both as arguments to predicates or to format the resulting value. Here
 * are some examples of advanced XPath
 * 
 * <pre>
 * &lt;blockquote&gt;
 *  ADK.initialize();
 *  StudentPersonal student = new StudentPersonal();
 *  student.setRefId( ADK.makeGUID() );
 *  student.setName( new Name( NameType.LEGAL, &quot;Johnson&quot;, &quot;Tim&quot; ) );
 *  PhoneNumberList pnl = new PhoneNumberList();
 *  pnl.add( new PhoneNumber( PhoneNumberType.ANSWERING_SERVICE, &quot;555-555-2224&quot; ) );
 *  pnl.add( new PhoneNumber( PhoneNumberType.PRIMARY, &quot;555-555-6521&quot; ) );
 *  pnl.add( new PhoneNumber( PhoneNumberType.FAX, &quot;555-555-8745&quot; ) );
 *  pnl.add( new PhoneNumber( PhoneNumberType.MEDIA_CONFERENCE, &quot;555-555-3215&quot; ) );
 *  student.setPhoneNumberList( pnl );
 * 
 *  SIFXPathContext context = SIFXPathContext.newSIFContext( student );
 *  // Print out all attributes in this object
 *  Iterator allAttributes = context.iterate(&quot;//&#64;&#42;&quot;);
 *  System.out.println( &quot;All Attributes....&quot; );
 *  while( allAttributes.hasNext() ) {
 *  	SimpleField nextAttr =(SimpleField) allAttributes.next();
 *  	System.out.println(  &quot;Attribute:  &#64;&quot; + nextAttr.getElementDef().tag( SIFVersion.SIF20) + &quot; = &quot; + nextAttr.getTextValue() );
 *  }
 * 
 *  // Print out all Elements in this object
 *  Iterator allNodes = context.iterate(&quot;//&#42;&quot;);
 *  System.out.println( &quot;All Elements....&quot; );
 *  while( allNodes.hasNext() ) {
 *  	Element nextNode =(Element) allNodes.next();
 *  	System.out.println(  &quot;Node: &quot; + nextNode.getElementDef().tag( SIFVersion.SIF20) + &quot; = &quot; + nextNode.getTextValue() );
 *  }
 * 
 *   //Use the concat function
 *  String concat = (String)context.getValue( &quot;concat(//LastName, ', ',//FirstName,  ' - ', //PhoneNumber[@Type='0096']/Number)&quot; );
 *  System.out.println(&quot;Result of Concat operation: &quot; + concat );
 * 
 *   // use the substring fuction
 *  String substring = (String)context.getValue( &quot;substring(//PhoneNumber[@Type='0096']/Number, 5)&quot; );
 *  System.out.println( &quot;PhoneNumber Substring = &quot; + substring );
 * </pre>
 * 
 * </blockquote>
 * 
 * <h2>Built In Function reference</h2>
 * 
 * SIFXPath has a number of built-in functions that can be used. In addition,
 * additional functions can be defined.
 * <p>
 * 
 * Here is a list of some of the built-in functions
 * 
 * <table border="1" cellpadding="2" cellspacing="3">
 * <tr>
 * <td><center><b>Function</b></center></td>
 * <td><center><b>Description</b></center></td>
 * </tr>
 * <tr>
 * <td valign="top">fn:substring(string,start,len)<br>
 * fn:substring(string,start)</td>
 * <td>Returns the substring from the start position to the specified length.
 * <p>
 * Index of the first character is 1. If length is omitted it returns the
 * substring from the start position to the end
 * <p>
 * Example: substring('SIFWorks',1,4)<br>
 * Result: 'SIFW'
 * <p>
 * Example: substring('SIFWorks',2)<br>
 * Result: 'FWorks' </td>
 * </tr>
 * <tr>
 * <td valign="top">concat(string,string,...)</td>
 * <td>Returns the concatenation of the strings
 * <p>
 * Example: concat('Hello ',' - ','World')<br>
 * Result: 'Hello - World' </td>
 * </tr>
 * <tr>
 * <td valign="top">starts-with(string1,string2)</td>
 * <td>Returns true if string1 starts with string2, otherwise it returns false
 * <p>
 * Example: starts-with('XML','X')<br>
 * Result: true </td>
 * </tr>
 * <tr>
 * <td valign="top">substring-before(string1,string2)</td>
 * <td>Returns the start of string1 before string2 occurs in it
 * <p>
 * Example: substring-before('12/10','/')<br>
 * Result: '12' </td>
 * </tr>
 * <tr>
 * <td valign="top">substring-after(string1,string2)</td>
 * <td>Returns the remainder of string1 after string2 occurs in it
 * <p>
 * Example: substring-after('12/10','/')<br>
 * Result: '10' </td>
 * </tr>
 * <tr>
 * <td valign="top">translate(string1,string2,string3)</td>
 * <td>Converts string1 by replacing the characters in string2 with the
 * characters in string3
 * <p>
 * Example: translate('12:30','30','45')<br>
 * Result: '12:45'
 * <p>
 * 
 * Example: translate('12:30','03','54')<br>
 * Result: '12:45'
 * <p>
 * 
 * Example: translate('12:30','0123','abcd')<br>
 * Result: 'bc:da' </td>
 * </tr>
 * <tr>
 * <td valign="top">normalize-space(string)<br>
 * normalize-space()</td>
 * <td>Removes leading and trailing spaces from the specified string, and
 * replaces all internal sequences of white space with one and returns the
 * result.<br>
 * If there is no string argument it does the same on the current node
 * <p>
 * 
 * Example: normalize-space(' The &nbsp;&nbsp;&nbsp;SIFWorks&nbsp;&nbsp;ADK ')<br>
 * Result: 'The SIFWorks ADK' </td>
 * </tr>
 * <tr>
 * <td valign="top">contains(string1,string2)</td>
 * <td>Returns true if string1 contains string2, otherwise it returns false
 * <p>
 * Example: contains('XML','XM')<br>
 * Result: true </td>
 * </tr>
 * </table>
 * 
 * <h3>Using functions from the ADKFunctions class</h3>
 * In addition, the ADK adds a number of custom functions to the ones defined by
 * JXPath. These functions are available in the "adk" namespace and be used by
 * prefixing the function name with "adk:". The ADK functions consist of all of
 * the static members of the
 * {@link openadk.library.tools.xpath.ADKFunctions} class.
 * <p>
 * 
 * Here is an example of using the <code>toUpperCase()</code> function:
 * 
 * <pre>
 * &lt;blockquote&gt;
 *  ADK.initialize();
 *  StudentPersonal student = new StudentPersonal();
 *  student.setRefId( ADK.makeGUID() );
 *  EmailList el = new EmailList();
 *  el.add( new Email( EmailType.PRIMARY, &quot;tjohnson@schools.k12.state.country&quot; ) );
 *  el.add( new Email (EmailType.ALT1, &quot;RoadRunner@aol.com&quot; ) );
 *  student.setEmailList( el );
 *  SIFXPathContext context = SIFXPathContext.newSIFContext( student );
 *  String value = (String)context.getValue( &quot;adk:toUpperCase(//Email[@Type='Primary'])&quot; );
 *  System.out.println( &quot;Email: &quot; + value );
 * </pre>
 * 
 * </blockquote>
 * 
 * <h3>Defining and using your own custom functions</h3>
 * 
 * You can define your own functions using JXPath function objects. To use them
 * with SIFXPathContext, call the {@link #registerCustomFunctions(Functions)}
 * method.
 * 
 * @author Andrew Elmhorst
 * @version ADK 2.0
 * 
 */
public class SIFXPathContext extends JXPathContextReferenceImpl {

	private static FunctionLibrary sFunctions;

	static {
		initialize();
	}
	
	private static AbstractFactory abstractFactory;


	/**
	 * This method is used to statically initialize the JXPath subsystem
	 * In order to make the ADK more modular, this needs to change to use
	 * a more service-based initialization approach. JXpath supports
	 */
	public static void initialize() {
		if( sFunctions != null ){
			return;
		}
		
		JXPathContextReferenceImpl.addNodePointerFactory(new ElementPointerFactory());
		System.setProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY,	SIFXPathContextFactory.class.getName());
		sFunctions = new FunctionLibrary();
		sFunctions.addFunctions(new ClassFunctions(ADKFunctions.class, "adk"));
		
		
		abstractFactory = new AbstractFactory() {
			@Override
		    public synchronized boolean createObject(JXPathContext context, Pointer pointer, Object parent, String name, int index) {
		    	
		    	Object value = pointer.getNode();
		    	
		    	if ( value instanceof Node ) {
		    		Node node = (Node)value;
		    		Node child;
		    		if (name.contains(":")) {
		    			String[] parts = name.split("\\:");
		    			if (parts.length == 2) {
		    				String namespaceURI = context.getNamespaceURI(parts[0]);
		    				if (namespaceURI != null) {
		    					child = node.getOwnerDocument().createElementNS(namespaceURI, name);
		    				} else {
		    					child = node.getOwnerDocument().createElement(name);
		    				}
		    			} else {
		    				child = node.getOwnerDocument().createElement(name);
		    			}
		    		} else {
		    			child = node.getOwnerDocument().createElement(name);
		    		}
		    		node.appendChild( child );
		    		
		    		return true;
		    	} else {
		    		return false;
		    	}
			}
		};
		
	}
	

	/**
	 * Creates a new SIFXPathContext instance to use for traversing the
	 * specified SIF Data Object
	 * 
	 * @param sdo
	 *            The SIF Data Object or SIFElement to traverse
	 * @return an instance of SIFXPathContext
	 */
	public static SIFXPathContext newSIFContext(SIFElement sdo) {
		return new SIFXPathContext(null, sdo);
	}

	/**
	 * Creates a new SIFXPathContext instance to use for traversing the
	 * specified SIF Data Object
	 * 
	 * @param sdo
	 *            The SIF Data Object or SIFElement to traverse
	 * @param version 
	 * 			The SIFVersion to use when traversing this object using XPath.
	 *  		NOTE: The SIFDataObject.setSIFVersion(version) is automatically
	 *  			called and set to the target version.           
	 * @return an instance of SIFXPathContext
	 */
	public static SIFXPathContext newSIFContext(SIFElement sdo, SIFVersion version ) {
		sdo.setSIFVersion( version );
		SIFXPathContext context = newSIFContext( sdo );
		return context;
	}
	
	/**
	 * Creates a new SIFXPathContext instance to use for traversing the
	 * specified SIF Data Object
	 * 
	 * @param parent
	 *            the parent context to retrieve settings from
	 * @param sdo
	 *            The SIF Data Object or SIFElement to traverse
	 * @return an instance of SIFXPathContext
	 */
	public static SIFXPathContext newSIFContext(SIFXPathContext parent,
			SIFElement sdo) {
		return new SIFXPathContext(parent, sdo);
	}
	
	/**
	 * Creates a new SIFXPathContext instance to use for traversing the
	 * specified SIF Data Object
	 * 
	 * @param parent
	 *            the parent context to retrieve settings from
	 * @param sdo
	 *            The SIF Data Object or SIFElement to traverse
	 * @param version 
	 * 			The SIFVersion to use when traversing this object using XPath.
	 *  		NOTE: The SIFDataObject.setSIFVersion(version) is automatically
	 *  			called and set to the target version.           
	 * @return an instance of SIFXPathContext
	 */
	public static SIFXPathContext newSIFContext(SIFXPathContext parent,
			SIFElement sdo, SIFVersion version) {
		sdo.setSIFVersion( version );
		return newSIFContext(parent, sdo);
	}

	/**
	 * Creates an instance of ADKXPathContext
	 * 
	 * @param parent
	 *            The parent context to retrieve settings from
	 * @param sdo
	 *            The SIF Data Object to traverse
	 */
	SIFXPathContext(JXPathContext parent, Object sdo) {
		super(parent, sdo);
		this.setLenient(true);
		this.setFunctions(sFunctions);
		this.setFactory(abstractFactory);
		this.registerNamespace("xsi", SIFWriter.XSI_NAMESPACE);
	}

	/**
	 * Creates an instance of SIFXPathContext
	 * 
	 * @param parent
	 *            the parent context to retrieve settings from
	 * @param sdo
	 *            The SIF Data Object to traverse
	 * @param contextPointer
	 *            a pointer to traverse
	 */
	SIFXPathContext(JXPathContext parent, Object sdo, Pointer contextPointer) {
		super(parent, sdo, contextPointer);
		this.setLenient(true);
		this.setFactory(abstractFactory);
		this.registerNamespace("xsi", SIFWriter.XSI_NAMESPACE);
	}
	
	/**
	 * Register custom functions to be used with SIFXPathContext
	 * 
	 * @param functions
	 */
	public static void registerCustomFunctions(Functions functions) {
		sFunctions.addFunctions(functions);
	}

	/**
	 * Removes XPath syntax that was proprietary to the ADK in ADK 1.x versions and converts
	 * the expression to the syntax supported by JXPath
	 * @param xPath The path to evaluate
	 * @return The converted xPath
	 */
	public static String convertLegacyXPath( String xPath )
	{
		// TODO: This is rudimentary and may need to become more robust
		// We also probably need to have a flag that turns this type of parsing
		// off for people using correct syntax because this parsing will get in
		// the way of complex expressions.
		StringBuilder sb = new StringBuilder( xPath );
		boolean inPredicate = false;
		boolean inString = false;
		int parens = 0;
		for( int a = 0; a< sb.length(); a++ ){
			char chr = sb.charAt( a );
			switch( chr ){
			case '[':
				inPredicate = true;
				break;
			case ']':
				inPredicate = false;
				break;
			case '(':
				parens++;;
				break;
			case ')':
				parens--;
				break;
			case '\'':
			case '"':
				inString = !inString;
				break;
			case ',':	// The ADK syntax assumes that a comma seperating predicates means " and "
				if( inPredicate && !inString && parens == 0 ){
					sb.replace( a, a + 1, " and " );
					a+=4;
				}
				break;
			case '$':
				if( sb.charAt( a+1 ) == '(' )
				{
					int closeParen = sb.indexOf( ")", a );
					if( closeParen > -1 ){
						sb.replace( closeParen, closeParen + 1, "" );
						sb.replace( a+1, a+2, "" );
						
						if( inString ){
							// Remove the single quotes around this variable, if present
							if( sb.charAt( a - 1 ) == '\'' ){
								sb.replace( a-1, a, "" );
								inString = false;
							}
							if( sb.charAt( closeParen - 2 ) == '\'' ){
								sb.replace( closeParen - 2, closeParen - 1, "" );
							}
							
						}
					}
				}
				break;
			case '+':		// The +] Syntax is ADK-specific and is used for outbound mappings
				if( inPredicate && !inString ){
					sb.replace( a, a + 1, " and adk:x()" );
					a+=11;
				}
				break;
			}
		}
		
		return sb.toString();
		
	}
	
	/* (non-Javadoc)
	 * @see org.apache.commons.jxpath.JXPathContext#compile(java.lang.String)
	 */
	public static CompiledExpression compile( String legacyXPath )
	{
		String xPath = convertLegacyXPath( legacyXPath );
		return JXPathContext.compile( xPath );
	}
	
	
	
	/**
	 * Allows for getting Elements from a SIF Element using the legacy ADK 1.x
	 * style XPath queries.
	 * 
	 * This method should only be used if the XPath syntax needs to be converted 
	 * from ADK 1.x syntax to true XPath. If the query is already in true XPath
	 * format, call {@link JXPathContext#getValue(java.lang.String)} 
	 * 
	 * @param xPath
	 * @return An Element from this object representing the path, or null
	 */
	public Element getElementOrAttribute( String xPath )
	{
		String jXPath = SIFXPathContext.convertLegacyXPath( xPath );
		return (Element)getValue( jXPath );
	}
	
	
	public void setElementOrAttribute( String xPath, Object value )
	{
		String jXPath = SIFXPathContext.convertLegacyXPath( xPath );
		createPathAndSetValue( jXPath, value );
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.jxpath.ri.JXPathContextReferenceImpl#createPathAndSetValue(java.lang.String,
	 *      org.apache.commons.jxpath.ri.compiler.Expression, java.lang.Object)
	 */
	@Override
	public Pointer createPathAndSetValue(String xpath, Expression expr,
			Object value) {
		
		if( expr.isContextDependent() ){
			NodePointer np = buildADKPathWithPredicates( (Path) expr );
			if( np != null ){
				// Only set the value if the buildADKPathWithPredicates function
				// returns a NodePointer
				np.setValue(value);
			}
			return np;
		}
		else {
			return super.createPathAndSetValue( xpath, expr, value );
		}
	}
	
	@Override
	public Pointer createPath(String xpath, Expression expr) 
	{
/*		if (expr instanceof CoreOperationAnd) {
			
		}
		else 
*/		if( expr.isContextDependent() ){
			NodePointer np = buildADKPathWithPredicates( (Path) expr );
			return np;
		}
		else {
			return super.createPath( xpath, expr );
		}
	};
//
//	/**
//	 * Removes an item from the parent cache
//	 * 
//	 * @param xPath
//	 */
//	private void unCache(String xPath) throws IllegalAccessException {
//		if (sParentCacheField == null) {
//			sParentCacheField = getParentCacheField();
//		}
//		Map map = (Map) sParentCacheField.get(this);
//		map.remove(xPath);
//	}
//
//	/**
//	 * Uses Java reflection to gain access to the private HashMap used by the
//	 * parent class to cache Expressions. The parent class does not offer a way
//	 * to remove an item from the cache. This class needs to remove an
//	 * expression from the cache because it has created a new object hierarchy
//	 * representing the path
//	 * 
//	 * @return The "compiled" field
//	 */
//	private Field getParentCacheField() {
//		for (Field field : JXPathContextReferenceImpl.class.getDeclaredFields()) {
//			if (field.getName().equals("compiled")) {
//				field.setAccessible(true);
//				return field;
//			}
//		}
//		throw new RuntimeException("Unable to gain access to parent cache");
//	}

	/**
	 * Manually builds out a path to support the necessary mapping needs of the
	 * ADK. By default, the JXPath implementation does not allow
	 * context-dependend predicates (e.g. PhoneNumber[@Type='0096'] to be used
	 * in XPaths that create the path. This implementation manually steps
	 * through the XPath and builds it out. It's primary intent is to provide
	 * the behavior that was present in the ADK before JXPath was used for
	 * mapping
	 * 
	 * @param expr
	 *            The Path expression to build out
	 * @param jxpe
	 *            The exception thrown by the base class that was caught. If
	 *            this method cannot build the requested path, it will re-throw
	 *            the exception
	 */
	private NodePointer buildADKPathWithPredicates( Path expr ) {

		// Use the set of expression steps to determine which parts of the
		// path already exist. Note that the order of evaluation used is optimized
		// for first-time creation of elements. In other words, the path chosen was
		// to evalaute the expression steps from the beginning rather than the end
		// because for outbound mappings, that order will generally be the most efficient
		Step[] steps = expr.getSteps();
		int currentStep = 0;
		StringBuilder pathSoFar = new StringBuilder( );
		NodePointer parent = (ADKElementPointer)getContextPointer();
		NodePointer holdParent = parent;
		NodePointer current = null;
		for( ; currentStep < steps.length; currentStep++ ){
			current = findChild( pathSoFar, steps[currentStep] );
			if( current == null ){
				break;
			}
			pathSoFar.append( "/" );
			pathSoFar.append( steps[currentStep].toString() );
			parent = current;
		}
		if( current != null ){
			// We traversed the entire path and came up with a result.
			// That means that the element we are trying to build the 
			// path to already exists. We will not create this path, so
			// return null;
			
			// JEN Changed to allow multiple object creation
//			return null;
			currentStep = 0;
			parent = holdParent;
		}
		
		// We've traversed down to the level where we think we need to
		// add a child. However, there are cases where this is not the proper
		// location. For example, in SIF 1.5r1, the StudentAddressList element is 
		// repeatable and Address is not. It would not be proper to add a new Address
		// element under StudentAddressList. Instead, the algorithm needs to back
		// up the stack until it reaches the next repeatable element for the current
		// version of SIF
		// The following code is primarily in place for the StudentAddressList case, which is
		// why the isContextDependent() logic applies. Currently, there is no known other place
		// where this checking needs to occur.
		if( currentStep > 0 && steps[currentStep].isContextDependent() ){
			int step = currentStep;
			NodePointer stepParent = parent;
			while( step > -1  )  // don't evaluate step 0 at the root of the object because this problem doesn't apply there
			{
				if( parent instanceof SIFElementPointer ){
					SIFElementPointer sifParentPointer = (SIFElementPointer)stepParent;
					NodeTest nt = steps[step].getNodeTest();
					if( nt instanceof NodeNameTest )
					{
						SIFElementPointer.AddChildDirective result = 
							sifParentPointer.getAddChildDirective( ((NodeNameTest)nt).getNodeName() );
						if( result != SIFElementPointer.AddChildDirective.DONT_ADD_NOT_REPEATABLE ){
							break;
						}
						
					}
				} else {
					break;
				}
				step--;
				stepParent = stepParent.getParent();
			}
			if( step > -1 && step != currentStep ){
				currentStep = step;
				parent = stepParent;
			}
			
		}
		
		// At this point, we have a parent element and the index of the current
		// step to evaluate
		InitialContext context = new InitialContext( new RootContext( this, (NodePointer) getContextPointer()));
		for( ; currentStep < steps.length; currentStep++  ){
			NodeTest nt = steps[currentStep].getNodeTest();
			if( nt instanceof NodeNameTest )
			{
				current = parent.createChild( this, ((NodeNameTest) nt ).getNodeName(), 0);
				if( current == null ){
					throw new JXPathException( "Cannot evaluate expression step: " + steps[currentStep].toString() );
				}
				for (Expression predicate : steps[currentStep].getPredicates()) {
					createPredicateValues(current, predicate, context);
				}
			} else if( nt instanceof NodeTypeTest ){
				NodeTypeTest ntt = (NodeTypeTest)nt;
				if( ntt.getNodeType() == Compiler.NODE_TYPE_NODE ||		// <AlertMessage>.</AlertMessage>
						ntt.getNodeType() == Compiler.NODE_TYPE_TEXT )  // <AlertMessage>text()</AlertMessage>
				{
					return parent;
				}
			} else {
				throw new JXPathException( "Cannot evaluate expression step: " + steps[currentStep].toString() );
			}
			
			parent = current;
		}
		// At the end, the 'parent' variable will contain the last element created by this function
		return parent;
	}

	private void createPredicateValues(NodePointer current, Expression predicate, EvalContext evalContext) {
		
		if( predicate instanceof CoreOperationEqual ) {
			Expression[] components = ((CoreOperationEqual) predicate).getArguments();
			LocationPath lp = (LocationPath) components[0];
			NodeNameTest attrName = (NodeNameTest) lp.getSteps()[0].getNodeTest();
			NodePointer attr = current.createAttribute(this, attrName.getNodeName());
			Object value = components[1].computeValue(evalContext);
			if( value instanceof EvalContext ){
				value = ((EvalContext)value).getValue();
			}
			attr.setValue( value );
			return;
		}
			
		// This might be the 'adk:x()' function
		if( predicate instanceof CoreOperationAnd ) 
		{
			for( Expression expr : ((CoreOperationAnd)predicate).getArguments())
			{
				if( expr instanceof ExtensionFunction && ((ExtensionFunction)expr).getFunctionName().getName().equals( "x"  )){
						// This is the special marker function that tells the ADK to always
						// create the parent repeatable element. Don't evaluate it.
						continue;
				}
				else {
					createPredicateValues( current, expr, evalContext );
				}
			}
			return;
		}
		
		// Unrecognized predicate
		throw new JXPathException( "Cannot evaluate expression predicate: " + predicate.toString() );
	}
	
	
	/**
	 * Evaluates the current step in the path. If the path represented by the step does not
	 * exist, NULL is returned. Otherwise, the node found is returned unless the special adk:X() 
	 * marker function is contained in the predicate expression, which signals that the specified
	 * repeatable element should always be created
	 * @param parent
	 * @param currentStep
	 * @return The NodePointer found by the path, or null
	 */
	private NodePointer findChild( StringBuilder parentPath, Step currentStep ){
		
		String currentStepxPath = currentStep.toString();
		if( currentStep.isContextDependent() ){
			// If the special 'adk:x()' function is present, that means to always 
			// create the element, therefore, return null as if it were not found
			if( currentStepxPath.indexOf( "adk:x" ) > -1  ){
				return null;
			}
			
		}
		Pointer retValue = getPointer( parentPath + "/" + currentStepxPath );
		if( retValue instanceof NullPropertyPointer || retValue instanceof NullPointer ){
			return null;
		}
		return (NodePointer)retValue;
		
	}
	
	
	
	
}
