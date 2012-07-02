//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *  The Subscriber message handler interface is implemented by classes that wish to 
 * 	process SIF_Event messages received from a zone. Consult the <i>ADK Developer
 * 	Guide</i> for more information about message handler interfaces.<p>
 *
 * 	<b>About SIF Events</b><p>
 *   
 * 	An Event is a notification to subscribing agents that a data object has been
 * 	added, changed, or deleted in the reporting application's database. In the
 * 	Schools Interoperability Framework, any agent may report SIF_Event messages for 
 * 	any object, even if that agent is not the authoritative publisher of the object.<p>
 * 
 * 	<b>Subscribing to SIF Events</b><p>
 * 
 * 	Agents that wish to receive SIF_Event messages must register with the zone
 * 	integration server as a Subscriber of one or more SIF Data Object types. To 
 * 	subscribe to events for a specific object type, register a <i>Subscriber</i> 
 * 	message handler with Zone or Topic instances. When registering Subscribers with zones, repeatedly call the 
 * 	{@linkplain openadk.library.Zone#setSubscriber(Subscriber, ElementDef, int)}
 * 	method for each SIF Data Object type you wish to subscribe to. When registering
 * 	message handlers with Topics, call the {@linkplain openadk.library.Topic#setSubscriber(Subscriber, int)}
 * 	method once to register with all zones bound to the topic (your <i>Subscriber</i>
 * 	implementation will be called whenever a SIF_Event is received on any of the
 * 	zones bound to that topic.) Be sure to specify the <code>ADKFlags.PROV_SUBSCRIBE</code> 
 * 	flag as the last parameter to these methods if you wish the ADK to send a 
 * 	SIF_Subscribe message to the zone when it connects.<p>
 * 
 *  	When a SIF_Event message is received, it is dispatched to the
 * 	appropriate Subscriber message handler's <code>onEvent</code> method for 
 * 	processing. Obtain a DataObjectInputStream from the Event instance
 * 	by calling the Event parameter's {@linkplain openadk.library.Event#getData()}
 * 	method, then repeatedly call the stream's {@linkplain openadk.library.DataObjectInputStream#readDataObject()}
 * 	method to get the next SIFDataObject in the event payload.<p>
 * 
 * 	For example,<p>
 * 
 * 	<code>
 *  &nbsp;&nbsp;&nbsp;&nbsp;public void onEvent( Event event, Zone zone, MessageInfo info )<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;{<br/>
 *	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DataObjectInputStream payload = event.getData();<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;while( payload.available() ) {<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;StudentPersonal sp = (StudentPersonal)payload.readDataObject();<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;switch( event.getAction() ) {<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;case Event.ADD:<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Add student...<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;break;<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;case Event.CHANGE:<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Change student...<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;break;<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;case Event.DELETE:<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Delete student...<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;break;<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br/>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;}</code>
 * 	<p> 
 * 
 * 	If <code>onEvent</code> returns successfully, the ADK acknowledges the SIF_Event
 * 	message with a success SIF_Ack. If a SIFException or other ADKException is 
 * 	thrown, the ADK acknowledges the SIF_Event with an error SIF_Ack using the error
 * 	category, code, and description from the exception.<p> 
 *
 *  @author Eric Petersen
 *  @since ADK 1.0
 */
public interface Subscriber
{
	/**
	 *  Respond to a SIF_Event received from a zone.<p>
	 *
	 *  @param event Encapsulates the SIF_Event message and the SIFDataObjects
	 * 		contained within
	 *  @param zone The zone from which this event originated
	 *  @param info Information about the SIF_Event message envelope and header
	 * 
	 * 	@exception ADKException may be thrown to return an error SIF_Ack to the zone. 
	 * 		Throw a SIFException for control over the error category, code, description
	 * 		and detailed description. If no exception is raised and this method 
	 * 		returns successfully, the ADK returns a success SIF_Ack to the zone. 
	 */
	public void onEvent( Event event, Zone zone, MessageInfo info )
		throws ADKException;

}
