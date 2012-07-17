//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import openadk.library.impl.DataObjectInputStreamImpl;

/**
 *  Encapsulates a SIF Event.<p>
 * 
 * 	An Event is a notification to subscribing agents that a data object has been
 * 	added, changed, or deleted in the reporting application's database. In the
 * 	Schools Interoperability Framework, any agent may report SIF_Event messages for 
 * 	any object, even if that agent is not the authoritative publisher of the object.<p>
 * 
 * 	<b>Reporting SIF Events</b><p>
 * 
 *  To report a SIF_Event to a zone, construct an Event instance and call the
 *  {@link openadk.library.Zone#reportEvent(Event)} method. Supply a
 *  SIFDataObject instance and an action code to the Event constructor. You may
 * 	also call alternative forms of the <code>reportEvent</code> method that accept 
 * 	SIFDataObjects and an action code as parameters. Note Events cannot be reported 
 * 	to a Topic because the ADK cannot determine which of its zones should receive 
 *	the event. If you're using Topics, you must report events to the specific Zone 
 *	or Zones to which the data applies.<p>
 * 
 * 	<b>Subscribing to SIF Events</b><p>
 * 
 * 	To subscribe to SIF_Events for a specific object type, register a <i>Subscriber</i> 
 * 	message handler with Zone or Topic instances. Call the <code>setSubscriber</code>
 * 	method repeatedly for each object type you wish to subscribe to. When the ADK
 * 	connects to a zone, it sends SIF_Subscribe messages for each object type. When a 
 * 	SIF_Event message is received by the ADK, it is dispatched to your 
 * 	{@linkplain openadk.library.Subscriber#onEvent(Event, Zone, MessageInfo)}
 * 	method for processing. Refer to that method for more information.<p>
 *
 *  @author Eric Petersen
 *  @since ADK 1.0
 */
public class Event
{
	/**
	 *  The data that has changed as described by the action
	 */
	protected DataObjectInputStream fData;

	/**
	 *  Identifies the type of SIF Data Object contained in the event
	 */
	protected ElementDef fObjType;

	/**
	 *  The action
	 */
	protected String fAction;

	/**
	 *  The zone from which this event originated
	 */
	protected Zone fZone;
	
	/**
	 * The SIF Contexts to which this event applies 
	 */
	private SIFContext[] fContexts = new SIFContext[] { SIFContext.DEFAULT };

	/**
	 *  Constructs an Event object to encapsulate an inbound SIF Event. This form
	 * 	of constructor is called internally by the ADK when it receives a SIF_Event
	 * 	message.<p>
	 *
	 *  @param data A DataObjectInputStream that returns SIFDataObjects, all of
	 *      which must be of the same class type
	 *  @param action Describes how the data has changed. One of the
	 *      following flags: <code>ADD</code>, <code>CHANGE</code>, or <code>DELETE</code>
	 *  @param objectType An ElementDef constant from the {@linkplain com.edustructures.sifworks.SIFDTD} class that
	 *      identifies the type of SIF Data Object contained in the event
	 * 	@see openadk.library.EventAction
	 */
    public Event( DataObjectInputStream data, EventAction action, ElementDef objectType )
	{
		fData = data;
		fObjType = objectType;

		switch( action )
		{
			case ADD:
				fAction="Add"; break;
			case CHANGE:
				fAction="Change"; break;
			case DELETE:
				fAction="Delete"; break;
			default:
				throw new IllegalArgumentException( "Event action code " + action + " is not valid" );
		}
    }

	/**
	 *  Constructs an Event object to encapsulate an inbound SIF Event. This form
	 * 	of constructor is called internally by the ADK when it receives a SIF_Event
	 * 	message.<p>
	 *
	 *  @param data A DataObjectInputStream that returns SIFDataObjects, all of
	 *      which must be of the same class type
	 *  @param action Describes how the data has changed
	 *  @param objectType An ElementDef constant from the {@linkplain com.edustructures.sifworks.SIFDTD} class that
	 *      identifies the type of SIF Data Object contained in the event
	 */
    public Event( DataObjectInputStream data, String action, ElementDef objectType )
	{
		fData = data;
		fObjType = objectType;
		fAction = action;
	}

	/**
	 *  Constructs an Event object to encapsulate an outbound SIF Event, which
	 * 	describes one or more SIF Data Objects that have been added, changed, or
	 * 	deleted by the local application. This form of constructor is called by 
	 * 	agents when reporting a SIF Event message to a zone.<p>
	 *
	 *  @param data A SIFDataObject
	 *  @param action Describes how the data has changed. One of the
	 *      following flags: <code>ADD</code>, <code>CHANGE</code>, or <code>DELETE</code>
	 * 	@see openadk.library.EventAction
	 * 	@see openadk.library.Zone#reportEvent(Event)
	 */
    public Event( SIFDataObject data, EventAction action )
	{
		try {
			fData = DataObjectInputStreamImpl.newInstance();
			if( data != null )
		    		((DataObjectInputStreamImpl)fData).setData( new SIFDataObject[] { data } );
		} catch( ADKException adke ) {
			throw new RuntimeException( adke.toString() );
		}

		fObjType = data != null ? data.getElementDef() : null;

		switch( action )
		{
			case ADD:
				fAction="Add"; break;
			case CHANGE:
				fAction="Change"; break;
			case DELETE:
				fAction="Delete"; break;
			default:
				throw new IllegalArgumentException( "Event action code " + action + " is not valid" );
		}
    }
    
    /**
     * Constructs an Event object to encapsulate an outbound SIF Event, which
	 * 	describes one or more SIF Data Objects that have been added, changed, or
	 * 	deleted by the local application. This form of constructor is called by 
	 * 	agents when reporting a SIF Event message to a zone.<p>
     * 
     * @param data
     * @param action
     * @param contexts
     */
    public Event( SIFDataObject data, EventAction action, SIFContext... contexts )
	{
    	this( data, action );
    	setContexts( contexts );
	}

	/**
	 *  Constructs an Event object to encapsulate an outbound SIF Event, which
	 * 	describes one or more SIF Data Objects that have been added, changed, or
	 * 	deleted by the local application. This form of constructor is called by 
	 * 	agents when reporting a SIF Event message to a zone.<p>
	 *
	 *  @param data An array of SIFDataObjects, all of which must be of the same class type
	 *  @param action Describes how the data has changed: in SIF, this string should
	 * 		be "Add", "Change", or "Delete"
	 * 
	 * 	@see openadk.library.Zone#reportEvent(Event)
	 */
    public Event( SIFDataObject[] data, String action )
	{
		try {
			fData = DataObjectInputStreamImpl.newInstance();
			if( data != null )
		    		((DataObjectInputStreamImpl)fData).setData( data );
		} catch( ADKException adke ) {
			throw new RuntimeException( adke.toString() );
		}

		fObjType = data != null && data.length > 0 && data[0] != null ? data[0].getElementDef() : null;

		fAction = action;
	}

	/**
	 *  Gets the SIF Data Objects in the Event payload<p>
	 *  @return An input stream from which the agent can read the individual
	 * 		SIFDataObjects contained in the event
	 */
	public DataObjectInputStream getData()
	{
		return fData;
	}

	/**
	 *  Identifies the type of SIF Data Object contained in the Event payload
	 *  @return An ElementDef constant from the {@linkplain com.edustructures.sifworks.SIFDTD} class that identifies the
	 *      type of SIF Data Object contained in the event. All objects in an Event
	 * 		must be of the same type.
	 */
	public ElementDef getObjectType()
	{
		return fObjType;
	}

	/**
	 *  Gets the action code identifying how the data in the Event payload has changed<p>
	 *
	 *  @return One of the action codes defined by this class. <code>UNDEFINED</code>
	 *      is returned if the ADK could not parse the value of the <code>@Action</code> 
	 * 		attribute specified in the SIF_Event message (call {@link #getActionString}
	 * 		to inspect the actual Action attribute value)
	 *
	 *  @see EventAction
	 *  @see #getActionString
	 */
	public EventAction getAction()
	{
		if( fAction.equalsIgnoreCase("Add") )
			return EventAction.ADD;
		if( fAction.equalsIgnoreCase("Change") )
			return EventAction.CHANGE;
		if( fAction.equalsIgnoreCase("Delete") )
			return EventAction.DELETE;

		return EventAction.UNDEFINED;
	}

	/**
	 *  Gets the action string that was assigned to the <code>SIF_Event/@Action</code> attribute<p>
	 *  @return An action string (e.g. "Add", "Change", or "Delete")
	 *  @see #getAction
	 */
	public String getActionString()
	{
		return fAction;
	}

	/**
	 *  Called internally by the ADK to track the zone from which this event 
	 * 	originated. Note: Calling this method for outbound Events created by an agent 
	 * 	has no effect.
	 * 	@param zone The Zone the SIF_Event message was received from
	 */
	public void setZone( Zone zone )
	{
		fZone = zone;
	}

	/**
	 *  Gets the Zone from which an inbound event originated.
	 * 	@return The Zone the SIF_Event message was received from
	 */
	public Zone getZone()
	{
		return fZone;
	}

	/**
	 * Sets the SIF Contexts to which this event applies. If not set, the event
	 * applies to the default SIF Context
	 * @param contexts One or more contexts to which this event applies
	 * @see SIFContext#DEFAULT 
	 */
	public void setContexts(SIFContext... contexts) {
		if( contexts == null || contexts.length == 0 )
		{
			throw new IllegalArgumentException( 
					"Event must apply to one or more SIF Contexsts. SIFContext cannot be null" );
		}
		this.fContexts = contexts;
	}

	/**
	 * Gets the SIF Contexts that this event applies to. If the context has not been set,
	 * it defaults to the SIF Default context
	 * @return An array of SIF contexts to which this event applies
	 * @see SIFContext#DEFAULT
	 */
	public SIFContext[] getContexts() {
		return fContexts;
	}
}
