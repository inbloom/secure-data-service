//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.examples.tinysis;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map.Entry;

import openadk.library.ComparisonOperators;
import openadk.library.Condition;
import openadk.library.GroupOperators;
import openadk.library.Query;
import openadk.library.SIFDataObject;
import openadk.library.SIFMessageInfo;
import openadk.library.student.SchoolEnrollmentType;
import openadk.library.student.StudentDTD;
import openadk.library.student.StudentSchoolEnrollment;
import openadk.library.tools.mapping.ADKMappingException;
import openadk.library.tools.mapping.FieldAdaptor;
import openadk.library.tools.mapping.Mappings;
import openadk.library.tools.mapping.MappingsContext;
import openadk.library.tools.queries.QueryField;
import openadk.library.tools.queries.QueryFormatter;
import openadk.library.tools.queries.QueryFormatterException;
import openadk.library.tools.queries.SQLField;
import openadk.library.tools.queries.SQLQueryFormatter;

public class StudentEnrollmentProvider extends DataObjectProvider {

	public StudentEnrollmentProvider(String tableName, Mappings rootMappings) {
		super(tableName, rootMappings);
	}

	@Override
	protected void onMappingObject( SIFMessageInfo smi, Query q, MappingsContext mappingsContext, FieldAdaptor oma, SIFDataObject sdo) throws ADKMappingException {
		super.onMappingObject( smi, q, mappingsContext, oma, sdo);

		// Override the mapping operation to set the TimeFrame attribute, which is a calculated
		// value based on a comparison of EntryDate and ExitDate with the date of the SIF_Request
		StudentSchoolEnrollment sse = (StudentSchoolEnrollment)sdo;
		// Compute the TimeFrame attribute, based on the date of the SIF_Request
		sse.computeTimeFrame( smi.getTimestamp() );

		// The tinySIS only tracks home enrollments
		sse.setMembershipType(SchoolEnrollmentType._01_HOME_SCHOOL );
	}



	@Override
	protected void onAddQueryFormatterFields(SIFMessageInfo smi, Query query, MappingsContext context, SQLQueryFormatter formatter) {
		super.onAddQueryFormatterFields(smi, query, context, formatter);
		formatter.addField( StudentDTD.STUDENTSCHOOLENROLLMENT_TIMEFRAME, new TimeFrameSQLField( smi.getTimestamp() ) );
	}

	private class TimeFrameSQLField implements QueryField
	{
		private String fEvaluationDate;
		public TimeFrameSQLField(Calendar evaluationDate ){
			SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
			fEvaluationDate = sdf.format( evaluationDate.getTime() );
		}

		public String render( QueryFormatter formatter, Query query, Condition condition )
			throws QueryFormatterException
		{

			if( condition.getOperator() != ComparisonOperators.EQ ){
				throwOnInvalidCondition(condition);
			}

			// Search through the already-defined fields for the EntryDate and ExitDate
			// mappings
			SQLField entryDateField = null;
			SQLField exitDateField = null;
			for( Entry<Object, Object> entry : ((SQLQueryFormatter)formatter).getFields() )
			{
				Object key = entry.getKey();
				if( key instanceof String ){

					String path = (String)key;
					if( path.equals( "EntryDate" ) ){
						entryDateField = (SQLField)entry.getValue();
					} else if ( path.equals( "ExitDate" ) ){
						exitDateField = (SQLField)entry.getValue();
					}
				}

			}

			if( entryDateField == null || exitDateField == null ){
				throw new QueryFormatterException( "Unable to process a query with conditions. Missing a mapping for EntryDate or ExitDate" );
			}

          StringBuilder buffer = new StringBuilder();

          String value = condition.getValue();
          if( value == null ){
        	  throwOnInvalidCondition(condition);
          }
          value = value.toLowerCase();
          if( value.equals( "current" ) ){

        	  	// EntryDate must be today or in the past
	            buffer.append(formatter.renderField(condition.getField(), entryDateField));
	            buffer.append(formatter.getOperator(ComparisonOperators.LE));
	            buffer.append(formatter.renderValue(fEvaluationDate, entryDateField));

	            buffer.append(formatter.getOperator( GroupOperators.AND ));

	            // ExitDate must be in the future, equal to today, or null
	            buffer.append( formatter.getOpenBrace() );
		            buffer.append(formatter.renderField(condition.getField(), exitDateField));
		            buffer.append(formatter.getOperator(ComparisonOperators.GE ));
		            buffer.append(formatter.renderValue(fEvaluationDate, exitDateField));
	            buffer.append(formatter.getOperator( GroupOperators.OR ));
		            buffer.append(formatter.renderField(condition.getField(), exitDateField));
		            buffer.append( " IS NULL" );
	            buffer.append( formatter.getCloseBrace() );

          } else if( value.equals( "historical" ) ){
	            buffer.append(formatter.renderField(condition.getField(), exitDateField));
	            buffer.append(formatter.getOperator(ComparisonOperators.LT));
	            buffer.append(formatter.renderValue(fEvaluationDate, exitDateField));

          } else if( value.equals( "future" ) ){
	            buffer.append(formatter.renderField(condition.getField(), entryDateField));
	            buffer.append(formatter.getOperator(ComparisonOperators.GT));
	            buffer.append(formatter.renderValue(fEvaluationDate, entryDateField));
          } else {
        	  throwOnInvalidCondition(condition);
          }


          return buffer.toString();
		}

		private void throwOnInvalidCondition(Condition condition) throws QueryFormatterException {
			throw new QueryFormatterException("Unable to process query for " + condition.getField().name() + " using operator "
					+ condition.getOperator().toString() + " and value " + condition.getValue() );
		}


	}



}
