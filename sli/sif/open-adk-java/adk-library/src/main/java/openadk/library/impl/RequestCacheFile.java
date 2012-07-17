//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Date;
import java.io.*;

import openadk.library.*;
import openadk.library.infra.*;

/**
 *  A RequestCache implementation that stores SIF_Request information to a file
 *  in the agent work directory.
 *
 *  @author Eric Petersen
 *  @version 1.0
 */

public class RequestCacheFile extends RequestCache
{
	protected RandomAccessFile fFile;
	protected HashMap fCache = new HashMap();
	
	// TT 1105 Maximum storage size for each entry is 128k
	private static final int MAX_ENTRY_SIZE = 131072;
	private static final String CACHE_FILE = "requestcache.adk";

	
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.RequestCache#initialize(com.edustructures.sifworks.Agent)
	 */
	protected synchronized void initialize( Agent agent )
		throws ADKException
	{
		// Look for the legacy version 1 request cache file, called "requests.adk"
		String fname = agent.getHomeDir() + File.separator + "work" + File.separator + "requests.adk";
		File cacheFile = new File( fname );
		if( cacheFile.exists() )
		{	
			// Read the legacy file and convert to the current format. This method will read the old file,
			// store it in the current format, and then destroy the old file
			convertLegacyFile( agent, cacheFile );
		}
		
		// Initialize the agent
		initialize( agent, false );
		
	}
	

	
	/** Initializes the RequestCacheFile
	 * @param agent The agent to cache requests for
	 * @param isRetry False if this method is being called for the first time. This class may call this method again,
	 * with this parameter set to TRUE, which will signal that it is attempting to recover from a corrupt file condition.
	 * @throws ADKException
	 */
	private synchronized void initialize( Agent agent, boolean isRetry )
		throws ADKException
	{
		
		//  Ensure the requestcache.adk file exists in the work directory
		String fname = agent.getHomeDir() + File.separator + "work" + File.separator + "requestcache.adk";
		File cacheFile = new File( fname );
		if( !cacheFile.exists() ) {
			if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 )
				Agent.getLog().info( "Creating SIF_Request ID cache: " + cacheFile.getAbsolutePath() );
		}

		try {
			fFile = new RandomAccessFile( cacheFile, "rw" );
		} catch( FileNotFoundException fnfe ) {
			throw new ADKException( "Error opening or creating SIF_Request ID cache: " + fnfe, null );
		}

		//  Read the file contents into memory
		if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 )
		{
			Agent.getLog().debug( "Reading SIF_Request ID cache: " + cacheFile.getAbsolutePath() );
		}

		String tmpName = null;
		RandomAccessFile tmp = null;

		try
		{
			tmpName = agent.getHomeDir() + File.separator + "work" + File.separator + "requestcache.$dk";
			tmp = new RandomAccessFile( tmpName, "rw" );
			tmp.setLength( 0 );

			int days = 90;
			String str = System.getProperty( "adkglobal.requestCache.age" );
			if( str != null && str.length() > 0  ) {
				try {
					days = Integer.parseInt(str);
				} catch( Exception e ) {
					Agent.getLog().warn( "Error parsing property 'adkglobal.requestCache.age', default of 90 days will be used: " + e.getMessage(), e );
				}
			}
			
			//  Clear out anything older than 90 days
			Calendar now = Calendar.getInstance();
			now.add( Calendar.DAY_OF_YEAR, -days );
			Date maxAge = now.getTime();
			
			RequestCacheFileEntry next = null;
			while( ( next = read( fFile, false ) ) != null )
			{
				if( next.isActive() && next.getRequestTime().compareTo( maxAge ) > 0 )
				{
					try{
						store( tmp, next );	
					}
					catch( RequestSerializationException rse ){
						Agent.getLog().warn( "Unable to store entry in the RequestCache: " + rse.toString(), rse );
					}
					
				}
			}

			tmp.close();
			fFile.close();

			//
			//  Overwrite the requests.adk file with the temporary, then
			//  delete the temporary.
			//
			File backupFile = new File( fname +  ".bak" );
			if( backupFile.exists() )
			{
				backupFile.delete();
			}
			cacheFile.renameTo(  backupFile );
			
			File newCacheFile = new File( tmpName );
			newCacheFile.renameTo( cacheFile );
			
			backupFile.delete();
			
			try {
				fFile = new RandomAccessFile( cacheFile, "rw" );
			} catch( FileNotFoundException fnfe ) {
				throw new ADKException( "Error opening or creating SIF_Request ID cache: " + fnfe, null );
			}

			if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 )
	    		Agent.getLog().debug( "Read " + fCache.size() + " pending SIF_Request IDs from cache" );
		}
		catch( IOException ioe )
		{
			Agent.getLog().warn( "Could not read SIF_Request ID cache (will start with fresh cache): " + ioe );

			//  Make sure the files are closed
			if( tmp != null ) {
				try {
					tmp.close();
				} catch( Throwable ignored ) { /* Ignored exception */ }
			}
			if( fFile != null ) {
				try {
					fFile.close();
				} catch( Throwable ignored ) { /* Ignored exception */ }
			}
			if( isRetry )
			{
				throw new ADKException( "Error opening or creating SIF_Request ID cache: " + ioe, null, ioe );
			}
			else
			{
				//
				//  Delete the files and re-initialize from scratch. We don't
				//  want a file error here to prevent the agent from running, so
				//  no exception is thrown to the caller.
				//
				File del = new File( fname );
				del.delete();
				del = new File( tmpName );
				del.delete();
	
				initialize( agent, true );
			}
		}
		
	}
	
	

	
	
	private void store( RandomAccessFile file, RequestCacheFileEntry entry )
		throws IOException, RequestSerializationException
	{
		entry.setRequestTime( new Date() );
		fCache.put( entry.getMessageId(), entry );
		
		file.seek( file.length() );
		entry.setLocation( file.length() );
		file.writeBoolean( entry.isActive() );
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream objOut = new ObjectOutputStream( out );
		objOut.writeObject( entry );
		
		if( out.size() > MAX_ENTRY_SIZE ){
			throw new RequestSerializationException( "RequestCacheFileEntry maximum allowable serialized size is 128k. Size is: " + out.size(), null );
		}
		
		byte[] serialized = out.toByteArray();
		out.close();
		
		file.writeInt( serialized.length );
		file.write( serialized );
	}
	
	/**
	 * Reads the next RequestCacheFileEntry from the file stream
	 * @param file the filestream to read the entry from
	 * @param readInactiveData If TRUE, all entry properties will be deserialized, even if inactive.
	 * If FALSE, only active entries will be deserialized and inactive entries will be returned as a
	 * RequestCacheFileEntry with the isActive() property set to FALSE
	 * @return The next RequestCacheFileEntry or NULL if at the end of the stream
	 */
	private RequestCacheFileEntry read( DataInput file, boolean readInactiveData ) throws IOException
	{
		try
		{
			boolean active = file.readBoolean();
			int length = file.readInt();
			if( length > MAX_ENTRY_SIZE ){
				// Prevent Out of Memory errors caused by corrupt file. The maximum size allowed for a serialied
				// RequestCacheFileEntry is 128K
				Agent.getLog().error( "Problem reading RequestCache due to unknown corruption. Entries likely are lost." );
				return null;
			}
			if( !active && !readInactiveData )
			{
				file.skipBytes( length );
				return new RequestCacheFileEntry( false );
			}
			else
			{
				try
				{
					// Read the serialized object into a buffer and deserialize
					byte[] serialized = new byte[ length ];
					file.readFully( serialized );
					ByteArrayInputStream input = new ByteArrayInputStream( serialized );
					
					ObjectInputStream dataInput = new ObjectInputStream( input );
					RequestCacheFileEntry returnValue = ( RequestCacheFileEntry )dataInput.readObject();
					
					dataInput.close();
					input.close();
					
					returnValue.setIsActive( active );
					return returnValue;
				}
				catch( ClassNotFoundException cfne )
				{
					// Not sure why this would happen, but we'll just return
					// an inactive record
					return new RequestCacheFileEntry( false );
				}
				catch( IOException iox )
				{
					Agent.getLog().warn( "Error Deserializing RequestCacheFileEntry: " + iox.getMessage(), iox );
					return new RequestCacheFileEntry( false );
				}
			}
		}
		catch( EOFException eofe )
		{
			// We're at the end of the file
			return null;
		}
	}
	
	/**
	 * Reads the original version of the RequestsCacheFile cache, which did not support
	 * storing user state. If the ADK finds a file called "requests.adk" in the agent's work
	 * directory, it will use this method to read the contents. The results are then stored in 
	 * the ADK's current format in a file called "requests2.adk".
	 * 
	 */
	private void convertLegacyFile( Agent agent, File legacyFileInfo )
	{
		RandomAccessFile newFile = null;
		RandomAccessFile legacyFile = null;
		try
		{
			String fname = agent.getHomeDir() + File.separator + "work" + File.separator + CACHE_FILE;
			File newFileInfo = new File( fname );
			if( newFileInfo.exists() ){
				throw new IOException( "File " + fname + " already exists. Legacy file will not be converted." );
			}
			newFile = new RandomAccessFile( fname, "rw" );
			legacyFile = new RandomAccessFile( legacyFileInfo.getPath(), "r" );
			
			// Use the old method for reading the data out of the file
			// Store the data read in the new format
			boolean active;
			long length = legacyFile.length();
			long ptr = 0;
			String msgId = null;
			String objType = null;
			long timestamp = 0;
	
			while( ptr < length )
			{
				active = legacyFile.readBoolean();
				msgId = legacyFile.readUTF();
				objType = legacyFile.readUTF();
				timestamp = legacyFile.readLong();
				if( active )
				{
					//  Add to cache
					RequestCacheFileEntry newEntry = new RequestCacheFileEntry( active );
					newEntry.setIsActive( true );
					newEntry.setMessageId( msgId );
					newEntry.setObjectType( objType );
					newEntry.setRequestTime( new Date( timestamp ) );
					
					try{
						store( newFile, newEntry );
					}
					catch( RequestSerializationException rse ){
						Agent.getLog().warn( "Unable to store entry in the RequestCache: " + rse.toString(), rse );
					}
					
				}
	
				ptr = legacyFile.getFilePointer();
			}
	
		}
		catch( Exception ex )
		{
			Agent.getLog().warn( "An error occurred while attempting to upgrade the ADK Request Cache format: " + ex.toString(), ex );
		}
		finally
		{
			fCache.clear();
			// Against all odds, try deleting the legacy file
			try
			{
				if( newFile != null ){
					newFile.close();
				}
				if( legacyFile != null ){
					legacyFile.close();	
				}
				
				legacyFileInfo.delete();
			}
			catch( Exception ex )
			{
				Agent.getLog().warn( "Unable to delete legacy file " + legacyFileInfo.getPath(), ex );
			}
		}
	}
	
	
	/**
	 * Returns the number of requests that are current active
	 * 
	 * @return The number of active requests
	 */
	public synchronized int getActiveRequestCount()
	{
		return fCache.size();
	}

	/**
	 *  Closes the RequestCache
	 */
	public synchronized void close()
		throws ADKException
	{
	
		try {
			if( fFile != null )
	    		fFile.close();
		} catch( IOException ioe ) {
			throw new ADKException( "Error closing SIF_Request ID cache: " + ioe, null );
		}
		
		sSingleton = null;
	}

	/**
	 *  Store the request MsgId and associated SIF Data Object type in the cache
	 */
	public synchronized RequestInfo storeRequestInfo( SIF_Request request, Query q, Zone zone )
		throws ADKException
	{
		Object userData = q.getUserData();
		if( userData != null )
		{
			if( ! ( userData instanceof Serializable ) ){
				throw new ADKException( "Query.getUserData() contains " + 
						userData.toString() + " which is not Serializable", null );
			}
		}
		try
		{
			RequestCacheFileEntry entry = new RequestCacheFileEntry( true );
			entry.setObjectType( request.getSIF_Query().getSIF_QueryObject().getObjectName() );
			entry.setMessageId( request.getMsgId() );
			entry.setUserData( q.getUserData() );
			store( fFile, entry);
			return entry;
		}
		catch( Throwable thr )
		{
			throw new ADKException( "Error writing to SIF_Request ID cache (MsgId: " + request.getMsgId() + ") " + thr, zone );
		}
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.RequestCache#getRequestInfo(java.lang.String, com.edustructures.sifworks.Zone)
	 */
	public synchronized RequestInfo getRequestInfo( String msgId, Zone zone )
		throws ADKException
	{
		return lookup( msgId, zone, true );
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.RequestCache#lookupRequestInfo(java.lang.String, com.edustructures.sifworks.Zone)
	 */
	public synchronized RequestInfo lookupRequestInfo( String msgId, Zone zone )
		throws ADKException
	{
		return lookup( msgId, zone, false );
	}

	/**
	 * Looks up the specified entry in the cache.
	 * @param msgId The message id to lookup
	 * @param zone The zone associated with the message
	 * @param remove If TRUE, the entry will be removed from the cache
	 * @return The entry associated with the specified message ID or NULL if no entry was found
	 * @throws ADKException
	 */
	protected RequestCacheFileEntry lookup( String msgId, Zone zone, boolean remove )
		throws ADKException
	{
		RequestCacheFileEntry e = (RequestCacheFileEntry)fCache.get(msgId);
		if( e == null )
		{
			return null;
		}

		if( remove )
		{
			try {
			    fCache.remove( msgId );
	        	fFile.seek( e.getLocation() );
		        fFile.writeBoolean( false );
			} catch( IOException ioe ) {
				throw new ADKException( "Error removing entry from SIF_Request ID cache: " + ioe, zone );
			} 
		}
		return e;
	}
	
	class RequestSerializationException extends Exception
	{
		public RequestSerializationException( String message ) {
			super( message );
		}
		
		public RequestSerializationException( String message, Throwable thr ) {
			super( message, thr );
		}
	}

	@Override
	public RequestInfo storeServiceRequestInfo(SIF_ServiceInput request,
			Query q, Zone zone) throws ADKException {
		Object userData = q.getUserData();
		if( userData != null )
		{
			if( ! ( userData instanceof Serializable ) ){
				throw new ADKException( "Query.getUserData() contains " + 
						userData.toString() + " which is not Serializable", null );
			}
		}
		try
		{
			RequestCacheFileEntry entry = new RequestCacheFileEntry( true );
			// entry.setObjectType( request.getSIF_Query().getSIF_QueryObject().getObjectName() );
			entry.setObjectType( request.getSIF_Service());
			entry.setMessageId( request.getMsgId() );
			entry.setUserData( q.getUserData() );
			store( fFile, entry);
			return entry;
		}
		catch( Throwable thr )
		{
			throw new ADKException( "Error writing to SIF_Request ID cache (MsgId: " + request.getMsgId() + ") " + thr, zone );
		}
	}
}
