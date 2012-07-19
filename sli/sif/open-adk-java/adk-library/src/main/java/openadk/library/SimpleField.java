//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 *  A simple field value. Unlike complex elements, which are
 *  stored as child objects of their parent, simple fields (i.e. attributes or
 *  elements that have no children) are wrapped in a SimpleField instance and
 *  stored in the field table of their parent object.
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public class SimpleField<T> extends Element
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -384984737821123229L;
	/**
	 * The SIF datatype wrapper
	 */
	private SIFSimpleType<T> fValue;
	
	/**
	 * @param parent The SIFElement that is the parent of this field
	 * @param def The metadata definition of this field
	 * @param value A typed subclass of SIFSimpleType
	 */
    public SimpleField( SIFElement parent, ElementDef def, SIFSimpleType<T> value )
	{
		super(def);
		
        if( value == null )
        {
            throw new IllegalArgumentException( 
            		"Cannot construct an instance of " + 
            		this.getClass().getCanonicalName() + 
            		" with a null value. " +
            		"Create an appropriate SifSimpleType subclass to wrap the null value." );
        }
		
		fParent = parent;
		fValue = value;
    }
    
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.SimpleField#getTextValue(com.edustructures.sifworks.SIFFormatter)
	 */
	public String getTextValue( SIFFormatter formatter ) {
		return fValue.toString( formatter );
	}
	
	/**
	 * @param value
	 * @param formatter
	 * @throws ADKParsingException
	 */
	public void setTextValue( String value, SIFFormatter formatter ) throws ADKParsingException
	{
		fValue = fValue.getTypeConverter().parse( formatter, value );
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Element#getTextValue()
	 */
	@Override
	public String getTextValue() {
		return fValue.toString( ADK.getTextFormatter() );
	}

	/**
	 * Sets the datatype of this field as a string value. The string is parsed
	 * using the default SIF formatter, which is the SIF 1.x formatter by default
	 * @see openadk.library.Element#setTextValue(java.lang.String)
	 * @throws NumberFormatException if the value being set cannot be parsed into
	 * the datatype being stored for this field
	 */
	@Override
	public void setTextValue(String value)  {
		try	{
			setTextValue( value, ADK.getTextFormatter() );
		}
		catch( ADKParsingException adkpe ){
			// TODO: Determine if this is the right way to handle it...
			throw new NumberFormatException( adkpe.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked") // Not sure how to work around this warning
	@Override
	public void setSIFValue( SIFSimpleType value ){
		fValue = value;
	}
	
	/**
	 * Returns the SIFSimpleType value of this field
	 * @return the SIFSimpleType value of this field
	 * @see openadk.library.Element#getSIFValue()
	 */
	public SIFSimpleType<T> getSIFValue()
	{
		return fValue;
	}
	
	/**
	 * Returns the native datatype value of this field
	 * @return the native datatype value of this field
	 */
	public T getValue()
	{
		return fValue.getValue();
	}
	
	/**
	 * Checks the underlying data type and flags to determine if XML encoding
	 * should be turned off for this field
	 * @see openadk.library.Element#isDoNotEncode()
	 */
	@Override 
	public boolean isDoNotEncode()
	{
		boolean isDoNotEncode = false;
		if( fValue != null ){
			isDoNotEncode = fValue.isDoNotEncode();
		}
		return isDoNotEncode | super.isDoNotEncode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() 
		throws CloneNotSupportedException
	{
				try{
			Constructor constructor = this.getClass().getConstructor( 
					new Class[] {
							SIFElement.class, ElementDef.class, SIFSimpleType.class } );

			// Do not clone the parent reference
			SimpleField<T> fieldCopy = (SimpleField<T>)constructor.newInstance( null, fElementDef, fValue );
			return fieldCopy;
		}
		catch( RuntimeException re )
		{
			throw re;
		}
		catch( Exception iae ){
			throw new CloneNotSupportedException( iae.getMessage() );
		}

	}
	
	private void writeObject( java.io.ObjectOutputStream out )
		throws IOException
	{
		out.defaultWriteObject();
		//
		// Write the path to the ElementDef
		//
		String path = null;
		if( fElementDef.isField() )
		{
			// This SimpleField represents a field on an object
			path = fElementDef.getSDOPath();
		} else {
			// This SimpleField must represent the text value of an object
			path = "TEXT_VALUE";
		}
			
		out.writeUTF( path );
	}
	
	private void readObject( java.io.ObjectInputStream in )
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		//
		// Read the path to the ElementDef
		//
		String path = in.readUTF();
		ElementDef foundElementDef = null;
		if( path.equals( "TEXT_VALUE" ) )
		{
			if( fParent != null ){
				
			    // TODO:  MLW - I consider this a hack.  On deserialization, the no-arguments constructor is 
			    // not called.  Also, SIFElements that were serialized without a parent but normally do have a parent
			    // are not returned by the lookupElementDef() call above.  To fix this, I instantiate
			    // a new object of this type, and then see what the elementdef of that object is.
			    try {
	                SIFElement parentInstance = (SIFElement) fParent.getClass().newInstance();
	                foundElementDef = parentInstance.getElementDef();
	            } catch (InstantiationException ex) {
	                throw new RuntimeException("Deserialization failed: " + ex.getMessage(), ex);
	            } catch (IllegalAccessException ex) {
	                throw new RuntimeException("Deserialization failed" + ex.getMessage(), ex);
	            }
				
			}
		} else if ( path.length()  > 0 )
		{
			foundElementDef = ADK.DTD().lookupElementDef(path);
		}
		if( foundElementDef == null  )
		{
			String errorInfo = null;
			if( fParent == null ){
				errorInfo = getClass().getCanonicalName() + "=" + this.fValue.getValue();
			} else  {
				errorInfo = fParent.getClass().getCanonicalName() + "/" + getClass().getCanonicalName();
			}
			throw new RuntimeException( "Unable to deserialize field of object: " + errorInfo); 
		}
		fElementDef = foundElementDef;
	}
   
}
