//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;


import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;

import openadk.library.*;
import openadk.library.infra.*;
import openadk.library.tools.HTTPUtil;

/**
 *  An protocol handler implementation for HTTP. Each zone that is registered
 *  with a ZIS using the HTTP or HTTPS protocol has an instance of this class
 *  as its protocol handler. It implements the HttpHandler interface to process
 *  SIF messages received by the agent's internal Jetty HTTP Server. When a
 *  message is received via that interface it is delegated to the zone's
 *  MessageDispatcher. HttpProtocolHandler also implements the IProtocolHandler
 *  interface so it can send outgoing messages received by the
 *  MessageDispatcher.
 *  <p>
 *
 *  An instance of this class runs in a separate thread only when the agent is
 *  registered with the ZIS in Pull mode. In this case it does not accept
 *  messages from the HttpHandler interface but instead periodically queries the
 *  ZIS for new messages waiting in the agent's queue. Messages are delegated to
 *  the MessageDispatcher for processing.
 *  <p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public abstract class BaseHttpProtocolHandler implements IProtocolHandler
{
	/**
	 *
	 */
	private static final long serialVersionUID = Element.CURRENT_SERIALIZE_VERSION;

	private String fHttpUserAgent;
	private String fHttpHost;
	protected ZoneImpl fZone;
	private SSLSocketFactory fSSLFactory;
	protected HttpTransport fTransport;
	private URL fURL;


	protected BaseHttpProtocolHandler( HttpTransport transport )
	{
		fTransport = transport;
	}


	public String getName() {
		return fZone.getAgent().getId()+"@"+fZone.getZoneId() + "." + this.getClass().getSimpleName();
	}

	/**
	 * Creates a new SIFParser to use for this protocol handler
	 * @return a newly-created SIFParser to use for this protocol handler
	 */
	protected SIFParser createParser()
	{
		try {
			return SIFParser.newInstance();
		} catch( ADKException adke ) {
			throw new openadk.util.InternalError( adke.toString() );
		}

	}

	/**
	 *  Initialize the protocol handler for a zone
	 */
	public void open( ZoneImpl zone )
		throws ADKException
	{
		fZone = zone;

		try
		{
			//  Ensure the ZIS URL is http/https
			fURL = fZone.getZoneUrl();
    		String check = fURL.getProtocol().toLowerCase();
	    	if( !check.equals("http") && !check.equals("https") )
		    	throw new ADKException("HttpProtocolHandler cannot handle URL: "+fZone.getZoneUrl(),fZone);

			//  Prepare headers later used to send messages
		    fHttpUserAgent = fZone.getAgent().getId() + " (ADK/" + ADK.getADKVersion() + ")";
	    	fHttpHost = fURL.getHost()+":"+fURL.getPort();
		}
		catch( Throwable thr )
		{
			throw new ADKException("HttpProtocolHandler could not parse URL \""+fZone.getZoneUrl()+"\": "+thr,fZone);
		}
	}

	/**
	 *  Close this ProtocolHandler for a zone
	 */
	public abstract void close( ZoneImpl zone );

	public abstract void start() throws ADKException;

	public abstract void shutdown();


	/**
	 *  Get an outbound connection to the ZIS
	 *  @return Either an HttpsURLConnection or an HttpURLConnection depending
	 *      on whether the associated transport protocol is secure or not
	 */
	@SuppressWarnings("unchecked")
	protected URLConnection getConnection()
		throws ADKTransportException
	{
		try
		{
			if( !fTransport.isSecure() )
			{
				//  Use a plain HTTP connection. The URLConnection class handles
				//  keep-alive internally so even though we ask for a new
				//  HttpURLConnection each call to this method, prior connections
				//  are cached and kept alive (at least this is my understanding).
				//
				HttpURLConnection conn = (HttpURLConnection)fURL.openConnection();
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				
				HttpProperties properties = (HttpProperties)fTransport.getProperties();
				
				HTTPUtil.setTimeoutsOnConnection( properties, conn );

				return conn;
			}
			else
			{
				HttpsProperties https = (HttpsProperties)fTransport.getProperties();

				//  Use an HTTPS Connection. If the SSLFactory hasn't been setup
				//  yet, do this first and keep the factory object around so
				//  subsequent calls can use it.
				//
				if( fSSLFactory == null )
				{
					SSLContext ctx;
					KeyManagerFactory kmf;
					KeyStore ks;
					String ksPwd = fTransport.getKeyStorePassword();
					char[] passphrase = ksPwd.toCharArray();

					ctx = SSLContext.getInstance("TLS");
					kmf = KeyManagerFactory.getInstance("SunX509");
					ks = KeyStore.getInstance("JKS");

					//  If no keystore was specified in the HttpTransport
					//  configuration, assume the default .keystore file in the
					//  user's home directory
					String ksF = fTransport.getKeyStore();
					if( ksF == null )
						ksF = System.getProperties().getProperty("user.home") + File.separator + ".keystore";
					File ksFile = new File(ksF);
					if( !ksFile.exists() )
						throw new ADKTransportException( "Keystore file not found: " + ksFile.getAbsolutePath(), fZone );

					if( ks == null ) {
						fZone.log.debug( "Using default Java keystore" );
					} else {
						fZone.log.debug( "Using keystore: " + ksFile.getAbsolutePath() );
					}

					if( ksPwd.equals("changeit") )
	    				fZone.log.debug( "Using default Java keystore password 'changeit'");

				    try {
						ks.load(new FileInputStream(ksFile), passphrase);
					} catch( Exception e ) {
						throw new ADKTransportException("Failed to load keystore "+ksFile.getAbsolutePath()+": "+e,fZone);
					}

					//  TODO: Don't rely on the System property
					//  Point JSSE at the truststore
					String ts = https.getTrustStore();
					String tsPwd = https.getTrustStorePassword();
					if( tsPwd == null )
						tsPwd = "changeit";

					if( ts != null )
					{
						File tsFile = new File(ts);
						if( !tsFile.exists() )
	    					throw new ADKTransportException( "Truststore file not found: " + tsFile.getAbsolutePath(), fZone );
						fZone.log.debug( "Using truststore: " + tsFile.getAbsolutePath() );
						System.setProperty( "javax.net.ssl.trustStore", ts );
						System.setProperty( "javax.net.ssl.trustStorePassword", tsPwd );
					}
					else
						fZone.log.debug( "Using default Java truststore" );

					if( tsPwd.equals("changeit") )
						fZone.log.debug( "Using default Java truststore password 'changeit'" );

					kmf.init(ks, passphrase);
					ctx.init(kmf.getKeyManagers(), null, null);
					fSSLFactory = ctx.getSocketFactory();

					HttpsURLConnection.setDefaultSSLSocketFactory(fSSLFactory);
				}

				//
				//  The URLConnection class handles keep-alive internally so even
				//  though we ask for a new HttpsURLConnection each call to this
				//  method, prior connections are cached and kept alive (at least
				//  this is my understanding).
				//
				HttpsURLConnection conn = (HttpsURLConnection)fURL.openConnection();
				conn.setSSLSocketFactory(fSSLFactory);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				
				HTTPUtil.setTimeoutsOnConnection( https, conn );

				String hostnameVerifier = https.getHostnameVerifier();
				if( hostnameVerifier == null || hostnameVerifier.length() == 0 )
				{
					conn.setHostnameVerifier(
	    				new HostnameVerifier() {
		    				public boolean verify( String hostname, SSLSession session ) {
		    					String peer = session.getPeerHost();
		    					if( !hostname.equals(peer) ) {
		    						try {
			    						InetAddress haddr = InetAddress.getByName( hostname );
			    						InetAddress paddr = InetAddress.getByName( peer );
			    						if( !haddr.getHostAddress().equals( paddr.getHostAddress() ) ) {
			    							fZone.log.debug( "Hostname in certificate ("+hostname+") does not match peer address (" + paddr.getHostName() + ", " + paddr.getHostAddress() + ")" );
			    							return false;
			    						}
		    						} catch( UnknownHostException uhe ) {
		    							fZone.log.debug( "Unable to verify hostname: " + uhe );
		    							return false;
		    						}
		    					}
			    				return true;
				    		}
					    }
	    			);
				}
				else
				if( !hostnameVerifier.equalsIgnoreCase("JSSE") )
				{
					//  If the HostnameVerifier property was specified and is set
					//  to 'default', do nothing; leave the JSSE verifier intact.
					//  Otherwise the agent is specifying a fully-qualified
					//  class name to their own HostnameVerifier implementation.
					//  Create an instance and pass it to the HttpsURLConnection

					Class<HostnameVerifier> clz = null;
					HostnameVerifier impl = null;

					try {
						fZone.log.debug( "Using custom HostnameVerifier: " + hostnameVerifier );
						clz = (Class<HostnameVerifier>)Class.forName( hostnameVerifier );
					} catch( ClassNotFoundException cnfe ) {
						fZone.log.error( "HostnameVerifier class not found: " + hostnameVerifier );
					}

					try {
						impl = clz.newInstance();
					} catch( Exception ex ) {
						fZone.log.error( "Unable to instantiate HostnameVerifier class " + hostnameVerifier + ": " + ex );
					}

					conn.setHostnameVerifier( impl );
				}

				return conn;
			}
		}
		catch( Throwable thr )
		{
			throw new ADKTransportException("Failed to create outgoing socket to "+fURL.toExternalForm()+": "+thr,fZone);
		}
	}
	/*
	private void logSSLCerts( HttpsURLConnection conn )
	{
		try
		{

			System.out.println("CipherSuite: "+conn.getCipherSuite());
			Certificate[] certs = conn.getLocalCertificates();
			System.out.println("Local Certificates: "+( certs == null ? "<none>" : String.valueOf(certs.length) ));
			if( certs != null ) {
				for( int i = 0; i < certs.length; i++ )
					System.out.println(certs[i]);
			}

			certs = conn.getServerCertificates();
			System.out.println("Server Certificates: "+( certs == null ? "<none>" : String.valueOf(certs.length) ));
			if( certs != null ) {
				for( int i = 0; i < certs.length; i++ )
					System.out.println(certs[i]);
			}

		}
		catch( Exception e )
		{
			System.out.println(e);
		}
	}
	*/






////////////////////////////////////////////////////////////////////////////////
//
//  Message Sending
//
//
	/**
	 *  Sends a SIF infrastructure message and returns the response.<p>
	 */
	public String send( String msg )
		throws ADKTransportException,
			   ADKMessagingException
	{
		return send(msg,null,-1,true);
	}

	/**
	 *  Sends a SIF infrastructure message and returns the response.<p>
	 */
	public String send( Reader msg, int length )
		throws ADKTransportException,
			   ADKMessagingException
	{
		return send(null,msg,length,true);
	}

	/**
	 *  Sends a SIF infrastructure message and returns the response.<p>
	 *
	 *  The message content should consist of a complete <SIF_Message> element.
	 *  This method sends whatever content is passed to it without any checking
	 *  or validation of any kind.
	 *  <p>
	 *
	 *  There are two ways to pass message content to this method: as a single
	 *  String or as an input stream encapsulated by a Reader. For short messages
	 *  that can be held in memory in a single String without affecting system
	 *  performance, callers should simply pass the String to the <code>msg</code>
	 *  parameter. For longer messages that have been cached to disk, the caller
	 *  may provide an input stream to the message and the number of bytes that
	 *  should be read from that stream.
	 *  <p>
	 *
	 *  @param msg The message content when provided as a String (ignored when
	 *      <code>longMsg</code> is non-null)
	 *  @param longMsg Provides the message content from an arbitrary input
	 *      stream. <code>longMsgLength</code> must also be specified.
	 *  @param longMsgLength Specifies the number of bytes to read from the
	 *      input stream when <code>longMsg</code> is non-null
	 *
	 *  @return The response from the ZIS (expected to be a <SIF_Ack> message)
	 *
	 *  @exception ADKMessagingException is thrown if there is an error sending
	 *      the message to the Zone Integration Server
	 */
	private String send( String msg, Reader longMsg, int longMsgLength, boolean expectResponse )
		throws ADKTransportException,
			   ADKMessagingException
	{
		String response = null;

		byte[] msgBytes = null;

		if( msg != null )
		{
			try{
				// We need to UTF-8 encode the message before we know what it's binary length in bytes will be
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				Writer w = SIFIOFormatter.createOutputWriter( buffer );
				w.write( msg );
				w.flush();
				msgBytes = buffer.toByteArray();
				w.close();
			}
			catch( IOException ioex ){
				throw new ADKMessagingException("HttpProtocolHandler: Unexpected error encoding message: "+ioex,fZone);
			}
		}
		
		int finalContentLength = (msgBytes != null ? msgBytes.length : longMsgLength);
		boolean willCompress = fZone.getProperties().getCompressionThreshold() > -1 && finalContentLength > fZone.getProperties().getCompressionThreshold();
		
		if (willCompress) {
			willCompress = false;
			SIF_ZoneStatus zoneStatus = fZone.getLastReceivedSIF_ZoneStatus(false);
			if (zoneStatus != null) {
				SIF_Protocol sifProtocol = zoneStatus.getSIF_SupportedProtocols().getSIF_Protocol(fTransport.isSecure() ? "HTTPS" : "HTTP");
				if (sifProtocol != null) {
					for (SIF_Property sifProp : sifProtocol.getSIF_Propertys()) {
						if (!"Accept-Encoding".equals(sifProp.getSIF_Name())) continue;
						if (sifProp != null && sifProp.getSIF_Value() != null) {
							List<String> codingPreference = HTTPUtil.derivePreferredCodingFrom(sifProp.getSIF_Value());
							willCompress = codingPreference.contains("gzip");
						}
					}
				}
			}
		}
		
		if (willCompress) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				GZIPOutputStream gzos = new GZIPOutputStream(baos);
				if (msgBytes != null) {
					gzos.write(msgBytes);
				} else {
					char[] buf = new char[1024];
					int charsRead = 0;
					OutputStreamWriter osw = new OutputStreamWriter(gzos);
					while ((charsRead = longMsg.read(buf)) > -1) {
						if (charsRead > 0) {
							osw.write(buf, 0, charsRead);
						}
					}
					osw.flush();
				}
				gzos.finish();
				msgBytes = baos.toByteArray();
				finalContentLength = msgBytes.length;
			} catch (IOException e) {
				throw new ADKMessagingException("Error while compressing outgoing data " + e, fZone, e);
			}
		}
		

		URLConnection conn = getConnection();
		conn.setRequestProperty( "Content-Length", String.valueOf(finalContentLength) );
		conn.setRequestProperty( "Content-Type", SIFIOFormatter.CONTENT_TYPE );
		conn.setRequestProperty( "Accept-Encoding", fZone.getProperties().getAcceptEncoding());
		conn.setRequestProperty( "Host", fHttpHost );
		conn.setRequestProperty( "User-Agent", fHttpUserAgent );
		conn.setRequestProperty( "Connection", "Keep-Alive" );
		if (willCompress) {
			conn.setRequestProperty("Content-Encoding", "gzip");
		}

		OutputStream outStream = null;
		try
		{
			outStream = conn.getOutputStream();
		}
		catch( Exception ex )
		{
	    	throw new ADKTransportException(
	    			"Could not establish a connection to the ZIS (" +
	    			fURL.toExternalForm() + "): " + ex, fZone, ex );
		}


		int totalBytes = 0;
		int bytes = 0;
		char[] buf = new char[1024];

		//  Message content
		if( msgBytes != null ) {
			//
			if( ( ADK.debug & ADK.DBG_TRANSPORT ) != 0 )
				fZone.log.debug("Sending message ("+msgBytes.length+" bytes)");
			if( ( ADK.debug & ADK.DBG_MESSAGE_CONTENT ) != 0 )
				fZone.log.debug(msg);
			try{
				outStream.write( msgBytes );
				outStream.flush();
			}
			catch( IOException ioe ){
				throw new ADKMessagingException(
						"HttpProtocolHandler: Unexpected error sending message: " + ioe , fZone, ioe );
			}
			finally {
				if( outStream != null ){
					try {
						outStream.close();
					} catch (IOException e) {
						fZone.log.warn(e.getMessage(), e  );
					}
					outStream = null;
				}
			}
		}
		else
		{
			try
			{
				if( ( ADK.debug & ADK.DBG_TRANSPORT ) != 0 )
					fZone.log.debug("Sending message (" + longMsgLength + " bytes)");
				PrintWriter out = new PrintWriter( SIFIOFormatter.createOutputWriter( outStream ) );
				while( longMsg.ready() && totalBytes < longMsgLength ) {
					bytes = longMsg.read(buf,0,buf.length);
					out.write(buf,0,bytes);
					totalBytes += bytes;
				}

				out.flush();

				if( out.checkError() )
					throw new ADKMessagingException(
							"HttpProtocolHandler: Unknown error reading long message content from stream",
							fZone );
			}
			catch( Exception ex ) {
				throw new ADKMessagingException(
						"HttpProtocolHandler: Unexpected error sending message: "+ex, fZone, ex );
			}
			finally {
				if( outStream != null ){
					try {
						outStream.close();
					} catch (IOException e) {
						fZone.log.warn(e.getMessage(), e  );
					}
					outStream = null;
				}
			}
		}

		totalBytes = 0;
		int contentLen = conn.getContentLength();
		boolean isGzip = "gzip".equalsIgnoreCase(String.valueOf(conn.getContentEncoding()).trim());
		InputStream in = null;
		
		if( contentLen != 0 ) try
		{
			if( ( ADK.debug & ADK.DBG_TRANSPORT ) != 0 )
				fZone.log.debug("Expecting reply ("+contentLen+" bytes)");
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			in = new BufferedInputStream(conn.getInputStream());
			if (isGzip) {
				in = new GZIPInputStream(in);
			}
			byte[] byteBuf = new byte[8192];
			int bytesRead = 0;
			while ((bytesRead = in.read(byteBuf)) > -1) {
				if (bytesRead > 0) {
					totalBytes += bytesRead;
					baos.write(byteBuf, 0, bytesRead);
				}
			}
			
			response = new String(baos.toByteArray(), "UTF-8");

			if( ( ADK.debug & ADK.DBG_TRANSPORT ) != 0 )
				fZone.log.debug("Received reply ("+totalBytes+" bytes)");
			if( ( ADK.debug & ADK.DBG_MESSAGE_CONTENT ) != 0 )
				fZone.log.debug(response);
		}
		
		catch( Throwable thr )
		{
			throw new ADKMessagingException("HttpProtocolHandler: Error receiving response to sent message: "+thr,fZone, thr);
		}
		finally {
			if( in != null ){
				try{
					// Close the stream to free up the HTTPConnection for re-use
					in.close();
				} catch (IOException ioe ){
					fZone.log.warn( ioe.getMessage(), ioe );
				}
			}
		}

		return response;
	}

	/**
	 *  Parse an HTTP response line
	 */
//	private int parseHttpResponse( String response )
//	{
//		int st = response.indexOf(' ');
//		if( st == -1 )
//			return HttpURLConnection.HTTP_OK;
//
//		int en = response.indexOf(' ',st+1);
//		String code = response.substring(st+1,en);
//		try {
//			return Integer.parseInt(code);
//		} catch( Exception e ){
//			return -1;
//		}
//	}

}
