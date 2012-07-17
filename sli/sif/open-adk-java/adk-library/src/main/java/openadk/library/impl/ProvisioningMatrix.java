//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import openadk.library.ADKException;
import openadk.library.Agent;
import openadk.library.ElementDef;
import openadk.library.Provisioner;
import openadk.library.ProvisioningOptions;
import openadk.library.Publisher;
import openadk.library.PublishingOptions;
import openadk.library.QueryResults;
import openadk.library.QueryResultsOptions;
import openadk.library.ReportPublisher;
import openadk.library.ReportPublishingOptions;
import openadk.library.SIFContext;
import openadk.library.SIFDTD;
import openadk.library.Subscriber;
import openadk.library.SubscriptionOptions;
import openadk.library.reporting.ReportingDTD;


/**
 * Provides a matrix of provisioning options for an agent, zone, or topic, divided by context. Any provisioning
 * that is done is either done in the default context or in a specific context. This class
 * enables all the provisioning registrations to be handled easily by the Zone, Agent, or Topic classes.
 * 
 * To register a generic handler for any object type, the key that is used is SIFDTD.SIF_MESSAGE
 * 
 * @author Andrew
 *
 */
public class ProvisioningMatrix implements Provisioner {

	private List<ContextMatrix> fAllContexts = new ArrayList<ContextMatrix>();
	
	public synchronized void setPublisher(Publisher publisher)
		throws ADKException
	{
		setPublisher( publisher, null, null );
	}
	
	
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setPublisher(com.edustructures.sifworks.Publisher, com.edustructures.sifworks.ElementDef)
	 */
	public synchronized void setPublisher(Publisher publisher, ElementDef objectType ) 
	throws ADKException 
	{
		setPublisher( publisher, objectType, null );
		
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setPublisher(com.edustructures.sifworks.Publisher, com.edustructures.sifworks.ElementDef, com.edustructures.sifworks.PublishingOptions)
	 */
	public synchronized void setPublisher(Publisher publisher, ElementDef objectType, PublishingOptions options) 
		throws ADKException 
	{
		if( objectType != null && objectType == ReportingDTD.SIF_REPORTOBJECT ){
			throw new IllegalArgumentException( "You must call setReportPublisher() for SIF_ReportObject type. " );
		}
		
		if( options == null ){
			options = new PublishingOptions();
		}
		for( SIFContext context : options.getSupportedContexts() ){
			getOrCreateContextMatrix( context ).setPublisher( publisher, objectType, options );
		}
		
	}
	
	
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setReportPublisher(com.edustructures.sifworks.ReportPublisher)
	 */
	public synchronized void setReportPublisher(ReportPublisher publisher ) 
	throws ADKException 
	{
		setReportPublisher(publisher, null);
	}
	

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setReportPublisher(com.edustructures.sifworks.ReportPublisher, com.edustructures.sifworks.ReportPublishingOptions)
	 */
	public synchronized void setReportPublisher(ReportPublisher publisher, ReportPublishingOptions options) 
		throws ADKException 
	{
		if( options == null ){
			options = new ReportPublishingOptions();
		}
		for( SIFContext context : options.getSupportedContexts() ){
			getOrCreateContextMatrix( context ).setReportPublisher( publisher, options );
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setSubscriber(com.edustructures.sifworks.Subscriber, com.edustructures.sifworks.ElementDef)
	 */
	public synchronized void setSubscriber(Subscriber subscriber, ElementDef objectType ) 
	throws ADKException 
	{
		setSubscriber( subscriber, objectType, null );
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setSubscriber(com.edustructures.sifworks.Subscriber, com.edustructures.sifworks.ElementDef, com.edustructures.sifworks.SubscriptionOptions)
	 */
	public synchronized void setSubscriber(Subscriber subscriber, ElementDef objectType, SubscriptionOptions options) 
		throws ADKException 
	{
		if( options == null ){
			options = new SubscriptionOptions();
		}
		for( SIFContext context : options.getSupportedContexts() ){
			getOrCreateContextMatrix( context ).setSubscriber( subscriber, objectType, options );
		}
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setQueryResults(com.edustructures.sifworks.QueryResults)
	 */
	public synchronized void setQueryResults(QueryResults queryResults) 
		throws ADKException 
	{
		setQueryResults( queryResults, null, null );
	}
	
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setQueryResults(com.edustructures.sifworks.QueryResults, com.edustructures.sifworks.ElementDef)
	 */
	public synchronized void setQueryResults(QueryResults queryResults, ElementDef objectType )
	throws ADKException
	{
		setQueryResults( queryResults, objectType, null );
	}
	

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setQueryResults(com.edustructures.sifworks.QueryResults, com.edustructures.sifworks.ElementDef, com.edustructures.sifworks.QueryResultsOptions)
	 */
	public synchronized void setQueryResults(QueryResults queryResults, ElementDef objectType, QueryResultsOptions options)
		throws ADKException
	{
		if( options == null ){
			options = new QueryResultsOptions();
		}
		for( SIFContext context : options.getSupportedContexts() ){
			getOrCreateContextMatrix( context ).setQueryResults( queryResults, objectType, options );
		}
	}
	
	/**
	 * Looks up the report publisher for the specified context
	 * @param context
	 * @param sif_reportobject
	 * @return
	 */
	public ReportPublisher lookupReportPublisher( SIFContext context ) {
		ContextMatrix handler = lookupContextMatrix( context );
		if( handler != null ){
			ProvisionedObject<ReportPublisher,ReportPublishingOptions> objHandler = handler.getReportPublisher();
			if( objHandler != null ){
				return objHandler.getHandler();
			}
		}
		return null;
	}

	/**
	 * Looks up the report publisher for the specified context
	 * @param context
	 * @param sif_reportobject
	 * @return
	 */
	public ReportPublisher lookupServicePublisher( SIFContext context ) {
		ContextMatrix handler = lookupContextMatrix( context );
		if( handler != null ){
			ProvisionedObject<ReportPublisher,ReportPublishingOptions> objHandler = handler.getReportPublisher();
			if( objHandler != null ){
				return objHandler.getHandler();
			}
		}
		return null;
	}

	
	/**
	 * Looks up the publisher for the specified context and object type
	 * @param context
	 * @param objectType
	 * @return
	 */
	public synchronized Publisher lookupPublisher( SIFContext context, ElementDef objectType ){
		ContextMatrix handler = lookupContextMatrix( context );
		if( handler != null ){
			return handler.lookupPublisher( objectType );
		}
		return null;
	}
	
	/**
	 * Returns all of the Publisher options for all contexts
	 * @param excludeWildcard
	 * @return
	 */
	public synchronized List<ProvisionedObject<Publisher, PublishingOptions>> getAllPublishers( boolean excludeWildcard )
	{
		List<ProvisionedObject<Publisher, PublishingOptions>> list = new ArrayList<ProvisionedObject<Publisher, PublishingOptions>>();
		for( ContextMatrix context : fAllContexts ){
			getAll( context.fPubs, list, excludeWildcard );
		}
		return list;
	}
	
	/**
	 * Returns all of the ReportPublishing options for all contexts
	 * @return
	 */
	public synchronized List<ProvisionedObject<ReportPublisher, ReportPublishingOptions>> getAllReportPublishers()
	{
		List<ProvisionedObject<ReportPublisher, ReportPublishingOptions>> list = new ArrayList<ProvisionedObject<ReportPublisher, ReportPublishingOptions>>();
		for( ContextMatrix context : fAllContexts ){
			ProvisionedObject<ReportPublisher,ReportPublishingOptions> handler = context.getReportPublisher();
			if( handler != null ){
				list.add( handler );
			}
		}
		return list;
	}
	
	
	/**
	 * Returns all of the Subscriptions options for all contexts
	 * @param excludeWildcard
	 * @return
	 */
	public synchronized List<ProvisionedObject<Subscriber, SubscriptionOptions>> getAllSubscribers( boolean excludeWildcard )
	{
		List<ProvisionedObject<Subscriber, SubscriptionOptions>> list = new ArrayList<ProvisionedObject<Subscriber, SubscriptionOptions>>();
		for( ContextMatrix context : fAllContexts ){
			getAll( context.fSubs, list, excludeWildcard );
		}
		return list;
	}
	
	/**
	 * Returns all of the QueryResults options for all contexts
	 * @param excludeWildcard
	 * @return
	 */
	public synchronized List<ProvisionedObject<QueryResults, QueryResultsOptions>> getAllQueryResults( boolean excludeWildcard )
	{
		List<ProvisionedObject<QueryResults, QueryResultsOptions>> list = new ArrayList<ProvisionedObject<QueryResults, QueryResultsOptions>>();
		for( ContextMatrix context : fAllContexts ){
			getAll( context.fQueryResults, list, excludeWildcard );
		}
		return list;
	}
	
	private synchronized <K,Y extends ProvisioningOptions> void getAll( 
			Map<ElementDef,ProvisionedObject<K,Y >> items, 
			List<ProvisionedObject<K,Y>> list, boolean excludeWildcard )
	{
		for( Map.Entry<ElementDef,ProvisionedObject<K, Y>> entry : items.entrySet() ){
			if( excludeWildcard && entry.getKey() == SIFDTD.SIF_MESSAGE ){
				continue;
			}
			list.add( entry.getValue() );
		}
		
	}
	
	/**
	 * Looks up the subscriber for the specified context and object type
	 * @param context
	 * @param objectType
	 * @return
	 */
	public synchronized QueryResults lookupQueryResults( SIFContext context, ElementDef objectType ){
		ContextMatrix handler = lookupContextMatrix( context );
		if( handler != null ){
			return handler.lookupQueryResults( objectType );
		}
		return null;
	}
	
	
	/**
	 * Looks up the subscriber for the specified context and object type
	 * @param context
	 * @param objectType
	 * @return
	 */
	public synchronized Subscriber lookupSubscriber( SIFContext context, ElementDef objectType ){
		ContextMatrix handler = lookupContextMatrix( context );
		if( handler != null ){
			return handler.lookupSubscriber( objectType );
		}
		return null;
	}
	
	private ContextMatrix lookupContextMatrix( SIFContext context )
	{
		for( ContextMatrix handler : fAllContexts ){
			if( handler.fContext.equals( context ) ){
				return handler;
			}
		}
		return null;
	}
	
	/**
	 * Looks up the ContextMatrix for the specified SIFContext
	 * @param context
	 * @return
	 */
	private ContextMatrix getOrCreateContextMatrix( SIFContext context )
	{
		ContextMatrix handler = lookupContextMatrix( context );
		if( handler == null ){
			handler = new ContextMatrix( context );
			fAllContexts.add( handler );
		}
		return handler;
	}
	
	
	
	/**
	 * Contains references to all the message handlers supported for a specific SIF Context
	 * @author Andrew
	 *
	 */
	private static class ContextMatrix
	{
		/**
		 *  The Subscribers registered with this context. The map consists of Handler
		 *  objects keyed by SIF data object names (e.g. "StudentPersonal"). If a
		 *  Subscriber is registered for all object types, it is keyed by the
		 *  string "*".
		 */
		private Map<ElementDef, ProvisionedObject<Subscriber, SubscriptionOptions>> fSubs = 
						new HashMap<ElementDef, ProvisionedObject<Subscriber, SubscriptionOptions>>();

		/**
		 *  The Publishers registered with this context. The map consists of Handler
		 *  objects keyed by SIF data object names (e.g. "StudentPersonal"). If a
		 *  Publisher is registered for all object types, it is keyed by the string "*".
		 */
		private Map<ElementDef, ProvisionedObject<Publisher, PublishingOptions>> fPubs = 
						new HashMap<ElementDef, ProvisionedObject<Publisher, PublishingOptions>>();

		/**
		 *  The QueryResults objects registered in this context. The map is keyed by
		 *  SIF data object names (e.g. "StudentPersonal"). If a QueryResults object
		 *  is registered for all object types, it is keyed by the string "*".
		 */
		private Map<ElementDef, ProvisionedObject<QueryResults, QueryResultsOptions>> fQueryResults = 
						new HashMap<ElementDef, ProvisionedObject<QueryResults, QueryResultsOptions>>();
		
		
		/**
		 * The context that this context handler covers 
		 */
		private SIFContext fContext;
		
		/**
		 * The ReportPublisher instance for this context 
		 */
		private ProvisionedObject<ReportPublisher, ReportPublishingOptions> fReportPublisher;
		
		
		public ContextMatrix( SIFContext context ){
			fContext = context;
		}
		
		/**
		 * Returns the report publisher for this context
		 * @return
		 */
		public ProvisionedObject<ReportPublisher,ReportPublishingOptions> getReportPublisher() {
			return fReportPublisher;
		}
		
		
		
		/**
		 * Sets the ReportPublisher instance for this context
		 * @param rp
		 */
		public void setReportPublisher( ReportPublisher rp, ReportPublishingOptions options ){
			
			if( rp == null ){
				ADKUtils._throw( new IllegalArgumentException("ReportPublisher object cannot be null"),Agent.getLog() );
			}

			if( ReportingDTD.SIF_REPORTOBJECT == null ){
				ADKUtils._throw( new IllegalStateException("The ADK Reporting package is not loaded"),Agent.getLog() );
			}
			
			fReportPublisher = new ProvisionedObject<ReportPublisher,ReportPublishingOptions>( ReportingDTD.SIF_REPORTOBJECT, rp, options );
		}

		/**
		 * Sets the publisher for this context and object type
		 * @param publisher
		 * @param objectType
		 * @param options
		 */
		public void setPublisher( 
				Publisher publisher, 
				ElementDef objectType, 
				PublishingOptions options )
		{
			setHandler( fPubs, publisher, objectType, options );
		}
		
		/**
		 * Sets the subscriber for this context and object type
		 * @param subscriber
		 * @param objectType
		 * @param options
		 */
		public void setSubscriber( 
				Subscriber subscriber, 
				ElementDef objectType, 
				SubscriptionOptions options )
		{
			setHandler( fSubs, subscriber, objectType, options );
		}
		
		
		/**
		 * Sets the Query results handler for this context and object type
		 * @param qr
		 * @param objectType
		 * @param options
		 */
		public void setQueryResults( 
				QueryResults qr, 
				ElementDef objectType, 
				QueryResultsOptions options )
		{
			setHandler( fQueryResults, qr, objectType, options );
		}

		/**
		 * Looks up the publisher for the specified object type
		 * @param objectType
		 * @return
		 */
		public Publisher lookupPublisher(ElementDef objectType) {
			return lookupHandler( objectType, fPubs );
		}
		
		/**
		 * Looks up the subscriber for the specified object type
		 * @param objectType
		 * @return
		 */
		public Subscriber lookupSubscriber(ElementDef objectType) {
			return lookupHandler( objectType, fSubs );
		}
		
		/**
		 * Looks up the subscriber for the specified object type
		 * @param objectType
		 * @return
		 */
		public QueryResults lookupQueryResults(ElementDef objectType) {
			return lookupHandler( objectType, fQueryResults );
		}
		
		/**
		 * Sets a handler of a specific type
		 * @param <T>
		 * @param map
		 * @param handler
		 * @param objType
		 * @param options
		 */
		public <T, V extends ProvisioningOptions> void setHandler( Map<ElementDef, ProvisionedObject<T,V>> map, 
							T handler,
							ElementDef objType,
							V options	 )
		{
			if( objType == null ){
				objType = SIFDTD.SIF_MESSAGE;
			}
			ProvisionedObject<T,V> item = new ProvisionedObject<T,V>(objType, handler, options );
			map.put( objType, item );
		}
		
		/**
		 * Looks up a handler of a specific type
		 * @param <T>
		 * @param def
		 * @param map
		 * @return
		 */
		public <T, V extends ProvisioningOptions > T lookupHandler(ElementDef def, Map<ElementDef, ProvisionedObject<T,V>> map ){
			ProvisionedObject<T,?> item = map.get( def );
			if( item == null ){
				// Look for a default handler 
				item = map.get( SIFDTD.SIF_MESSAGE );
			}
			if( item != null ){
				return item.getHandler();
			}
			return null;
		}
		
	
		
	}
	

	
		
	
}
