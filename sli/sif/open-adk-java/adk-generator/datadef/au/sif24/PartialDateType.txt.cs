	private int?[] fDate = new int?[3];
	
    /// <summary>
    /// Constructor that accepts values for all mandatory fields
    /// </summary>
    /// <param name="date">The DateTime object representing the date</param>
	public PartialDateType( DateTime? date ) : base( CommonDTD.PARTIALDATETYPE ) {
		SetDate( date );
	}


	
	/// <summary>
    /// Create a PartialDateType representing the given year
	/// </summary>
    /// <param name="year">Year (CCYY)</param>
	public PartialDateType( int? year ): base( CommonDTD.PARTIALDATETYPE ) {
		fDate[0] = year;
		calculateTextValue();
	}

	
	
    /// <summary>
    /// Create a PartialDateType representing the given year and month
    /// </summary>
    /// <param name="year">Year (CCYY)</param>
    /// <param name="month">Month (MM)</param>
	public PartialDateType( int? year, int? month ) : base( CommonDTD.PARTIALDATETYPE ) {
		SetDate( year, month );
	}


    /// <summary>
    /// Create a PartialDateType representing the given year and month
    /// </summary>
    /// <param name="year">Year (CCYY)</param>
    /// <param name="month">Month (MM)</param>
    /// <param name="day">Day (DD)</param>
	public PartialDateType( int? year, int? month, int? day ) : base( CommonDTD.PARTIALDATETYPE ) {
		SetDate( year, month, day );
	}


	/// <summary>
    /// the Year component of this partial date
	/// </summary>
    public int? Year
    {
        get { return fDate[ 0 ]; }
        set
        {
            fDate[ 0 ] = value;
            calculateTextValue();
        }
    }
   

	
    /// <summary>
    /// The Month component of this partial date
    /// </summary>
    public int? Month
    {
        get
        {
            return fDate[ 1 ];
        }
        set
        {
            fDate[ 1 ] = value;
            calculateTextValue();
        }
    }
	

    /// <summary>
    /// The Day component of this partial date
    /// </summary>
	public int? Day
	{
        get { return fDate[2]; }
        set
        {
            fDate[ 2 ] = value;
            calculateTextValue();
        }
	}
	
    /// <summary>
    /// Sets the date to be used for this PartialDateType
    /// </summary>
    /// <param name="date"></param>
	public void SetDate( DateTime? date )
	{
        if( date.HasValue ) {
            DateTime value = date.Value;
            SetDate( value.Year, value.Month, value.Day );
        } else {
            fDate[ 0 ] = null;
            fDate[ 1 ] = null;
            fDate[ 2 ] = null;
            RemoveField( CommonDTD.PARTIALDATETYPE );
            
        }
	}
	
	/// <summary>
	///  Returns a DateTime instance with its Year, Month and Day fields set
    /// to the values of this partial date type
	/// </summary>
	public DateTime Date
	{
        get
        {
            int year = fDate[0] == null ? -1 : fDate[0].Value;
            int month = fDate[ 1 ] == null ? -1 : fDate[ 1 ].Value;
            int day = fDate[ 2 ] == null ? -1 : fDate[ 2 ].Value;
           return new DateTime( year, month, day );
        }
	}


	
    /// <summary>
    /// Sets the integral components of this partial date instance
    /// </summary>
    /// <param name="year"></param>
    /// <param name="month"></param>
    /// <param name="day"></param>
	public void SetDate( int? year, int? month, int? day )
	{
		fDate[0] = year;
		fDate[1] = month;
		fDate[2] = day;
		calculateTextValue();
	}
	
    /// <summary>
    /// Sets the integral year and month components
    /// </summary>
    /// <param name="year"></param>
    /// <param name="month"></param>
	public void SetDate( int? year, int? month )
	{
		fDate[0] = year;
		fDate[1] = month;
		fDate[2] = null;
		calculateTextValue();
	}
	
	
    /// <summary>
    /// Parses the text value of this element into its representative integral parts 
    /// </summary>
	private void parseTextValue()
	{
		// The text value for this element can be an xsd:date, xsd:gYear, or xsd:gYearMonth
		// The time zone component is ignored
		// Set all of the values to null before parsing
        // The text value for this element can be an xsd:date, xsd:gYear, or xsd:gYearMonth
        // The time zone component is ignored
        // Set all of the values to null before parsing
        fDate[ 0 ] = null;
        fDate[ 1 ] = null;
        fDate[ 2 ] = null;
        String dateValue = Value;
        if( dateValue == null || dateValue.Length == 0 )
        {
            return;
        }

        // chop off the time zone
        int loc = dateValue.IndexOf( "Z" );
        if( loc > -1 )
        {
            dateValue = dateValue.Substring( 0, loc );
        }
        loc = dateValue.IndexOf( ":" );
        if( loc > -1 )
        {
            dateValue = dateValue.Substring( 0, loc - 3 );
        }

        // Now it should be in the format, YYYY, YYYY-mm, or YYYY-mm-dd
        switch( dateValue.Length )
        {
            case 4:

                fDate[ 0 ] = int.Parse( dateValue );
                break;
            case 7:
                fDate[ 0 ] = int.Parse( dateValue.Substring( 0, 4 ) );
                fDate[ 1 ] = int.Parse( dateValue.Substring( 5, 2 ) );
                break;
            default:
                DateTime? c = Adk.Dtd.GetFormatter( SifVersion.SIF20 ).ToDate( dateValue );
                if( c.HasValue ) {
                    fDate[0] = c.Value.Year;
                    fDate[ 1 ] = c.Value.Month;
                    fDate[ 2 ] = c.Value.Day;
                }
                break;
        }
        // If no value was parsed for year, remove the text value
        if( fDate[ 0 ] == null )
        {
            RemoveField( CommonDTD.PARTIALDATETYPE );
        }
	}
	
	/**
	 * Sets the text value of this element from its representative date parts.
	 * This is called any time one of the integral components of the date is set,
	 * overwriting any previous text value
	 */
	private void calculateTextValue()
	{
		// Do not call setField( ElementDef, SIFSimplyType ) because it would cause
		// the integral components to have to be re-parsed
		if( fDate[0] == null  )
		{
			// No year component
			RemoveField( CommonDTD.PARTIALDATETYPE );
		}
		else
		{
			String textValue = null;
			if( fDate[1] == null ){
				// Year only
				textValue = String.Format( "{0:0000}", fDate[0].Value );
			} else if ( fDate[2] == null ){
				// Year-month only
                textValue = String.Format( "{0:0000}-{1:00}", fDate[ 0 ].Value, fDate[ 1 ].Value );
			} else {
                textValue = String.Format( "{0:0000}-{1:00}-{2:00}", fDate[ 0 ].Value, fDate[ 1 ].Value, fDate[ 2 ].Value );
			}

			SifString value = new SifString( textValue );
			SimpleField field = value.CreateField( this, CommonDTD.PARTIALDATETYPE );
			SetField( field );
		}
	}
	
	
    /// <summary>
    /// Sets the value of this field
    /// </summary>
    /// <param name="id"></param>
    /// <param name="value"></param>
    /// <returns></returns>
	
	public override SimpleField SetField( IElementDef id, SifSimpleType value) {
        
		// verify the parameter values
		if( id == null || !(CommonDTD.PARTIALDATETYPE.Name.Equals( id.Name ))){
			throw new ArgumentException("ElementDef must be CommonDTD.PARTIALDATETYPE" );
		}
		if( value != null && value.DataType != SifDataType.String ){
            throw new ArgumentException( "Value type must be SIFDataType.STRING" );
		}
		
		// Allow any datatype to be set, but convert it to a string
		// This is important, because the value of this could be set to 
		// an int from mappings, because it was an int in SIF 1.5
		if( value != null && value.DataType != SifDataType.String ){
			value = new SifString( value.ToString() );
		}
		// Parse the text value into its representative parts
		SimpleField returnValue = base.SetField(id, value);
		parseTextValue();
		return returnValue;
	}
	
	
    /// <summary>
    /// Sets the value of this field
    /// </summary>
    /// <param name="field"></param>
	public override void SetField(SimpleField field) {
		if( field.SifValue.DataType != SifDataType.String ){
			field.SifValue = new SifString( field.SifValue.ToString() );
		}
		base.SetField(field);
		parseTextValue();
	}
	
	
    /// <summary>
    /// Gets or sets the string value of this field
    /// </summary>
	public override String TextValue {
        set
	    {
	        base.TextValue = value;
	        parseTextValue();
	    }
	}

    /// <summary>
    /// Returns the type of date represented by this partial date instance
    /// </summary>
	public DateType DataType {
        get
        {
            if ( fDate[0] != null ) {
                if ( fDate[1] != null ) {
                    if ( fDate[2] != null ) {
                        return DateType.Date;
                    }
                    else {
                        return DateType.GYearMonth;
                    }
                }
                else {
                    return DateType.GYear;
                }
            }
            return DateType.Unknown;
        }
	}
	
    /// <summary>
    /// The set of Data types supported by PartialDateType
	/// </summary>
	public enum DateType
	{
		/// <summary>
        /// The PartialDateType instance contains year, month, and day information 
		/// </summary>
		Date,
		
        /// <summary>
        /// The PartialDateType instance contains only the year
        /// </summary>
		GYear,
	
        /// <summary>
        /// The PartialDateType instance contains only the year and month
        /// </summary>
		GYearMonth,
	    /// <summary>
	    /// The PartialDateType instance does not contain enough information to
        /// determine the datatype. For example, the value could be null.
	    /// </summary>
		Unknown
	}

