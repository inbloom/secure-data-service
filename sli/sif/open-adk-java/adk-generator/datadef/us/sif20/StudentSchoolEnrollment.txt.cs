
    /// <summary>
    /// Computes the "TimeFrame" element based on the given dates using the guidelines 
    /// published in the SIF Specification. In order for this calculation to be accurate,
    /// the EntryDate and ExitDate, if applicable need to be set on this object before calling
    /// this method.
    /// </summary>
    /// <param name="compareDate">The timestamp to base the calculation on. 
    /// For SIF_Requests, this must be the date of the SIF_Request, 
    /// and should be the value returned from <see cref="SifMessageInfo#TimeStamp"/> 
    /// For SIF_Events, this must be the timestamp of when the event is 
    /// going to be published, and can be the current time.</param>
    /// <returns>The TimeFrame value that was computed and set as the TimeFrame
    /// of this StudentSchoolEnrollment object. If the TimeFrame cannot be computed
    /// because there is no EntryDate set, null will be returned.</returns>
    public TimeFrame computeTimeFrame( DateTime compareDate )
	{
		DateTime? exitDate = ExitDate;
		DateTime? entryDate = EntryDate;
		
		TimeFrame tf = null;
		if( exitDate.HasValue ) {
			if( compareDate > exitDate.Value ) {
				tf = OpenADK.Library.us.Student.TimeFrame.HISTORICAL;
			}
		}
		if( (tf == null) && entryDate.HasValue ) {
			if ( compareDate < (entryDate.Value) ) {
		    	 tf = OpenADK.Library.us.Student.TimeFrame.FUTURE;
		    } else {
		    	 tf = OpenADK.Library.us.Student.TimeFrame.CURRENT;
		    }
		}
		
		this.SetTimeFrame( tf );
		return tf;
		
	}
