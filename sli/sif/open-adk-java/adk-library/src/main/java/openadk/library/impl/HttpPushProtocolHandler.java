//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.ADKParsingException;
import openadk.library.ADKTransportException;
import openadk.library.MessagingListener;
import openadk.library.SIFErrorCategory;
import openadk.library.SIFErrorCodes;
import openadk.library.SIFException;
import openadk.library.SIFMessagePayload;
import openadk.library.SIFParser;
import openadk.library.SIFVersion;
import openadk.library.SIFWriter;
import openadk.library.Zone;
import openadk.library.infra.SIF_Ack;
import openadk.library.infra.SIF_Data;
import openadk.library.infra.SIF_Event;
import openadk.library.infra.SIF_Header;
import openadk.library.infra.SIF_Protocol;
import openadk.library.infra.SIF_Status;
import openadk.library.tools.HTTPUtil;
import openadk.util.GUIDGenerator;

import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpFields;
import org.mortbay.http.HttpHandler;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.http.HttpServer;


/**
 * @author Andy Elmhorst
 * @version ADK 2.1
 *
 */
public class HttpPushProtocolHandler extends BaseHttpProtocolHandler implements HttpHandler
{
	/**
	 *
	 */
	private static final long serialVersionUID = 96862608535041043L;
	private HttpContext fHttpCtx;
	private HttpServer fServer;
	private static final int URI_OFFSET = "/zone/".length();
	public static AnonymousHttpHandler sAnonymousHandler;

	HttpPushProtocolHandler( HttpTransport transport, HttpServer server )
	{
		super( transport );
		fServer = server;
	}


	@Override
	public void close( ZoneImpl zone )
	{

		if( fServer != null && fHttpCtx != null ) {
			try {
				fHttpCtx.stop();
			} catch( InterruptedException ignored ) {
				zone.log.warn( "Error shutting down context: " + ignored, ignored );
			}
			fServer.removeContext( fHttpCtx );
		}
	}

	@Override
	public synchronized void start() throws ADKException
	{
		try
		{
			//
			//  For Push mode, establish an HttpContext for this zone.
			//  Messages received by the ZIS server will be routed to us.
			//
			String ctx = "/zone/" + fZone.getZoneId() + "/";
			fHttpCtx = fServer.getContext( ctx );

			HttpHandler[] handlers = fHttpCtx.getHandlers();
			boolean alreadyhandled = false;
			if( handlers != null ) {
				for( int i = 0; i < handlers.length; i++ ) {
					if( handlers[i] == this ) {
						alreadyhandled = true;
						break;
					}
				}
			}

			fHttpCtx.setClassLoader( this.getClass().getClassLoader() );

			if( !alreadyhandled ){
				fHttpCtx.addHandler(this);
			}

			if( !fHttpCtx.isStarted() ){
				fHttpCtx.start();
			}

			if( false )
			{
				//  Also establish a catch-all handler for "/" in case the ZIS
				//  disregards our URL path of "/zone/{zoneId}/". Any traffic
				//  received on this handler will be routed as follows: if the
				//  agent is connected to only one zone in Push mode, the
				//  traffic is assumed to have come from that zone. If the
				//  agent is connected to more than one zone in Push mode, the
				//  traffic is disregarded (i.e. no Ack is returned to the ZIS,
				//  which means the message will remain in the agent's queue.)
				//  If the agent is connected to a ZIS that behaves this way
				//  it must use Pull mode.
				//
				if( sAnonymousHandler == null )
				{
					sAnonymousHandler = new AnonymousHttpHandler();
					HttpContext context = fServer.addContext("/");
					context.addHandler(sAnonymousHandler);
					fServer.addContext(context);
					context.start();
				}
			}
		}
		catch( Exception e )
		{
			throw new ADKException("HttpProtocolHandler could not establish HttpContext: "+e,fZone);
		}

	}


		/**
		 *  Process an http request from Jetty.
		 */
		public void handle( String pathInContext,
							String pathParams,
							HttpRequest request,
							HttpResponse response )
			throws HttpException
		{
			request.setHandled(true);

			if( ( ADK.debug & ADK.DBG_MESSAGING ) != 0 )
				fZone.log.debug("Received push message from "+request.getRemoteAddr()+" ("+request.getScheme()+")" );

			SIF_Ack ack = null;
			SIFMessagePayload parsed = null;

			//  Check request length and type
			if( request.getContentLength() < 1 )
				throw new HttpException( HttpResponse.__400_Bad_Request );
/*			if( !request.getContentType().equalsIgnoreCase( SIFIOFormatter.CONTENT_TYPE ) )
				throw new HttpException( HttpResponse.__415_Unsupported_Media_Type );
*/			
			String contentTest = request.getContentType().toLowerCase();
			if ( (!contentTest.contains(SIFIOFormatter.CONTENT_TYPE_BASE)) ||
					(!contentTest.contains(SIFIOFormatter.CONTENT_TYPE_UTF8)) )
				throw new HttpException( HttpResponse.__415_Unsupported_Media_Type );

			//  Read raw content
			StringBuffer xml = readPush(request,response);
			if( xml == null ){
				// Shouldn't happen
				xml = new StringBuffer();
			}

			if( ( ADK.debug & ADK.DBG_MESSAGE_CONTENT ) != 0 ){
				fZone.log.debug("Received "+xml.length()+" bytes:\r\n"+xml );
			}

			Throwable parseEx = null;
			boolean reparse = false;
			boolean cancelled = false;
			SIFParser parser = createParser();
			int reparsed = 0;

			do
			{
				try
				{
					parseEx = null;

					//  Parse content
			    	parsed = (SIFMessagePayload)parser.parse(xml.toString(),fZone);
					parsed.LogRecv(fZone.log);
				}
				catch( ADKParsingException adke )
				{
					parseEx = adke;
				}
				catch( Throwable ex )
				{
					parseEx = ex;
				}

				//
				//	Notify listeners...
				//
				//	If we're asked to reparse the message, do so but do not notify
				//	listeners the second time around.
				//
				if( reparsed == 0 )
				{
					List<MessagingListener> msgList = MessageDispatcher.getMessagingListeners( fZone );
					for( MessagingListener ml : msgList  )
					{
						try
						{
							byte pload = ADK.DTD().getElementType( parsed.getElementDef().name() );
							byte code = ml.onMessageReceived( pload, xml );
							switch( code )
							{
								case MessagingListener.RX_DISCARD:
									cancelled = true;
									break;

								case MessagingListener.RX_REPARSE:
									reparse = true;
									break;
							}
						}
						catch( ADKException adke )
						{
							parseEx = adke;
						}
					}
				}

				if( cancelled )
					return;

				reparsed++;
			}
			while( reparse );

			if( parseEx != null )
			{
				//  TODO: Handle the case where SIF_OriginalSourceId and SIF_OriginalMsgId
				//  are not available because parsing failed. See SIFInfra
				//  Resolution #157.
				if( parseEx instanceof SIFException && parsed != null )
				{
					//  Specific SIF error already provided to us by SIFParser
					ack = parsed.ackError( (SIFException)parseEx );
				}
				else{
					String errorMessage = null;
					if( parseEx instanceof ADKException )
					{
						errorMessage = parseEx.getMessage();
					} else {
						// Unchecked Throwable
						errorMessage = "Could not parse message";
					}

					if( parsed == null )
					{
						SIFException sifError = null;
						if( parseEx instanceof SIFException ){
							sifError = (SIFException) parseEx;

						}else {
							sifError = new SIFException(SIFErrorCategory.XML_VALIDATION,
									SIFErrorCodes.XML_GENERIC_ERROR_1,
									"Could not parse message" , parseEx.toString(), fZone );
						}

						ack = SIFPrimitives.ackError(
								xml.toString(),
								sifError,
								fZone );
					}
					else
					{
						ack = parsed.ackError(
							SIFErrorCategory.GENERIC,
							SIFErrorCodes.GENERIC_GENERIC_ERROR_1,
				    		errorMessage,
					    	parseEx.toString() );
					}

				}



				if( ( ADK.debug & ADK.DBG_MESSAGING ) != 0 )
					fZone.log.warn("Failed to parse push message from zone \"" + fZone + "\": " + parseEx );

				if( ack != null )
				{
					//  Ack messages in the same version of SIF as the original message
					if( parsed != null ){
						ack.setSIFVersion( parsed.getSIFVersion() );
					}
					ackPush(ack,request,response);
				}
				else
				{
					//  If we couldn't build a SIF_Ack, returning an HTTP 500 is
					//  probably the best we can do to let the server know that
					//  we didn't get the message. Note this should cause the ZIS
					//  to resend the message, which could result in a deadlock
					//  condition. The administrator would need to manually remove
					//  the offending message from the agent's queue.

					if( ( ADK.debug & ADK.DBG_MESSAGING ) != 0 )
						fZone.log.debug("Could not generate SIF_Ack for failed push message (returning HTTP/1.1 500)");
					throw new HttpException(
						HttpResponse.__500_Internal_Server_Error );
				}

				return;
			}

			//  Check SourceId to see if it matches this agent's SourceId
			String destId = parsed.getDestinationId();
			if( destId != null && !destId.equals( fZone.getAgent().getId() ) )
			{
				fZone.log.warn("Received push message for DestinationId \""+destId+"\", but agent is registered as \""+fZone.getAgent().getId()+"\"" );

				ack = parsed.ackError(
					SIFErrorCategory.TRANSPORT,
					SIFErrorCodes.WIRE_GENERIC_ERROR_1,
					"Message not intended for this agent (SourceId of agent does not match DestinationId of message)",
					"Message intended for \"" + destId + "\" but this agent is registered as \"" + fZone.getAgent().getId() + "\"" );

				ackPush(ack,request,response);

				return;
			}

			//
			//  Check Zone ID.
			//  Extract the zone ID from the path. The path will be the context
			//  string "/zone/{zondId}/" and the pathInContext should be "/" (the
			//  trailing slash) unless the ZIS specified additional information
			//  on URI path for some reason. Extract the zone name
			//  from that.
			//
			String zone = request.getPath();
			zone = zone.substring( URI_OFFSET, zone.length() - pathInContext.length() );
			if( !zone.equals( fZone.getZoneId() ) )
			{
				fZone.log.warn("Received push message from zone \""+zone+"\", but agent is expecting messages from zone \""+fZone.getZoneId() );

				ack = parsed.ackError(
					SIFErrorCategory.SYSTEM,
					SIFErrorCodes.SYS_GENERIC_ERROR_1,
					"Unexpected Zone",
					"Agent not expecting messages from zone: "+zone);

				ackPush(ack,request,response);

				return;
			}

			//  Convert content to SIF message object and dispatch it
			ack = processPush(parsed);

			//  Send SIF_Ack reply
			ackPush(ack,request,response);
		}

		private SIF_Ack processPush( SIFMessagePayload parsed )
			throws HttpException
		{
			try
			{
				//  Dispatch. When the result is an Integer it is an ack code to
				//  return; otherwise it is ack data to return and the code is assumed
				//  to be 1 for an immediate acknowledgement.
				int ackStatus = fZone.getFDispatcher().dispatch(parsed);

				//  Ask the original message to generate a SIF_Ack for itself
		    	return parsed.ackStatus(ackStatus);
			}
			catch( SIFException se )
			{
				return parsed.ackError( se );
			}
			catch( ADKException adke )
			{
				return parsed.ackError(
					SIFErrorCategory.GENERIC,
					SIFErrorCodes.GENERIC_GENERIC_ERROR_1,
					adke.getMessage() );
			}
			catch( Throwable thr )
			{
				if( ( ADK.debug & ADK.DBG_MESSAGING ) != 0 )
					fZone.log.debug("Uncaught exception dispatching push message: "+thr);

				return parsed.ackError(
						SIFErrorCategory.GENERIC,
					SIFErrorCodes.GENERIC_GENERIC_ERROR_1,
					"An unexpected error has occurred",
					thr.toString() );
			}
		}

		private StringBuffer readPush( HttpRequest request, HttpResponse response )
			throws HttpException
		{
			Reader in = null;
			int expected = request.getContentLength(),
				totalRead = 0;

			try
			{
				// NOTE: Keep this a StringBuffer for now because it is used externally
				StringBuffer strbuf = new StringBuffer();
				char buf[] = new char[expected < 1024 ? expected : 1024];

				String contentEncoding = request.getField(HttpFields.__ContentEncoding);
				if (contentEncoding != null && "gzip".equalsIgnoreCase(contentEncoding.trim())) {
					in = SIFIOFormatter.createInputReader(new GZIPInputStream(request.getInputStream()));
				} else {
					in = SIFIOFormatter.createInputReader( request.getInputStream() );
				}
				
				int charsRead = 0;
				while ((charsRead = in.read(buf)) > -1) {
					if (charsRead > 0) {
						strbuf.append(buf, 0, charsRead);
						totalRead += charsRead;
					}
				}

				return strbuf;
			}
			catch( Throwable thr )
			{
				System.out.println("HttpProtocolHandler failed to read push message (approximately "+
					totalRead+" of "+expected+" bytes read; zone="+fZone.getZoneId()+"): "+thr);
				throw new HttpException(
					HttpResponse.__500_Internal_Server_Error );
			}
			finally
			{
				if( in != null ) {
					try {
						in.close();
					} catch( IOException ignored )
					{
						fZone.log.warn( ignored.getMessage(), ignored );
					};
				}
			}
		}

		private void ackPush( SIF_Ack ack, HttpRequest request, HttpResponse response )
			throws HttpException
		{
			try
			{
				//  Set SIF_Ack / SIF_Header fields
				SIF_Header hdr = ack.getHeader();
				hdr.setSIF_Timestamp( Calendar.getInstance() );
				hdr.setSIF_MsgId(GUIDGenerator.makeGUID());
				hdr.setSIF_SourceId(fZone.getAgent().getId());

				ack.LogSend(fZone.log);

			    //  Convert message to a string
				ByteArrayOutputStream raw = new ByteArrayOutputStream();
				SIFWriter out = new SIFWriter( raw, fZone );
				out.write(ack);
				out.close();
				raw.close();
				
				byte[] realData = raw.toByteArray();
				boolean compressed = false;
				raw.reset();
				
				if (fZone.getProperties().getCompressionThreshold() > -1 && realData.length > fZone.getProperties().getCompressionThreshold()) {
					String acceptEncoding = request.getField("Accept-Encoding");
					if (acceptEncoding != null) {
						List<String> tokens = HTTPUtil.derivePreferredCodingFrom(acceptEncoding);
						if (tokens.contains("gzip")) {
							GZIPOutputStream gzos = new GZIPOutputStream(raw);
							gzos.write(realData);
							gzos.flush();
							gzos.finish();
							realData = raw.toByteArray();
							compressed = true;
						}
					}
				}

			    //  Send reply
				response.setContentType( SIFIOFormatter.CONTENT_TYPE );
				response.setContentLength(realData.length);
				if (compressed) {
					response.setField("Content-Encoding", "gzip");
				}
				response.getOutputStream().write(realData);
				response.getOutputStream().flush();
		//		reply.close();
			}
			catch( Throwable thr )
			{
				System.out.println("HttpProtocolHandler failed to send SIF_Ack for pushed message (zone="+
					fZone.getZoneId()+"): "+thr);
				throw new HttpException(
					HttpResponse.__500_Internal_Server_Error );
			}
		}



//////////////////////////////////////////////////////////////////////////////	//
//	  HttpHandler
	//

		public void initialize( HttpContext context ) {
			fHttpCtx = context;
		}
		public HttpContext getHttpContext() {
			return fHttpCtx;
		}

		public synchronized boolean isStarted() {
			return true;
		}


		@Override
		public void shutdown() {
			if( fServer != null && fHttpCtx != null ){
				try
				{
					fHttpCtx.stop();
				}
				catch( InterruptedException iex ){
					fZone.log.warn( "Unable to stop HttpContext: " + iex, iex );
				}
				fServer.removeContext( fHttpCtx );
				fHttpCtx = null;
			}
		}


		public void stop() throws InterruptedException {
			shutdown();
		}


		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.impl.IProtocolHandler#isActive(com.edustructures.sifworks.impl.ZoneImpl)
		 */
		public boolean isActive(ZoneImpl zone)
			throws ADKTransportException
		{
			return fTransport.isActive( zone ) && fHttpCtx != null && fHttpCtx.isStarted();
		}


		/**
		 * Creates a SIF_Protocol object for a SIF_Register message.
		 * <p>
		 *
		 * @param zone
		 *            The zone the SIF_Register message will be sent to
		 * @return A SIF_Protocol object to be included in the SIF_Register message,
		 *         or null if the zone is not operating in Push mode
		 */
		public SIF_Protocol makeSIF_Protocol(Zone zone, SIFVersion version)
			throws ADKTransportException
		{
			SIF_Protocol proto = new SIF_Protocol();
			fTransport.configureSIF_Protocol( proto, zone, version );
			return proto;
		}

}
