//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

import java.util.*;

/**
 *  Encapsulates a DataObject (<object>) or Infrastructure Message (<infra>) definition
 *
 *
 */
public class ObjectDef extends AbstractDef
{
	public static final int
		FLAG_TOPICOBJECT = 0x00100000,
		FLAG_EMPTYOBJECT = 0x00200000,
		FLAG_INFRAOBJECT = 0x00400000,
		FLAG_OBJECTCOLLAPSED = 0x00800000;

	protected Hashtable<String, FieldDef> fFields = new Hashtable<String,FieldDef>();
	protected int fFieldSeq = 1;
	protected String fPackage;
	protected String fSuperclass;
	protected String fExtrasFile;
	protected String fRenderAs;
	protected ADKElementType fElementType;
	

	/**
	 *  The shared flag indicates this object serves as the superclass for
	 *  one or more sub-classes. For example, "CountryOfResidency" uses "Country"
	 *  as its superclass, so the "Country" object is marked as shared in the
	 *  definition file.
	 */
	protected boolean fShared;

	/**
	 * If this object accepts a text value, holds the datatype of that value.
	 */
	private FieldType fValueType;

	/**
	 *  Constructor
	 *  @param id Sequence number of this object
	 *  @param name Element name (e.g. "StudentPersonal", "OtherId", etc.)
	 *  @param localPackage Package name (e.g. "common", "student", "food", etc.)
	 *  @param version Version of SIF this object is being defined for
	 */
    public ObjectDef( int id, String name, String localPackage, SIFVersion version )
	{
		fName = name;
		fPackage = localPackage;
    }

	/**
	 *  Gets the local package name where this object's class should be generated.
	 *  The local package name excludes the "com.edustructures.sifworks." prefix
	 */
	public String getLocalPackage()
	{
		return fPackage;
	}

	/**
	 *  Gets the name used to represent this object in the DTD class. A static
	 *  String is defined with this name, having a value equal to the string
	 *  returned by getName.
	 */
	public String getDTDSymbol()
	{
		return getName().toUpperCase();
	}

	public String getPackageQualifiedDTDSymbol()
	{
		return getPackageDTDName() + "." + getDTDSymbol();
	}
	
	public String getPackageDTDName()
	{
		return fPackage.substring( 0, 1 ).toUpperCase() + fPackage.substring( 1 ) + "DTD";
	}
	
	
	/**
	 * Returns the ADKElementType of this object, which can be used to determine what the
	 * base class of this SDO object will be. The ADK defines several subclasses of SIFElement
	 * that can be used as the base class for specialized elements within SIF, such as SIFActionList
	 * @return
	 */
	public ADKElementType getElementType( DB db, CodeGenerator generator )
	{
		if( this.fName.equals( "MedicalAlertMsg" ) ){
			System.out.println("Time to debug" );
		}
		if( fElementType == null ){
			if( this.isTopic() ){
				fElementType = ADKElementType.SIFDATAOBJECT;
			}
			else if( fSuperclass != null && !fSuperclass.equals( "SIFElement" ) )
			{
				if( fSuperclass.startsWith( "SIFActionList" ) ){
					fElementType = ADKElementType.SIFACTIONLIST;
				} 
				else
				{
					fElementType = ADKElementType.SIFEELEMENT;
				}
			}
			else 
			{
				FieldDef childType = this.getRepeatableChildDef();
				
				if( childType != null  ){
//					ObjectDef childObject = db.getObject( childType.getFieldType().getClassType() );
//					if( childObject == null ){
//						throw new RuntimeException("Unable to find object definition {" + 
//								childType.getFieldType().getClassType() + "} for member: " +
//								this.getName() + "." + childType.getName()	);
//					}
//					FieldDef[] childKeys = childObject.getKey();
//					//
//					// TODO: The ADK has to glean the difference between an ActionList and
//					//       a normal list from the metadata, so we need to add this information.
//					//
//					if( childKeys != null && childKeys.length > 0 ){
//						
//						 fElementType = ADKElementType.SIFACTIONLIST;
//					} else {
						fElementType = ADKElementType.SIFLIST;
//					}
				} else {
					FieldDef[] childKeys = this.getKey( generator );
					if( childKeys != null && childKeys.length == 1 ){
						fElementType = ADKElementType.SIFKEYEDELEMENT;
					} else {
						fElementType = ADKElementType.SIFEELEMENT;
					}
				}
			}
		}
		return fElementType;
	}
	
	
	/**
	 *  Gets the element tag name
	 */
	@Override
	public String getName()
	{
		return fName;
	}

	public String getExtrasFile()
	{
		return fExtrasFile;
	}
	public void setExtrasFile( String fn )
	{
		fExtrasFile = fn;
	}

	public String getSuperclass()
	{
		return fSuperclass;
	}
	
	public String getClassDefinitionString()
	{
		if( fFields.size() == 1 ){
			FieldDef[] fields = new FieldDef[1];
			fFields.values().toArray( fields );
			if( fields[0].isRepeatable() ){
				return fields[0].getFieldType().getClassType();
			}
		} 
		return null;
	}
	
	
	
	/**
	 * If this object is a repeatable elements container, this method returns the 
	 * FieldDef of the repeatable child
	 * @return
	 */
	public FieldDef getRepeatableChildDef()
	{
		if( fFields.size() == 1 ){
			FieldDef[] fields = new FieldDef[1];
			fFields.values().toArray( fields );
			if( fields[0].isRepeatable() ){
				return fields[0];
			}
		} 
		return null;
	}

	public void setSuperclass( String cls )
	{
		fSuperclass = cls;
	}

	public void setRenderAs( String tag ) {
		fRenderAs = tag;
	}

	public String getRenderAs() {
		return fRenderAs;
	}

	public String getTag() {
		if( fRenderAs == null ){
			return fName;
		}
		return fRenderAs;
	}

	public boolean isEmpty()
	{
		return( fFlags & FLAG_EMPTYOBJECT ) != 0;
	}

	/**
	 *  Is this object a top-level SIF object such as StudentPersonal?
	 */
	public boolean isTopic()
	{
		return( fFlags & FLAG_TOPICOBJECT ) != 0;
	}

	/**
	 *  Is this an <infra> object describing a SIF Infrastructure message?
	 */
	public boolean isInfra()
	{
		return( fFlags & FLAG_INFRAOBJECT ) != 0;
	}

	/**
	 *  Indicates this is an <infra> object describing a SIF Infrastructure message
	 */
	public void setInfra()
	{
		fFlags |= FLAG_INFRAOBJECT;
	}

	/**
	 *  Does this object serve as the superclass for one or more subclasses?
	 */
	public boolean isShared() {
		return fShared;
	}
	public void setShared( boolean shared ) {
		fShared = shared;
	}

	/**
	 *  Sets the topic flag
	 */
	public void setTopic( boolean topic )
	{
		if( topic )
			fFlags |= FLAG_TOPICOBJECT; else
			fFlags &= ~FLAG_TOPICOBJECT;
	}

	public FieldDef defineAttr( String name, String classType )
	throws ParseException
	{
		return defineAttrOrElement(name,classType,FieldDef.FLAG_ATTRIBUTE);
	}

	public FieldDef defineElement( String name, String classType )
	throws ParseException
	{
		return defineAttrOrElement(name,classType,FieldDef.FLAG_ELEMENT);
	}

	protected FieldDef defineAttrOrElement( String name, String classType, int nodeType )
	throws ParseException
	{
		FieldDef d = fFields.get(name);
		if( d == null ) {
			d = new FieldDef(this,name,classType, fFieldSeq++, nodeType);
			fFields.put(name,d);
		}
		return d;
	}

	public FieldDef getField( String name )
	{
		return fFields.get(name);
	}

	public FieldDef[] getAllFields()
	{
		List<FieldDef> v = new Vector<FieldDef>();
		for( Enumeration<FieldDef> e = fFields.elements(); e.hasMoreElements(); ) {
			v.add( e.nextElement() );
		}
	
		Collections.sort( v );
		//  Sort by sequence # first
		FieldDef[] returnValue = new FieldDef[ v.size() ];
		v.toArray( returnValue );
		return returnValue;
	}
	
	public FieldDef[] getDTDFields()
	{
		List<FieldDef> v = new Vector<FieldDef>();
		for( Enumeration<FieldDef> e = fFields.elements(); e.hasMoreElements(); ) {
			v.add( e.nextElement() );
		}
		
		Collections.sort( v );
		//  Sort by sequence # first
		FieldDef[] returnValue = new FieldDef[ v.size() ];
		v.toArray( returnValue );
		return returnValue;
	}
	
	public FieldDef[] getAttributes()
	{
		return getFields( FieldDef.FLAG_ATTRIBUTE, null );
	}
	public FieldDef[] getElements()
	{
		return getFields( FieldDef.FLAG_ELEMENT, null );
	}

	protected FieldDef[] getFields( int flags, CodeGenerator generator )
	{
		Vector<FieldDef> v = new Vector<FieldDef>();
		for( Enumeration e = fFields.elements(); e.hasMoreElements(); ) {
			FieldDef f = (FieldDef)e.nextElement();
			if( ( f.getFlags() & flags ) != 0 )
				v.addElement(f);
		}

		if( ( flags & FLAG_MANDATORY ) != 0){
			FieldDef valueDef = getValueDef( generator );
			if( valueDef != null ){
				v.add( valueDef );
			}
		}
		
		
		Collections.sort( v );

		FieldDef[] arr = new FieldDef[v.size()];
		v.copyInto(arr);

		Arrays.sort(arr);

		return arr;
	}

	/**
	 *  Get all attributes and elements that are marked mandatory (M) or
	 *  required (R)
	 */
	public FieldDef[] getMandatoryFields( CodeGenerator generator )
	{
		return getFields( FLAG_MANDATORY, generator );
	}

	/**
	 *  For complex fields, returns the names of the fields that serve as the
	 *  object's key. By default this method returns all attributes marked with
	 *  an "R" flag (and if no attributes exist or none are marked with an "R"
	 *  flag, returns all elements marked with an "R" flag).
	 */
	public FieldDef[] getKey( CodeGenerator generator )
	{
		FieldDef[] attrs = getAttributes();
		// If this object is a Topic and it has a RefId, return the RefId
		if( isTopic() ){
			for( int i = 0; i < attrs.length; i++ ) {
				if( attrs[i].getName().equals("RefId") )
				{
					int f = attrs[i].getFlags();
					if( ( f & FieldDef.FLAG_NOT_A_KEY ) == 0 ){
						return new FieldDef[]{ attrs[i] };
					}
				}
			}
		}
		
		Vector<FieldDef> v = new Vector<FieldDef>();
		int loop = 0;
		while( attrs != null )
		{
			for( int i = 0; i < attrs.length; i++ ) {
				int f = attrs[i].getFlags();
				if(
					( ( f & AbstractDef.FLAG_MANDATORY ) != 0 ) &&
					( ( f & FieldDef.FLAG_NOT_A_KEY ) == 0 )
				  )
					v.addElement(attrs[i]);
			}

			//  There are a few inconsistencies in the SIF schema such that some
			//  objects' keys are described by elements, not attributes. So, if we
			//  get here and have no keys, loop again processing element fields
			//  instead of attribute fields.
			//
			if( v.size() == 0 && loop == 0 )
				attrs = getElements();
			else
				attrs = null;

			loop++;
		}
		
		// If this is a simple text-only element, return the value def for the element
		if( v.size() == 0 ){
			FieldDef value = this.getValueDef( generator );
			if( value != null )
			{
				v.add( value );
			}
		}

		FieldDef[] arr = new FieldDef[v.size()];
		v.copyInto(arr);
		Arrays.sort(arr);
		return arr;
	}
	
	/**
	 * If this this object accepts an element value <element>123.123</element>,
	 * the datatype of that value is set in the metadata using a "datatype" element
	 * @param type
	 */
	public void setDataType( String dataType ){
		fValueType = FieldType.getFieldType( dataType );
	}
	
	public void setEnumType( String enumType ) throws ParseException{
		fValueType = FieldType.toEnumType( FieldType.getFieldType( "String" ), enumType ); 
	}

	/**
	 *  Does this element have a text value?
	 *  @return true if this element has no elements or attributes, or it has
	 *      no elements but at least one attribute and the FLAG_EMPTYOBJECT flag
	 *      
	 *      is not set
	 */
	public  FieldType getValueType()
	{
		if( fValueType == null ){
			if( ( fFlags & FLAG_EMPTYOBJECT ) == 0 ){
				if( fFields == null || fFields.size() == 0 || getElements().length == 0 ){
					fValueType = FieldType.getFieldType( "String" );
				}
			}
		}
		return fValueType;
	}
	
	/**
	 * 
	 * @return
	 */
	public FieldDef getValueDef( CodeGenerator generator ){
		FieldDef returnValue = null;
		FieldType valueType = getValueType();
		if( valueType != null ){
			try{
				returnValue = new FieldDef( this, "Value", valueType, 999, FieldDef.FLAG_TEXT_VALUE | AbstractDef.FLAG_MANDATORY );
			} catch (ParseException parseEx ){
				System.out.println( parseEx );
				throw new RuntimeException( parseEx.toString(), parseEx );
			}
			returnValue.setDesc( "Gets or sets the content value of the &lt;" + this.fName + "&gt; element" );
			returnValue.setEarliestVersion( this.getEarliestVersion() );
			returnValue.setLatestVersion( this.getLatestVersion() );
			returnValue.setElementDefConst( this.getPackageQualifiedDTDSymbol() );
		}
		return returnValue;
	}

	public void addAlias(SIFVersion version, String renderAs) {
		if( fAliases == null ){
			fAliases = new HashMap<String, List<SIFVersion>>();
		}
		List<SIFVersion> versions = fAliases.get( renderAs );
		if( versions == null ){
			versions = new ArrayList<SIFVersion>();
		}
		versions.add( version );
		fAliases.put( renderAs, versions );
	}
	
	/**
	 * Returns a map of aliases this Object has in other versions, along
	 * with a list of SIFVersions it applies to
	 * @return
	 */
	public Map<String, List<SIFVersion>> getAliases() {
		return fAliases;
	}
	
	private Map<String, List<SIFVersion>> fAliases = null;

	public String getPackage() {
		return fPackage;
	}

}
