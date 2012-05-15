package org.slc.sli.test.edfi.entities.meta;

public class CalendarMeta {
	 
	public final String id;
	
	public String sessionId;
	
	public   String gradingPeriodId;
	 
	public CalendarMeta (String id, SessionMeta sessionMeta) {
		
		this.id = id;
		this.sessionId = sessionMeta.id;	
		
	}
	
	
	public CalendarMeta (String id, GradingPeriodMeta gradingPeriodMeta) {
		
		this.id = id;
		this.gradingPeriodId = gradingPeriodMeta.id;	
	}
	
    @Override
    public String toString() {
        return "CalendarMeta [id=" + id + ", sessionId=" + sessionId + "]";
    }

}
