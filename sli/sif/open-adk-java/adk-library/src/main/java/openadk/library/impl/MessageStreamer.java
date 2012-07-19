//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.io.*;

/**
 *  A MessageStreamer is a Reader class used to assemble and stream a SIF_Message
 *  by concatenating an outer <i>envelope</i> with one or more inner <i>payloads</i>. 
 * 	The MessageStreamer can be passed to any method that accepts a Reader as an
 *  input source.<p>
 *
 *  MessageStreamer takes care of assembling the envelope and payload by reading
 *  from the Envelope Reader and Payload Reader passed to the constructor.
 *  Bytes are read from the Envelope Reader up until the named element is
 *  encountered (inclusive). Subsequent bytes are read from the Payload Reader(s)
 *  until the closing tag of the named element is encountered (also inclusive).
 *  Finally, the remaining bytes of the Envelope Reader are read.<p>
 *
 *  An instance of this class is typically passed to the IProtocolHandler.send
 *  method when sending an arbitrarily large SIF_Message.
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */

public class MessageStreamer extends Reader
{
	private int fPos = 0;
	private int fStage = 0;
	protected Reader fEnvelope;
	protected Reader[] fPayloads;
	protected int fPayloadIndex = -1;
	protected String fElement;
	protected boolean fReplaceMode = false;
	protected boolean fReady = true;

	/**
	 *  Constructor
	 *  @param envelope A Reader that provides the content of the <i>envelope</i>
	 *  @param payload An array of Readers that provide the content of the <i>payload</i>
	 *  @param element The element that signals the beginning of the payload.
	 *      The first element of this type will be considered the beginning of
	 *      the payload.
	 */
	public MessageStreamer( Reader envelope, Reader[] payloads, String element )
	{
		fEnvelope = envelope;
		fPayloads = payloads;
		fElement = element;
	}

	/**
	 *  Turns <i>replace</i> mode on or off. When this mode is enabled, the
	 *  element is replaced with the supplied payload instead of inserting the
	 *  payload as a child of the element.
	 */
	public void setReplaceMode( boolean enable )
	{
		fReplaceMode = enable;
	}

	public synchronized int read( char[] cbuf, int off, int len )
		throws IOException
	{
		/* In replace mode, the open tag is skipped in the payload,
		 * but the close tag of the payload is written out.  Then in
		 * stage 2, the close tag in the envelope is skipped.  This
		 * means there can only be one payload in replace mode.
		 */
		if ( fReplaceMode && fPayloads.length > 1 )
			throw new IllegalArgumentException( "In replace mode, MessageStreamer can only handle one payload." );
		
		if( len == 0 )
			return 0;

		int bytes = -1;

		switch( fStage )
		{
			case 0: /* Read initial portion of Envelope */
			{
				int start = -1;
				int pos = off;

				//  Read from the Envelope Reader, looking for <element> to signal
				//  advancement to the next stage
				do
				{
					cbuf[pos] = (char)fEnvelope.read();

					if( cbuf[pos] == '<' )
						start = pos; else
					if( cbuf[pos] == '>' ) {
						String cmp = new String(cbuf,start,(pos-start)+1);
						if( cmp.equals(fElement) ) {
							fStage++;
							return( pos-off ) + 1;
						}
					}

					pos++;
				}
				while( cbuf[pos-1] != -1 && pos < len );
			}
			break;

		    case 1: /* Read entire Payload */
		    	/*
		    	 * TT 919 - When in replace mode, we were not handling payloads over 
		    	 * 1024 bytes long correctly because we were skipping the element tag
		    	 * once for each 1024 bytes, rather than just once at the beginning
		    	 * of the payload.  (1024 comes from the len passed in.)  Changed this
		    	 * so that the fPayloadIndex starts at -1.  Then the advancement is the
		    	 * same more all payloads.  When the advancement is done, then we skip
		    	 * the element if necessary.  Note that the end tag is written out
		    	 * in stage 1.  In stage 2, the end tag is skipped.  This means that
		    	 * this code can really only accept one payload if in replace mode.  
		    	 */
		    	if ( fPayloadIndex >= 0 ) 
		    		bytes = fPayloads[ fPayloadIndex ].read(cbuf,off,len);
				if( bytes <= 0 /* == -1 */ ) 
				{
					//	Advance to the next Payload, or if there isn't one, to
					//	the next stage
					if( ++fPayloadIndex == fPayloads.length )
						fStage++;
					else if ( fReplaceMode )
						fPayloads[ fPayloadIndex ].skip(fElement.length());
						
					return 0;
				} 
				else
					return bytes;

			case 2: /* Read remaining portion of Envelope */
				if( fReplaceMode )
					fEnvelope.skip(fElement.length() + 2 /* 2 for the < and > */ );
				bytes = fEnvelope.read(cbuf,off,len);
				if( bytes == -1 ) {
					fStage++;
					fReady = false;
					return 0;
				} else
					return bytes;
		}

		return -1;
	}

	public synchronized void close()
		throws IOException
	{
		fEnvelope.close();
		for( int i = 0; i < fPayloads.length; i++ )
			fPayloads[i].close();
	}

	public synchronized boolean ready() {
		return fReady;
	}

}
