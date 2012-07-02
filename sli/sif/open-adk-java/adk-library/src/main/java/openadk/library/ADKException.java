//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.util.Vector;
import java.io.*;

import openadk.library.Zone;
import openadk.library.impl.ZoneImpl;

import org.apache.log4j.Category;

/**
 *  The base class for all exception classes defined by the ADK.<p>
 *
 *  ADKExceptions have the following characteristics:<p>
 *
 *  <ul>
 *      <li>
 *          An exception may contain one or more child exceptions. All ADK
 *          methods that operate on multiple zones or topics take a fail-late
 *          approach where the method continues processing even when an error
 *          occurs. Only after all zones and topics have been enumerated is a
 *          final exception thrown. It may contain multiple child exceptions
 *          collected during the processing.
 *      </li>
 *      <li>
 *          The caller can obtain a reference to the Zone in which the exception
 *          occurred.
 *      </li>
 *  </ul>
 *  <p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class ADKException extends Exception implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = Element.CURRENT_SERIALIZE_VERSION;

	/**
	 *  When this flag is enabled, the ADK will retain the message associated
	 *  with the exception rather than removing it from the agent's queue, if
	 *  possible.
	 *  @see #setRetry(boolean)
	 */
	private static final int FLG_RETRY = 0x00000001;

	/**
	 *  The Zone associated with this exception. Because the Zone interface does
	 *  not support RMI, this is a transient data member. Rather than calling
	 *  <code>getZone</code> to retrieve the zone associated with an ADKException,
	 *  RMI-based clients should use the <code>getZoneId</code> method
	 *  instead.
	 */
	private transient Zone fZone;

	/**
	 *  The ID of the zone associated with this exception
	 */
	private String fZoneId;

	/**
	 *  The nested child exceptions
	 */
	private Vector<Throwable> fChildren;

	/**
	 *  Optional exception flags that may influence how the ADK handles this
	 *  exception when it is caught within the class framework
	 */
	private int fFlags;

	/**
	 *  Constructs an exception with a detailed message that occurs in the
	 *  context of a zone<p>
	 *  @param msg A message describing the exception
	 *  @param zone The zone associated with the exception
	 */
	public ADKException( String msg, Zone zone )
	{
		super(msg);

		fZone = zone;
		fZoneId = zone == null ? null : zone.getZoneId();
	}
	
	/**
	 *  Constructs an exception with a detailed message that occurs in the
	 *  context of a zone<p>
	 *  @param msg A message describing the exception
	 *  @param zone The zone associated with the exception
	 *  @param src The source exception
	 */
	public ADKException( String msg, Zone zone, Throwable src )
	{
		super(msg, src);

		fZone = zone;
		fZoneId = zone == null ? null : zone.getZoneId();
	}

	/**
	 *  Gets the zone associated with this exception.<p>
	 *
	 *  <b>Note:</b> The ADK's <i>Zone</i> interface does not support Java RMI.
	 *  Therefore, this method will return a <code>null</code> value when called
	 *  by an RMI-based client on a marshalled ADKException object. RMI clients
	 *  should instead use the <code>getZoneId</code> method to learn the ID of
	 *  the zone passed to the constructor.
	 *  <p>
	 *
	 *  @return The Zone associated with the exception
	 *
	 *  @see #getZoneId
	 */
	public Zone getZone()
	{
		return fZone;
	}

	/**
	 *  Gets the ID of the zone associated with this exception.<p>
	 *  @return the ID of the Zone passed to the constructor
	 *  @see #getZone
	 */
	public String getZoneId()
	{
		return fZoneId;
	}

	/**
	 *  Determines if this exception has nested child exceptions
	 *  @return true if this exception has nested child exceptions
	 */
	public boolean hasChildren() {
		return fChildren != null;
	}

	/**
	 *  Adds a child exception
	 * @param thr A new child exception
	 */
	public void add( Throwable thr ) {
		if( fChildren == null )
			fChildren = new Vector<Throwable>();
		fChildren.addElement(thr);
	}

	/**
	 *  Gets the child exceptions, if any
	 * @return All child excpeptions. If no child exceptions are defined, 
	 * 	an emtpy array is returned
	 */
	public 	Throwable[] getChildren() {
		Throwable[] arr = new Throwable[ fChildren == null ? 0 : fChildren.size() ];
		if( fChildren != null )
			fChildren.copyInto(arr);
		return arr;
	}

	/**
	 *  Determines if the ADK should attempt to retry the operation associated
	 *  with this exception.
	 *  @param retry When true, the exception is flagged for retry. The method
	 *      that catches the exception should attempt to retry the operation in
	 *      progress.
	 */
	public void setRetry( boolean retry ) {
		if( retry ) {
			fFlags |= FLG_RETRY;
		} else {
			fFlags &= ~FLG_RETRY;
		}
	}

	/**
	 *  Determines if the ADK should attempt to retry the operation associated
	 *  with this exception.
	 *  @return True if the exception is flagged for retry (the default is
	 *      false). The method that catches the exception should attempt to
	 *      retry the operation in progress.
	 */
	public boolean getRetry() {
		return ( fFlags & FLG_RETRY ) != 0 ;
	}

	/**
	 *  Determines if this exception contains any SIFException children.
	 *  SIFException encapsulates SIF errors returned to the agent in SIF_Ack
	 *  messages.
	 *
	 *  @return true if this exception has at least one nested SIFException
	 */
	public boolean hasSIFExceptions()
	{
		if( fChildren != null ) {
			for( int i = 0; i < fChildren.size(); i++ ) {
				if( fChildren.elementAt(i) instanceof SIFException )
					return true;
			}
		}

		return false;
	}

	/**
	 *  Gets all child SIFExceptions, if any
	 *  @return An array of SIFExceptions
	 */
	public SIFException[] getSIFExceptions()
	{
		Vector<Throwable> v = new Vector<Throwable>();

		if( fChildren != null ) {
			for( Throwable t : v ) {
				if( t instanceof SIFException )
					v.addElement( t );
			}
		}

		SIFException[] arr = new SIFException[ v.size() ];
		v.copyInto(arr);
		return arr;
	}

	/**
	 *  Determines if this exception contains at least one nested SIFException
	 *  with the specified error category.<p>
	 *  @deprecated Please use the overload of this method that accepts a SIFErrorCategory
	 *  @param category The error category to search for
	 *  @return true if this exception has nested SIFExceptions and at least one
	 *      of those has the specified error category.
	 */
	public boolean hasSIFError( int category )
	{
		if( this instanceof SIFException )
			return ((SIFException)this).hasErrorCategory( category );

		return _recurseError( this, SIFErrorCategory.lookup( category ) );
	}
	
	/**
	 *  Determines if this exception contains at least one nested SIFException
	 *  with the specified error category.<p>
	 *  @param category The error category to search for
	 *  @return true if this exception has nested SIFExceptions and at least one
	 *      of those has the specified error category.
	 */
	public boolean hasSIFError( SIFErrorCategory category )
	{
		if( this instanceof SIFException )
			return ((SIFException)this).hasErrorCategory( category );

		return _recurseError( this, category );
	}
	

	/**
	 *  Determines if this exception contains at least one nested SIFException
	 *  with the specified error category and code.<p>
	 *  @deprecated Please use the overload of this method that accepts a SIFErrorCategory
	 *  	as the first parameter
	 *  @param category The error category to search for
	 *  @param code The error code to search for
	 *  @return true if this exception has nested SIFExceptions and at least one
	 *      of those has the specified error category and code
	 */
	public boolean hasSIFError( int category, int code )
	{
		if( this instanceof SIFException ){
			return ((SIFException)this).hasError( category, code );
		}

		return _recurseError( this, SIFErrorCategory.lookup( category ), code );
	}
	
	/**
	 *  Determines if this exception contains at least one nested SIFException
	 *  with the specified error category and code.<p>
	 *  @param category The error category to search for
	 *  @param code The error code to search for
	 *  @return true if this exception has nested SIFExceptions and at least one
	 *      of those has the specified error category and code
	 */
	public boolean hasSIFError( SIFErrorCategory category, int code )
	{
		if( this instanceof SIFException )
			return ((SIFException)this).hasError( category,code );

		return _recurseError( this, category, code );
	}

	private boolean _recurseError( ADKException parent, SIFErrorCategory category, int code )
	{
		if( parent.fChildren != null )
		{
			Throwable ch = null;

			for( int i = 0; i < fChildren.size(); i++ )
			{
				ch = fChildren.elementAt(i);

				if( ch instanceof SIFException ) {
					if( ((SIFException)ch).hasError( category, code ) )
						return true;
				}
				else
				if( ch instanceof ADKException ) {
					if( _recurseError( (ADKException)ch, category, code ) )
	    				return true;
				}
			}
		}

		return false;
	}

	private boolean _recurseError( ADKException parent, SIFErrorCategory category )
	{
		if( parent.fChildren != null )
		{
			Throwable ch = null;

			for( int i = 0; i < fChildren.size(); i++ )
			{
				ch = fChildren.elementAt(i);

				if( ch instanceof SIFException ) {
					if( ((SIFException)ch).hasErrorCategory( category ) )
						return true;
				}
				else
				if( ch instanceof ADKException ) {
					if( _recurseError( (ADKException)ch, category ) )
	    				return true;
				}
			}
		}

		return false;
	}

	/**
	 *  Returns a string representation of this exception and all child
	 *  exceptions formatted for printing to System.out. Each exception is
	 *  on its own line.
	 * @return A string representation of this exception and all child exceptions
	 */
	public String toString()
	{
		return toString(0,true);
	}

	/**
	 *  Returns a string representation of this exception and all child
	 *  exceptions formatted for printing to System.out. Each exception is
	 *  on its own line.
	 * @param indent The amount of indentation to apply to the string
	 * @param includeChildren True if all children should be returned
	 * @return A string representation of this exception
	 */
	public String toString( int indent, boolean includeChildren )
	{
		StringBuffer b = new StringBuffer();

		for( int i = 0; i < indent; i++ )
			b.append("  ");

		//  Print detailed message if any
		if( indent != 0 )
			b.append( "Zone " + ( fZone == null ? "(Unknown)" : fZone.getZoneId() ) + ": ");
		b.append( getMessage() );

		//  Print nested exceptions if any
		if( fChildren != null && includeChildren )
		{
			b.append(":");
			b.append("\r\n");
			int cnt = fChildren.size();
			for( int i = 0; i < cnt; i++ )
			{
				Throwable innerX = fChildren.elementAt(i);
				if( innerX instanceof ADKException )
					b.append( ((ADKException)innerX).toString(indent+1,true) );
				else
				{
					StringBuffer msg = new StringBuffer();
					for( int k = 0; k < (indent+1); k++ )
						msg.append("  ");
					msg.append(innerX.toString());
					b.append(msg.toString());
				}

				b.append("\r\n");
			}
		}

		return b.toString();
	}

	/**
	 *  Write this exception and all of its nested exceptions to Log4j. For
	 *  any exception that is not associated with a zone, the supplied default
	 *  Category will be used. Otherwise the Category of the zone is used.
	 * @param def The category to log to
	 */
	public void log( Category def )
	{
		log(def,0);
	}

	/**
	 * Logs the exception to the Category, using the "error" logging level
	 * @param def The Category to log to
	 * @param indent The amount of indentation to apply
	 */
	public void log( Category def, int indent )
	{
		Category target = fZone == null ? def : ((ZoneImpl)fZone).log;
		if( target == null ){
			return;
		}

		target.error(toString(indent,false), this);

		if( fChildren != null )
		{
			int cnt = fChildren.size();
			for( int i = 0; i < cnt; i++ )
			{
				Throwable innerX = fChildren.elementAt(i);
				if( innerX instanceof ADKException )
					((ADKException)innerX).log(def,indent+1);
				else
				{
					StringBuffer msg = new StringBuffer();
					for( int k = 0; k < (indent+1); k++ )
						msg.append("  ");
					msg.append( innerX.toString() );
					target.error( msg.toString(), this );
				}
			}
		}
	}

}
