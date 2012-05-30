package org.slc.sli.ingestion.validation;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * Throws a runtime exception which is caught by the XsdValidatior
 * if a user attempts to pass external entities in their XML.
 *
 * @author dshaw
 *
 */
public final class ExternalEntityResolver implements LSResourceResolver {

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        throw new RuntimeException("Attempted disallowed ingestion of External XML Entity (XXE).");
    }
}