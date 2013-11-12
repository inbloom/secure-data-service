/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slc.sli.api.resources.security;

import org.joda.time.DateTime;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Artifact;
import org.opensaml.saml2.core.ArtifactResolve;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.ws.soap.soap11.Body;
import org.opensaml.ws.soap.soap11.Envelope;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureException;
import org.opensaml.xml.signature.Signer;
import org.slc.sli.api.security.saml.SamlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyStore;
import java.util.UUID;

/**
 * @author: npandey
 */
@Component
public class ArtifactBindingHelper {

    @Autowired
    private SamlHelper samlHelper;

    @Value("${sli.security.sp.issuerName}")
    private String issuerName;

    @Value("${sli.security.idp.url}")
    private String idpUrl;


    /**
     *
     * @param artifactString
     *      String representation of the artifact token.
     * @param pkEntry
     *      Key entry from the keystore.
     * @return
     */
    protected ArtifactResolve generateArtifactResolveRequest(String artifactString, KeyStore.PrivateKeyEntry pkEntry) {
        try {
            DefaultBootstrap.bootstrap();
        } catch (ConfigurationException ex) {
            error("Error composing artifact resolution request: xml object configuration initialization failed", ex);
            throw new IllegalArgumentException("Couldn't compose artifact resolution request", ex);
        }

        XMLObjectBuilderFactory xmlObjectBuilderFactory = Configuration.getBuilderFactory();
        Artifact artifact = (Artifact) xmlObjectBuilderFactory.getBuilder(Artifact.DEFAULT_ELEMENT_NAME).buildObject(Artifact.DEFAULT_ELEMENT_NAME);
        artifact.setArtifact(artifactString);
        Issuer issuer = (Issuer) xmlObjectBuilderFactory.getBuilder(Issuer.DEFAULT_ELEMENT_NAME).buildObject(Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue(issuerName);

        //ToDo: Update to obtain the idp url from realm
        /*String idpUrl = getArtifactEndpointFromRealm();
        if(idpUrl == null) {
            error("Error composing artifact resolution request: IdP endpoint not specified");
            throw new APIAccessDeniedException("Artifact Resolution endpoint has not been specified for this realm");
        }*/

        ArtifactResolve artifactResolve =  (ArtifactResolve) xmlObjectBuilderFactory.getBuilder(ArtifactResolve.DEFAULT_ELEMENT_NAME).buildObject(ArtifactResolve.DEFAULT_ELEMENT_NAME);
        artifactResolve.setIssuer(issuer);
        artifactResolve.setIssueInstant(new DateTime());
        artifactResolve.setID(UUID.randomUUID().toString());
        artifactResolve.setDestination(idpUrl);
        artifactResolve.setArtifact(artifact);

        Signature signature = samlHelper.getDigitalSignature(pkEntry);

        artifactResolve.setSignature(signature);

        try {
            Configuration.getMarshallerFactory().getMarshaller(artifactResolve).marshall(artifactResolve);
        } catch (MarshallingException ex) {
            error("Error composing artifact resolution request: Marshalling artifact resolution request failed", ex);
            throw new IllegalArgumentException("Couldn't compose artifact resolution request", ex);
        }

        try {
            Signer.signObject(signature);
        } catch (SignatureException ex) {
            error("Error composing artifact resolution request: Failed to sign artifact resolution request", ex);
            throw new IllegalArgumentException("Couldn't compose artifact resolution request", ex);
        }

        return artifactResolve;
    }

    /**
     *
     * @param artifactResolutionRequest
     * @return
     */
    protected Envelope generateSOAPEnvelope(ArtifactResolve artifactResolutionRequest) {
        XMLObjectBuilderFactory xmlObjectBuilderFactory = Configuration.getBuilderFactory();
        Envelope envelope = (Envelope) xmlObjectBuilderFactory.getBuilder(Envelope.DEFAULT_ELEMENT_NAME).buildObject(Envelope.DEFAULT_ELEMENT_NAME);
        Body body = (Body) xmlObjectBuilderFactory.getBuilder(Body.DEFAULT_ELEMENT_NAME).buildObject(Body.DEFAULT_ELEMENT_NAME);

        body.getUnknownXMLObjects().add(artifactResolutionRequest);
        envelope.setBody(body);

        return envelope;
    }
}
