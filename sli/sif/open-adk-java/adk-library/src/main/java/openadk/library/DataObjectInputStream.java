//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *  DataObjectInputStream is supplied to message handlers to allow agents to 
 * 	stream an arbitrarily large set of SIFDataObjects from SIF_Response 
 * 	and SIF_Event messages.<p>
 *
 *  To use DataObjectInputStream, construct a while loop that calls 
 * 	{@link #available()} to determine if more objects are available from the 
 * 	stream. Within the loop, call {@link #readDataObject} to obtain the next 
 * 	SIFDataObject instance from the stream. Note all SIFDataObjects in the stream 
 * 	are of the same type. To determine the type, call {@link #getObjectType} 
 * 	to retrieve an ElementDef constant from the {@linkplain com.edustructures.sifworks.SIFDTD} class.<p>
 * 
 * 	For example,<p>
 * 
 * 	<code>if( myStream.getObjectType() == SIFDTD.STUDENTPERSONAL ) {<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;while( myStream.available() ) {<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;StudentPersonal sp = (StudentPersonal)myStream.readDataObject();<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;}<br/>
 * 	}</code>
 * 	<p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public interface DataObjectInputStream
{
	/**
	 *  Read the next SIFDataObject from the stream
	 */
	public SIFDataObject readDataObject()
		throws ADKException;

	/**
	 *  Determines the type of SIF Data Object provided by the stream
	 *  @return An ElementDef constant from the SIFDTD class (e.g. <code>SIFDTD.STUDENTPERSONAL</code>)
	 */
	public ElementDef getObjectType();

	/**
	 *  Determines if any SIFDataObjects are currently available for reading
	 */
	public boolean available();
}
