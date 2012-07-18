//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.examples.tinysis;

import java.util.Calendar;

import openadk.library.Query;
import openadk.library.SIFDataObject;
import openadk.library.SIFMessageInfo;
import openadk.library.student.StudentSnapshot;
import openadk.library.tools.mapping.ADKMappingException;
import openadk.library.tools.mapping.FieldAdaptor;
import openadk.library.tools.mapping.Mappings;
import openadk.library.tools.mapping.MappingsContext;


public class StudentSnapshotProvider extends DataObjectProvider {

	public StudentSnapshotProvider(String tableName, Mappings rootMappings) {
		super( tableName, rootMappings );

	}

	@Override
	protected void onMappingObject( SIFMessageInfo smi, Query q, MappingsContext mappingsContext, FieldAdaptor oma, SIFDataObject sdo) throws ADKMappingException {
		super.onMappingObject( smi, q, mappingsContext, oma, sdo);

		// Override the mapping operation to set the TimeFrame attribute, which is a calculated
		// value based on a comparison of EntryDate and ExitDate with the date of the SIF_Request
		StudentSnapshot s = (StudentSnapshot)sdo;

		Calendar snapDate = Calendar.getInstance();
		s.setSnapDate( snapDate );
		s.setSchoolYear( 2008 );

	}

}
