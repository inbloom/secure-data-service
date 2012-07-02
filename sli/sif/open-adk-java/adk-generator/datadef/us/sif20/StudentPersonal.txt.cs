 /// <summary>
    /// Adds a StudentAddressList instance for interoperating with multiple addresses in SIF Version 1.x
    /// </summary>
    /// <remarks>
    /// <para>In SIF 2.0, the StudentAddressList object is not a repeatable element. Use this method of adding
    ///  student address only if you are creating an object for use in SIF 1.x
    /// </para>
    /// <para>In SIF 2.0, or if you are only dealing with a single Student address, you can add the address 
    /// to the StudentPersonal object by calling the <see cref="StudentAddressList#Add"/> method
    /// </remarks>
    /// <param name="value"></param>
	public void AddAddressList( StudentAddressList value ) { 
		AddChild( StudentDTD.STUDENTPERSONAL_ADDRESSLIST, value);
	}


    /// <summary>
    /// Adds a StudentAddressList instance for interoperating with multiple addresses in SIF Version 1.x
    /// </summary>
    /// <remarks>
    /// <para>In SIF 2.0, the StudentAddressList object is not a repeatable element. Use this method of adding
    ///  student address only if you are creating an object for use in SIF 1.x
    /// </para>
    /// <para>In SIF 2.0, or if you are only dealing with a single Student address, you can add the address 
    /// to the StudentPersonal object by calling the <see cref="StudentAddressList#Add"/> method
    /// </remarks>
    /// <param name="pickupOrDropoff">Specifies if this is a pickup or dropoff address</param>
    /// <param name="dayOfWeek"> The days of the week for which the pickup or dropoff address is valid</param>
    /// <param name="address">The street address</param>
    public void AddAddressList( PickupOrDropoff pickupOrDropoff, String dayOfWeek, Address address ) {
		AddChild( StudentDTD.STUDENTPERSONAL_ADDRESSLIST, new StudentAddressList( pickupOrDropoff, dayOfWeek, address ) );
	}





    /// <summary>
    ///  Gets all <code>StudentAddressList</code> object instances. More than one instance can be defined for this object in SIF 1.x,
    ///  but this object is not repeatable in SIF 2.x. In SIF 2.x, all of the student addresses can be obtained by calling
    ///  <see cref="StudentAddressList#Addresses"/> property
    /// </summary>
    /// <returns></returns>
	public StudentAddressList[] AddressLists {
        get
        {
            SifElementList v = GetChildList(StudentDTD.STUDENTPERSONAL_ADDRESSLIST );
            StudentAddressList[] cvt = new StudentAddressList[v.Count];
            v.CopyTo(cvt, 0);
            return cvt;
        }
	}		
	