//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.ADKException;
import openadk.library.Agent;
import openadk.library.SIFParser;

/**
 * Factory for creating objects used by the ADK. The following object are
 * currently created by this class:
 * 
 * <ul>
 * <li>ZoneFactory (The ZoneFactory used to create zone objects in the ADK)</li>
 * <li>TopicFactory (The Topic factory used by the ADK)</li>
 * </ul>
 * 
 * @author Andrew Elmhorst
 * @version 2.1
 * 
 */
public abstract class ObjectFactory {

	/**
	 * The name of the System property that is checked for a class name used to
	 * create an instance of the ObjectFactory
	 */
	public static final String OBJECT_FACTORY_CLASS = "adkglobal.factory.ObjectFactory";

	private static ObjectFactory sInstance;

	/**
	 * Returns the object factory used by the ADK to create objects
	 * 
	 * @return the object factory used by the ADK to create objects
	 * @throws ADKException
	 */
	public static synchronized ObjectFactory getInstance() throws ADKException {
		if ( sInstance == null ) {
			String cls = System.getProperty( OBJECT_FACTORY_CLASS );
			if ( cls == null || cls.length() == 0 ) {
				sInstance = new ObjectFactoryImpl();
			} else {
				try {
					sInstance = (ObjectFactory) Class.forName( cls )
							.newInstance();
				} catch ( Throwable thr ) {
					throw new ADKException(
							"ADK could not create an instance of the class "
									+ cls + ": " + thr, null );
				}
			}
		}
		return sInstance;
	}

	/**
	 * Creates an instance of the object factory of a specified type
	 * 
	 * @param factoryType
	 *            the type of Object factory to return
	 * @param agentInstance
	 *            the running instance of Agent (required)
	 * @return the requested object factory
	 */
	public abstract Object createInstance(ADKFactoryType factoryType,
			Agent agentInstance) throws ADKException;

	/**
	 * The Types of objects that can be created by ObjectFactory
	 * 
	 * @author Andrew Elmhorst
	 * @version 2.1
	 * 
	 */
	/**
	 * @author Andy
	 *
	 */
	public enum ADKFactoryType {
		/**
		 * Indicates that the requested type is subclass of ZoneFactory
		 */
		ZONE,
		/**
		 * Indicates that the requested type is subclass of ZoneFactory
		 */
		TOPIC,
		/**
		 * Indicates that the requested type is subclass of PolicyFactory
		 */
		POLICY_FACTORY,
		/**
		 * Indicates that the requested type is subclass of PolicyManager
		 */
		POLICY_MANAGER;
	}

}
