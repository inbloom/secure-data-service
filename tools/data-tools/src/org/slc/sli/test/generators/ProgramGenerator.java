package org.slc.sli.test.generators;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.Program;
import org.slc.sli.test.edfi.entities.ProgramSponsorType;
import org.slc.sli.test.edfi.entities.ProgramType;

public class ProgramGenerator {

	private static final Logger log = Logger.getLogger(ProgramGenerator.class);
	
	public Program getProgram(String id) {
		Program program = new Program();
		program.setId(id);
		program.setProgramId(id);
		program.setProgramType(ProgramType.HEAD_START);
		program.setProgramSponsor(ProgramSponsorType.FEDERAL);
		return program;
	}
}
