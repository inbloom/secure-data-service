//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.*;

public class DataObjectInputStreamImpl implements DataObjectInputStream
{
	protected SIFDataObject[] fReady = null;
	protected ElementDef fObjType = null;
	protected int fIndex = 0;

	/**
	 *  Construct a new DataObjectInputStream
	 *  @return A new DataObjectInputStream object, which will always be a
	 *      an instanceof DataObjectInputStreamImpl as defined by the
	 *      <code>adkglobal.factory.DataObjectInputStream</code> system property.
	 */
	public static DataObjectInputStreamImpl newInstance()
		throws ADKException
	{
		String cls = System.getProperty("adkglobal.factory.DataObjectInputStream");
		if( cls == null )
			cls = "openadk.library.impl.DataObjectInputStreamImpl";

		try
		{
			return (DataObjectInputStreamImpl)Class.forName(cls).newInstance();
		}
		catch( Throwable thr )
		{
			throw new ADKException("ADK could not create an instance of the class "+cls+": "+thr,null);
		}
	}
	

	/**
	 *  Assign an array of SIFDataObjects to this stream. The array is appended
	 *  to any data currently available and will be made available by the
	 *  readDataObject method as soon as the current array is exhaused.
	 */
	public synchronized void setData( SIFDataObject[] data )
	{
		if( data == null || data.length == 0 || data[0] == null )
			return;

		if( data.length > 0 )
		{
			ElementDef typ = data[0].getElementDef();
			if( fObjType != null && fObjType != typ && fReady != null )
				throw new IllegalArgumentException( "Cannot add SIFDataObjects of this type to the stream; type differs from existing data" );
			fObjType = typ;
		}

		if( fReady != null )
		{
			//  Determine the last non-null element in fReady
			int last = -1;
			for( int i = fReady.length - 1; i >= 0; i-- ) {
				if( fReady[i] != null ) {
					last = i;
					break;
				}
			}

			if( last == -1 )
			{
				//  Replace fReady with 'data'
				fReady = data;
				fIndex = 0;
			}
			else
			{
				//  Resize fReady and append 'data' to it
				SIFDataObject[] tmp = new SIFDataObject[ last+1+data.length ];
				System.arraycopy( fReady,0,tmp,0,last+1 );
				System.arraycopy( fReady,last+1,data,0,data.length );
				fReady = tmp;
			}
		}
		else
		{
			fReady = data;
			fIndex = 0;
		}
	}

	/**
	 *  Determines the type of SIF Data Object provided by the stream
	 *  @return An ElementDef constant
	 */
	public ElementDef getObjectType() {
		return fObjType;
	}
	
	/**
	 * 	Sets the type of SIF Data Object provided by the stream
	 * 	@param objType An ElementDf constant 
	 */
	public void setObjectType( ElementDef objType ) {
		fObjType=objType;
	}

	/**
	 *  Read the next SIFDataObject from the stream
	 */
	public synchronized SIFDataObject readDataObject()
		throws ADKException
	{
		if( fReady != null && fIndex < fReady.length )
    		return fReady[ fIndex++ ];

		fReady = null;
    	return null;
	}


	/**
	 *  Determines if any SIFDataObjects are currently available for reading
	 */
	public synchronized boolean available()
	{
		return fReady != null && fIndex < fReady.length;
	}
}
