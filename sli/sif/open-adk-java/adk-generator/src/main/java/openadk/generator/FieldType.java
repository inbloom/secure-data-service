//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;


public class FieldType {

	private static FieldType FIELD_BOOLEAN = new FieldType( ADKDataType.BOOLEAN );
	private static FieldType FIELD_STRING = new FieldType( ADKDataType.STRING );
	private static FieldType FIELD_DATE = new FieldType( ADKDataType.DATE );
	private static FieldType FIELD_TIME = new FieldType( ADKDataType.TIME );
	private static FieldType FIELD_DATETIME = new FieldType( ADKDataType.DATETIME );
	private static FieldType FIELD_DURATION = new FieldType( ADKDataType.DURATION );
	private static FieldType FIELD_FLOAT = new FieldType( ADKDataType.FLOAT );
	private static FieldType FIELD_INT = new FieldType( ADKDataType.INT );
	private static FieldType FIELD_UINT = new FieldType( ADKDataType.UINT );
	private static FieldType FIELD_LONG = new FieldType( ADKDataType.LONG );
	private static FieldType FIELD_ULONG = new FieldType( ADKDataType.ULONG );
	private static FieldType FIELD_DECIMAL = new FieldType( ADKDataType.DECIMAL );
	private static FieldType FIELD_SIFVERSION = new FieldType( ADKDataType.SIFVERSION );

	private ADKDataType fDataType;
	private String fClassType;
	private FieldType( ADKDataType valueType ){
		fDataType = valueType;
	}

	public boolean isComplex(){
		return fDataType == ADKDataType.COMPLEX;
	}

	public boolean isEnum(){
		return fDataType == ADKDataType.ENUM;
	}

	public boolean isSimpleType(){
		return !( isComplex() || isEnum() );
	}

	public String getClassType()
	{
		return fClassType;
	}

	public String getEnum()
	{
		if( fDataType == ADKDataType.ENUM ){
			return fClassType;
		}
		return null;
	}


	public ADKDataType getDataType()
	{
		return fDataType;
	}

	public static FieldType getFieldType( String classType )
	{
		if( 	classType == null  ||
				classType.length() == 0 ||
				classType.equalsIgnoreCase( "String" ) ||
				classType.equalsIgnoreCase( "Token" ) ||
				classType.equalsIgnoreCase( "NormalizedString" ) ||
				classType.equalsIgnoreCase( "NCName" ) ||
				classType.equalsIgnoreCase( "IdRefType" ) ||
				classType.equalsIgnoreCase( "AnyUri" ) ){
			return FIELD_STRING;
		} else if( classType.equalsIgnoreCase( "DateTime" ) ){
			return FIELD_DATETIME;
		} else if( classType.equalsIgnoreCase( "Int" ) ||
					classType.equalsIgnoreCase( "PositiveInteger" ) ||
					classType.equalsIgnoreCase( "GYear" ) ||
					classType.equalsIgnoreCase( "GMonth" ) ||
					classType.equalsIgnoreCase( "GDay") ){
			return FIELD_INT;
		} else if( classType.equalsIgnoreCase( "UnsignedInt" ) ||
				classType.equalsIgnoreCase( "NonNegativeInteger" ) ||
				classType.equalsIgnoreCase( "UInt" ) ){
			return FIELD_UINT;
		} else if( classType.equalsIgnoreCase( "Long" ) ){
			return FIELD_LONG;
		} else if( classType.equalsIgnoreCase( "ULong" ) ){
			return FIELD_ULONG;
		}else if( classType.equalsIgnoreCase( "Decimal" ) ){
			return FIELD_DECIMAL;
		}else if( classType.equalsIgnoreCase( "Float" ) ){
			return FIELD_FLOAT;
		} else if( classType.equalsIgnoreCase( "SIFDate" ) ||
					classType.equalsIgnoreCase( "Date" ) ){
			return FIELD_DATE;
		} else if( classType.equalsIgnoreCase( "Boolean" ) ||
					classType.equalsIgnoreCase( "YesNo" ) ){
			return FIELD_BOOLEAN;
		} else if( classType.equalsIgnoreCase( "SIFSimpleTime" )||
				classType.equalsIgnoreCase( "Time" ) ||
				classType.equalsIgnoreCase( "SIFTime" ) ){
			return FIELD_TIME;
		}else if( classType.equalsIgnoreCase( "SIFVersion" ) ){
			return FIELD_SIFVERSION;
		}else if( classType.equals( "duration" ) ){
			// NOTE: The value "duration" is case sensitive because there is a "Duration"
			// type in the ADK
			return FIELD_DURATION;
		}
		else
		{
			FieldType returnValue = new FieldType( ADKDataType.COMPLEX );
			returnValue.fClassType = classType;
			return returnValue;
		}
	}

	public static FieldType toEnumType( FieldType existingField, String enumName )
	throws ParseException
	{
		 if (enumName.equalsIgnoreCase( "Topics" )) {
			 return FIELD_STRING;
		 }

		if( existingField.getDataType() == ADKDataType.ENUM ){
			if( existingField.getClassType().equals( enumName ) )
			{
				return existingField;
			} else
			{
				throw new ParseException( "Field was already defined as an Enum with a different name: " +
						existingField.getDataType() +  " { ENUM:" + enumName + "}" );
			}
		} else
		{
			if( existingField.getDataType() != ADKDataType.STRING ){
				throw new ParseException( "Cannot define an enum for a type other than a String. Field:" +
						existingField.getDataType() +  " { ENUM:" + enumName + "}" );
			}
		}
		// TODO: We could support "YesNo" values as boolean fields, but we need to be able
		// to format them differently than booleans....
//		if( enumName.equals( "YesNo" )){
//			return FIELD_BOOLEAN;
//		}


		FieldType returnValue = new FieldType( ADKDataType.ENUM );
		returnValue.fClassType = enumName;
		return returnValue;
	}

	@Override
	public boolean equals(Object o) {
	    if( this == o )
	    {
	        return true;
	    }
	    if ((o != null) && (o.getClass().equals(this.getClass())))
	    {
	        FieldType compared = (FieldType)o;
	        if( fDataType == ADKDataType.COMPLEX || fDataType == ADKDataType.ENUM ){
				return fClassType.equals( compared.fClassType );
			} else	{
				return fDataType.equals( compared.fDataType );
			}
	    }
	    return false;
	}

	@Override
	public String toString()
	{
		if( isComplex() ){
			return "Complex Field: " + getClassType();
		} else if ( isEnum() ){
			return "Enum Field: " + getEnum();
		} else {
			return "Simple Field: " + fDataType;
		}

	}

}
