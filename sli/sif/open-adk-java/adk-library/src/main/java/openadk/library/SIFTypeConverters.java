//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.Duration;

public class SIFTypeConverters {


	/**
	 * A type converter that can convert SIF Int XML values or
	 * Java Integer values to a {@link SIFSimpleType}
	 */
	public static SIFTypeConverter<Integer> INT = new SIFIntConverter();

	/**
	 * A type converter that can convert SIF Int XML values or
	 * Java Integer values to a {@link SIFSimpleType}
	 */
	public static SIFTypeConverter<Long> LONG = new SIFLongConverter();

	/**
	 * A type converter that can convert SIF Boolean XML values or
	 * Java Boolean values to a {@link SIFSimpleType}
	 */
	public static SIFTypeConverter<Boolean> BOOLEAN = new SIFBooleanConverter();

	/**
	 * A type converter that can convert SIF Decimal XML values or
	 * Java BigDecimal values to a {@link SIFSimpleType}
	 */
	public static SIFTypeConverter<BigDecimal> DECIMAL = new SIFDecimalConverter();

	/**
	 * A type converter that can convert SIF Date XML values or
	 * Java Calendar values to a {@link SIFSimpleType}
	 */
	public static SIFTypeConverter<Calendar> DATE = new SIFDateConverter();

	/**
	 * A type converter that can convert SIF DateTime XML values or
	 * Java Calendar values to a {@link SIFSimpleType}
	 */
	public static SIFTypeConverter<Calendar> DATETIME = new SIFDateTimeConverter();

	/**
	 * A type converter that can convert SIF Float XML values or java
	 * float values to a {@link SIFSimpleType}
	 */
	public static SIFTypeConverter<Float> FLOAT = new SIFFloatConverter();

	/**
	 * A type converter that can convert SIF Time XML values or
	 * Java Calendar values to a {@link SIFSimpleType}
	 */
	public static SIFTypeConverter<Calendar> TIME = new SIFTimeConverter();

	/**
	 * A type converter that can convert SIF String XML values or
	 * Java String values to a {@link SIFSimpleType}
	 */
	public static SIFTypeConverter<String> STRING = new SIFStringConverter();

	/**
	 * A type converter that can convert SIF Duration XML values or
	 * Java Duration values to a {@link SIFSimpleType}
	 */
	public static final SIFTypeConverter<Duration> DURATION = new SIFDurationConverter();

	/**
	 * A Type converter for Int values
	 * @author Andrew Elmhorst
	 *
	 */
	private static final class SIFIntConverter extends SIFTypeConverter<Integer> {
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFTypeConverter#parse(com.edustructures.sifworks.SIFFormatter, java.lang.String)
		 */
		@Override
		public SIFSimpleType<Integer> parse(SIFFormatter formatter, String xmlValue) throws ADKTypeParseException {
			SIFInt returnValue = null;
			try
			{
				returnValue = new SIFInt( formatter.toInteger( xmlValue ) );
			} catch( IllegalArgumentException iae ){
				throw new ADKTypeParseException( "Error converting value '" + xmlValue + "' to an Integer: " + iae.getMessage(), null, iae );
			}
			return returnValue;
		}


		/* (non-Javadoc)
		 * @see SIFTypeConverter#toString(SIFFormatter, SIFSimpleType)
		 */
		@Override
		public String toString( SIFFormatter formatter, SIFSimpleType<Integer> value) {
			Integer i = value.getValue();
			if( i != null ){
				return formatter.toString( i.intValue() );
			}
			return null;
		}


		@Override
		public SIFDataType getDataType() {
			return SIFDataType.INT;
		}


		@Override
		public int getSQLType() {
			return Types.INTEGER;
		}


		@Override
		public SIFSimpleType<Integer> getSIFSimpleType( Object nativeType ) {
			if( nativeType == null || nativeType instanceof Integer ){
				return new SIFInt( (Integer)nativeType );
			}
			if( nativeType instanceof String ){
				try{
					return parse( ADK.getTextFormatter(), (String) nativeType );
				} catch( ADKParsingException adkpe ){
					throw new IllegalArgumentException( "Unable to convert '" + nativeType + "' to Integer", adkpe );
				}
			} else if( nativeType instanceof BigDecimal ){
				return new SIFInt( ((BigDecimal)nativeType).intValue() );
			}else if( nativeType instanceof Float ){
				return new SIFInt( ((Float)nativeType).intValue() );
			}
			throw new IllegalArgumentException( "Unable to convert " + nativeType + "(" + nativeType.getClass().getCanonicalName() + ") to Integer" );
		}
	}

	/**
	 * A Type converter for Int values
	 * @author Andrew Elmhorst
	 *
	 */
	private static final class SIFLongConverter extends SIFTypeConverter<Long> {
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFTypeConverter#parse(com.edustructures.sifworks.SIFFormatter, java.lang.String)
		 */
		@Override
		public SIFSimpleType<Long> parse(SIFFormatter formatter, String xmlValue) throws ADKTypeParseException {
			SIFLong returnValue = null;
			try
			{
				returnValue = new SIFLong( formatter.toLong( xmlValue ) );
			} catch( IllegalArgumentException iae ){
				throw new ADKTypeParseException( "Error converting value '" + xmlValue + "' to a Long: " + iae.getMessage(), null, iae );
			}
			return returnValue;
		}


		/* (non-Javadoc)
		 * @see SIFTypeConverter#toString(SIFFormatter, SIFSimpleType)
		 */
		@Override
		public String toString( SIFFormatter formatter, SIFSimpleType<Long> value) {
			Long i = value.getValue();
			if( i != null ){
				return formatter.toString( i.longValue() );
			}
			return null;
		}


		@Override
		public SIFDataType getDataType() {
			return SIFDataType.LONG;
		}


		@Override
		public int getSQLType() {
			return Types.BIGINT;
		}


		@Override
		public SIFSimpleType<Long> getSIFSimpleType( Object nativeType ) {
			if( nativeType == null || nativeType instanceof Long ){
				return new SIFLong( (Long)nativeType );
			}
			if( nativeType instanceof String ){
				try{
					return parse( ADK.getTextFormatter(), (String) nativeType );
				} catch( ADKParsingException adkpe ){
					throw new IllegalArgumentException( "Unable to convert '" + nativeType + "' to Integer", adkpe );
				}
			} else if( nativeType instanceof BigDecimal ){
				return new SIFLong( ((BigDecimal)nativeType).longValue() );
			}else if( nativeType instanceof Float ){
				return new SIFLong( ((Float)nativeType).longValue() );
			}
			throw new IllegalArgumentException( "Unable to convert " + nativeType + "(" + nativeType.getClass().getCanonicalName() + ") to Long" );
		}
	}


	/**
	 * A Type converter for Decimal values
	 * @author Andrew Elmhorst
	 *
	 */
	private static final class SIFDecimalConverter extends SIFTypeConverter<BigDecimal> {
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFTypeConverter#parse(com.edustructures.sifworks.SIFFormatter, java.lang.String)
		 */
		@Override
		public SIFSimpleType<BigDecimal> parse(SIFFormatter formatter, String xmlValue) throws ADKTypeParseException {
			SIFDecimal returnValue = null;
			try
			{
				returnValue = new SIFDecimal( formatter.toDecimal( xmlValue ) );
			} catch( IllegalArgumentException iae ){
				throw new ADKTypeParseException( "Error converting value '" + xmlValue + "' to a BigDecimal: " + iae.getMessage(), null, iae );
			}
			return returnValue;
		}


		/* (non-Javadoc)
		 * @see SIFTypeConverter#toString(SIFFormatter, SIFSimpleType)
		 */
		@Override
		public String toString( SIFFormatter formatter, SIFSimpleType<BigDecimal> value) {
			BigDecimal bd = value.getValue();
			if( bd != null ){
				return formatter.toString( bd );
			}
			return null;
		}


		@Override
		public SIFDataType getDataType() {
			return SIFDataType.DECIMAL;
		}


		@Override
		public int getSQLType() {
			return Types.DECIMAL;
		}


		@Override
		public SIFSimpleType<BigDecimal> getSIFSimpleType(Object nativeType) {

			if( nativeType == null || nativeType instanceof BigDecimal ){
				return new SIFDecimal( (BigDecimal)nativeType );
			}
			if( nativeType instanceof String ){
				try{
					return parse( ADK.getTextFormatter(), (String) nativeType );
				} catch( ADKParsingException adkpe ){
					throw new IllegalArgumentException( "Unable to convert '" + nativeType + "' to BigDecimal", adkpe );
				}
			} else if( nativeType instanceof Integer ){
				return new SIFDecimal( new BigDecimal((Integer)nativeType));
			}else if( nativeType instanceof Float ){
				return new SIFDecimal( new BigDecimal( (Float)nativeType ) );
			}else if( nativeType instanceof Double ){
				return new SIFDecimal( new BigDecimal( (Double)nativeType ) );
			}
			throw new IllegalArgumentException( "Unable to convert " + nativeType + "(" + nativeType.getClass().getCanonicalName() + ") to BigDecimal" );
		}

	}


	/**
	 * A Type converter for Int values
	 * @author Andrew Elmhorst
	 *
	 */
	private static final class SIFFloatConverter extends SIFTypeConverter<Float> {
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFTypeConverter#parse(com.edustructures.sifworks.SIFFormatter, java.lang.String)
		 */
		@Override
		public SIFSimpleType<Float> parse(SIFFormatter formatter, String xmlValue) throws ADKTypeParseException {
			SIFFloat returnValue = null;
			try
			{
				returnValue = new SIFFloat( formatter.toFloat( xmlValue ) );
			} catch( IllegalArgumentException iae ){
				throw new ADKTypeParseException( "Error converting value '" + xmlValue + "' to a BigDecimal: " + iae.getMessage(), null, iae );
			}
			return returnValue;
		}


		/* (non-Javadoc)
		 * @see SIFTypeConverter#toString(SIFFormatter, SIFSimpleType)
		 */
		@Override
		public String toString( SIFFormatter formatter, SIFSimpleType<Float> value) {
			Float bd = value.getValue();
			if( bd != null ){
				return formatter.toString( bd );
			}
			return null;
		}


		@Override
		public SIFDataType getDataType() {
			return SIFDataType.FLOAT;
		}


		@Override
		public int getSQLType() {
			return Types.FLOAT;
		}


		@Override
		public SIFSimpleType<Float> getSIFSimpleType(Object nativeType) {
			if( nativeType == null || nativeType instanceof Float ){
				return new SIFFloat( (Float)nativeType );
			}
			if( nativeType instanceof String ){
				try{
					return parse( ADK.getTextFormatter(), (String) nativeType );
				} catch( ADKParsingException adkpe ){
					throw new IllegalArgumentException( "Unable to convert '" + nativeType + "' to Float", adkpe );
				}
			} else if( nativeType instanceof Integer ){
				return new SIFFloat( new Float((Integer)nativeType));
			}else if( nativeType instanceof BigDecimal ){
				return new SIFFloat( ((BigDecimal)nativeType ).floatValue() );
			}else if( nativeType instanceof Double ){
				return new SIFFloat( ((Double)nativeType ).floatValue() );
			}

			throw new IllegalArgumentException( "Unable to convert " + nativeType + "(" + nativeType.getClass().getCanonicalName() + ") to Float" );

		}

	}



	/**
	 * A Type converter for Boolean values
	 * @author Andrew Elmhorst
	 *
	 */
	private static final class SIFBooleanConverter extends SIFTypeConverter<Boolean> {
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFTypeConverter#parse(com.edustructures.sifworks.SIFFormatter, java.lang.String)
		 */
		@Override
		public SIFSimpleType<Boolean> parse(SIFFormatter formatter, String xmlValue) throws ADKTypeParseException {
			try
			{
				return new SIFBoolean( formatter.toBoolean( xmlValue ) );
			}
			catch( IllegalArgumentException iae ){
				throw new ADKTypeParseException(
						"Error converting value '" +
							xmlValue +
						"' to a boolean: " +
							iae.getMessage(), null, iae );
			}
		}


		/* (non-Javadoc)
		 * @see SIFTypeConverter#toString(SIFFormatter, SIFSimpleType)
		 */
		@Override
		public String toString( SIFFormatter formatter, SIFSimpleType<Boolean> value) {
			Boolean b = value.getValue();
			if( b != null ){
				return formatter.toString( value.getValue().booleanValue() );
			}
			return null;
		}


		@Override
		public SIFDataType getDataType() {
			return SIFDataType.BOOLEAN;
		}


		@Override
		public int getSQLType() {
			return Types.BOOLEAN;
		}


		@Override
		public SIFSimpleType<Boolean> getSIFSimpleType(Object nativeType) {
			if( nativeType == null || nativeType instanceof Boolean ){
				return new SIFBoolean( (Boolean)nativeType );
			}
			if( nativeType instanceof String ){
				try{
					return parse( ADK.getTextFormatter(), (String) nativeType );
				} catch( ADKParsingException adkpe ){
					throw new IllegalArgumentException( "Unable to convert '" + nativeType + "' to BigDecimal", adkpe );
				}
			} else if( nativeType instanceof Integer ){
				return new SIFBoolean( (Integer)nativeType == 1 );
			}
			throw new IllegalArgumentException( "Unable to convert " + nativeType + "(" + nativeType.getClass().getCanonicalName() + ") to BigDecimal" );

		}
	}

	private static final class SIFStringConverter extends SIFTypeConverter<String> {
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFTypeConverter#parse(com.edustructures.sifworks.SIFFormatter, java.lang.String)
		 */
		@Override
		public SIFSimpleType<String> parse(SIFFormatter formatter, String xmlValue) throws ADKTypeParseException {
			return new SIFString( xmlValue );
		}

		/* (non-Javadoc)
		 * @see SIFTypeConverter#toString(SIFFormatter, SIFSimpleType)
		 */
		@Override
		public String toString( SIFFormatter formatter, SIFSimpleType<String> value) {
			return value.getValue();
		}

		@Override
		public SIFDataType getDataType() {
			return SIFDataType.STRING;
		}

		@Override
		public int getSQLType() {
			return Types.VARCHAR;
		}

		@Override
		public SIFSimpleType<String> getSIFSimpleType(Object nativeType) {
			if( nativeType != null && !(nativeType instanceof String ) ){
				return new SIFString( nativeType.toString() );
			}
			return new SIFString( (String)nativeType );
		}
	}

	/**
	 * A SIFTypeConverter for Date values
	 * @author Andrew Elmhorst
	 *
	 */
	private static final class SIFDateConverter extends SIFTypeConverter<Calendar> {
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFTypeConverter#parse(com.edustructures.sifworks.SIFFormatter, java.lang.String)
		 */
		@Override
		public SIFSimpleType<Calendar> parse(SIFFormatter formatter, String xmlValue) throws ADKTypeParseException {
			SIFDate returnValue = null;
			try
			{
				returnValue =  new SIFDate( formatter.toDate( xmlValue ) );
			} catch( IllegalArgumentException iae ){
				throw new ADKTypeParseException( "Error converting value '" + xmlValue + "' to a Calendar: " + iae.getMessage(), null, iae );
			}
			return returnValue;
		}

		/* (non-Javadoc)
		 * @see SIFTypeConverter#toString(SIFFormatter, SIFSimpleType)
		 */
		@Override
		public String toString( SIFFormatter formatter, SIFSimpleType<Calendar> value) {
			Calendar c = value.getValue();
			if( c != null ){
				return formatter.toDateString( c );
			}
			return null;
		}

		@Override
		public SIFDataType getDataType() {
			return SIFDataType.DATE;
		}

		@Override
		public int getSQLType() {
			return Types.DATE;
		}

		@Override
		public SIFSimpleType<Calendar> getSIFSimpleType(Object nativeType) {
			if( nativeType != null ){
				if( nativeType instanceof Calendar ){
					return new SIFDate( (Calendar)nativeType );
				} else if( nativeType instanceof Date ){
					return new SIFDate( (Date)nativeType );
				} else if( nativeType instanceof String ){
					try{
						return parse( ADK.getTextFormatter(), (String) nativeType );
					} catch( ADKParsingException adkpe ){
						throw new IllegalArgumentException( "Unable to convert '" + nativeType + "' to Integer", adkpe );
					}
				}

				throw new IllegalArgumentException( "Unable to convert " + nativeType + "(" + nativeType.getClass().getCanonicalName() + ") to a SIFDate" );
			}
			return new SIFDate( (Calendar)null );


		}
	}

	/**
	 * A SIFTypeConverter for Date values
	 * @author Andrew Elmhorst
	 *
	 */
	private static final class SIFDateTimeConverter extends SIFTypeConverter<Calendar> {
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFTypeConverter#parse(com.edustructures.sifworks.SIFFormatter, java.lang.String)
		 */
		@Override
		public SIFSimpleType<Calendar> parse(SIFFormatter formatter, String xmlValue) throws ADKTypeParseException {
			SIFDateTime returnValue = null;
			try
			{
				returnValue = new SIFDateTime( formatter.toDateTime( xmlValue ) );
			} catch( IllegalArgumentException iae ){
				throw new ADKTypeParseException( "Error converting value '" + xmlValue + "' to a DateTime: " + iae.getMessage(), null, iae );
			}
			return returnValue;
		}

		/* (non-Javadoc)
		 * @see SIFTypeConverter#toString(SIFFormatter, SIFSimpleType)
		 */
		@Override
		public String toString( SIFFormatter formatter, SIFSimpleType<Calendar> value) {
			Calendar c = value.getValue();
			if( c != null ){
				return formatter.toDateTimeString( c );
			}
			return null;
		}

		@Override
		public SIFDataType getDataType() {
			return SIFDataType.DATETIME;
		}

		@Override
		public int getSQLType() {
			return Types.TIMESTAMP;
		}

		/**
		 * @author harakim
		 */
		@Override
		public SIFSimpleType<Calendar> getSIFSimpleType(Object nativeType) {
			
			Calendar calendar;
			
			if ( nativeType == null ) {
				calendar = null;
			} else if ( nativeType instanceof Calendar ) {
				calendar = (Calendar)nativeType;
			} else if ( nativeType instanceof Date ) {
				calendar = Calendar.getInstance();
				calendar.setTime( (Date)nativeType );
			} else {
				throw new IllegalArgumentException( "Unable to convert " + nativeType + " to SIFDateTime");
			}
			
			return new SIFDateTime( calendar );
		}
	}

	/**
	 * A SIFTypeConverter for Time values
	 * @author Andrew Elmhorst
	 *
	 */
	private static final class SIFTimeConverter extends SIFTypeConverter<Calendar> {
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFTypeConverter#parse(com.edustructures.sifworks.SIFFormatter, java.lang.String)
		 */
		@Override
		public SIFSimpleType<Calendar> parse(SIFFormatter formatter, String xmlValue) throws ADKTypeParseException {
			SIFTime returnValue = null;
			try
			{
				returnValue = new SIFTime( formatter.toTime( xmlValue ) );
			} catch( IllegalArgumentException iae ){
				throw new ADKTypeParseException( "Error converting value '" + xmlValue + "' to a Time: " + iae.getMessage(), null, iae );
			}
			return returnValue;
		}

		/* (non-Javadoc)
		 * @see SIFTypeConverter#toString(SIFFormatter, SIFSimpleType)
		 */
		@Override
		public String toString( SIFFormatter formatter, SIFSimpleType<Calendar> value) {
			Calendar c = value.getValue();
			if( c != null ){
				return formatter.toTimeString( c );
			}
			return null;
		}

		@Override
		public SIFDataType getDataType() {
			return SIFDataType.TIME;
		}

		@Override
		public int getSQLType() {
			return Types.TIMESTAMP;
		}

		/**
		 * @author harakim
		 */
		@Override
		public SIFSimpleType<Calendar> getSIFSimpleType(Object nativeType) {
			Calendar calendar;
			
			if ( nativeType == null ) {
				calendar = null;
			} else if ( nativeType instanceof Calendar ) {
				calendar = (Calendar)nativeType;
			} else if ( nativeType instanceof Date ) {
				calendar = Calendar.getInstance();
				calendar.setTime( (Date)nativeType );
			} else {
				throw new IllegalArgumentException( "Unable to convert " + nativeType + " to SIFTime");
			}
			
			return new SIFTime( calendar );		
		}
	}

	/**
	 * A Type converter for Int values
	 * @author Andrew Elmhorst
	 *
	 */
	private static final class SIFDurationConverter extends SIFTypeConverter<Duration> {
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFTypeConverter#parse(com.edustructures.sifworks.SIFFormatter, java.lang.String)
		 */
		@Override
		public SIFSimpleType<Duration> parse(SIFFormatter formatter, String xmlValue) throws ADKTypeParseException {
			SIFDuration returnValue = null;
			try
			{
				returnValue = new SIFDuration( formatter.toDuration( xmlValue ) );
			} catch( IllegalArgumentException iae ){
				throw new ADKTypeParseException( "Error converting value '" + xmlValue + "' to a Duration: " + iae.getMessage(), null, iae );
			}
			return returnValue;
		}


		/* (non-Javadoc)
		 * @see SIFTypeConverter#toString(SIFFormatter, SIFSimpleType)
		 */
		@Override
		public String toString( SIFFormatter formatter, SIFSimpleType<Duration> value) {
			Duration d = value.getValue();
			if( d != null ){
				return formatter.toString( d );
			}
			return null;
		}


		@Override
		public SIFDataType getDataType() {
			return SIFDataType.DURATION;
		}


		@Override
		public int getSQLType() {
			return Types.TIME;
		}


		@Override
		public SIFSimpleType<Duration> getSIFSimpleType( Object nativeType ) {
			if( nativeType != null && !(nativeType instanceof Duration ) ){
				throw new IllegalArgumentException( "Unable to convert " + nativeType.getClass().getCanonicalName() + "- '" + nativeType + "' to Duration" );
			}
			return new SIFDuration( (Duration)nativeType );
		}
	}


}
