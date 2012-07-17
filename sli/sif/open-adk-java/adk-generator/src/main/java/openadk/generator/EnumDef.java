//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

import java.util.Hashtable;

/**
 *  An enumeration of valid values for a field.
 *
 */
public class EnumDef extends AbstractDef
{
	protected String fPackage;
	protected Hashtable<String, ValueDef> fValues = new Hashtable<String, ValueDef>();

    public EnumDef( String name, String pkg )
	{
		super();

		fName=name;
		fPackage=pkg;
    }

	public void defineValue( String tag, String value, String desc ) throws ParseException{
		String t = tag;
		if( tag == null ){
			t = value;
		}
		if( fValues.containsKey( tag ) ){
			throw new ParseException("Enum "  + fName + " already contains an entry with the name " + tag );
		} else if ( fValues.contains( value ) ){
			throw new ParseException("Enum "  + fName + " already contains an entry with the value " + value );
		}
		fValues.put( t, new ValueDef(t,value,desc) );
	}
	public Hashtable<String, ValueDef> getValues() {
		return fValues;
	}
}
