//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.xpath;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathContextFactory;
import org.apache.commons.jxpath.JXPathContextFactoryConfigurationError;

/**
 * Provides a factory interface for creating new SIFXPathContexts
 * 
 * @author Andrew Elmhorst
 * @version ADK 2.0
 */
public class SIFXPathContextFactory extends JXPathContextFactory {

	@Override
	public JXPathContext newContext(JXPathContext parentContext,
			Object contextBean) throws JXPathContextFactoryConfigurationError {
		return new SIFXPathContext(parentContext, contextBean);
	}

}
