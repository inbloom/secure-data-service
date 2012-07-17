	/// <summary>  Gets the timestamp of this message from the SIF_Date and SIF_Time
	/// elements in its header.
	/// 
	/// If the message does not have a Edustructures.SifWorks element, one is created with
	/// a timestamp equal to the current time.
	/// 
	/// </summary>
	/// <returns> The message timestamp
	/// </returns>
	public virtual SifTimestamp Timestamp
	{
		get
		{
			return new SifTimestamp( new SifTime( SIF_Time ), new SifDate( SIF_Date ) );
		}

	}