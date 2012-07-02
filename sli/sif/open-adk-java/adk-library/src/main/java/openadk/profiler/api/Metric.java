//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.profiler.api;

/**
 * 	Encapsulates an instance of a Metric.
 */
public class Metric
{
	public static final int OID_LENGTH = 8;
	
	
	/**
	 * 	The database-assigned ID
	 */
	protected int fID;
	
	/**
	 * 	The OID
	 */
	protected int[] fOID;
	
	/**
	 * 	If this record represents a timestamp, the absolute system time
	 */
	protected long fStarted;
	
	/**
	 * 	The elapsed time (in ms)
	 */
	protected long fElapsed;
	
	/**
	 * 	If this Metric is associated with a SIF_Message, the SIF_MsgId
	 */
	protected String fMsgId;
	
	/**
	 * 	A constant from the ObjectTypes.java class in SIFAgentLib (with 1000 added to it), 
	 * 	or -1 if this Metric is associated with an object type, 
	 */
	protected short fObjType;
	

	/**
	 * 	Constructor
	 */
	public Metric( int id )
	{
		fID = id;
		fOID = new int[ OID_LENGTH ];
	}
	
	/**
	 * 	Set an element of the OID
	 * 	@param element The zero-based element index
	 * 	@param value The element value
	 */
	public void setOID( int element, int value )
	{
		if( element < 0 || element >= fOID.length )
			throw new IllegalArgumentException( "Element index " + element + " out of range" );
		
		fOID[element] = value;
	}
	
	/**
	 * 	Sets the SIF_MsgId associated with this Metric
	 *	@return msgId A SIF_MsgId or null if no message is associated with this Metric
	 */
	public void setMsgId( String msgId )
	{
		fMsgId = msgId;
	}
	
	public void setObjectType( short objType )
	{
		fObjType = objType;
	}
	
	/**
	 * 	Return the OID as a string
	 */
	public String getOID()
	{
		StringBuffer b = new StringBuffer();
		if( fOID != null ) {
			for( int i = 0; i < fOID.length; i++ ) {
				b.append( String.valueOf( fOID[i] ) );
				if( i != fOID.length )
					b.append( '.' );
			}
		}
		
		return b.toString();
	}
	
}
