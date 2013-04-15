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

package org.slc.sli.api.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

@Component
public class CertificateValidationHelper {
	@Value("${sli.security.truststore.path}")
	private String pathToTruststore;

	@Value("${sli.security.truststore.password}")
	private String trustStorePassword;

	public void validateCertificate(HttpServletRequest request) {
		X509Certificate[] certs = (X509Certificate[]) request
				.getAttribute("javax.servlet.request.X509Certificate");

		if (null == certs || certs.length < 1) {
			throw new IllegalArgumentException(
					"App must provide client side X509 Certificate");
		}

		X509Certificate cert = certs[0];
		OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder
				.getContext().getAuthentication();
		String appId = auth.getClientAuthentication().getClientId();

		Certificate storedCert;
		try {
			KeyStore ks = KeyStore.getInstance("JKS");
			InputStream fis = new FileInputStream(new File(
					this.pathToTruststore));
			ks.load(fis, this.trustStorePassword.toCharArray());
			storedCert = ks.getCertificate(appId);
		} catch (Exception e) {
			error("ERROR YO", e);
			throw new IllegalStateException("Failed to Compile yo", e);
		}

		if (null == storedCert) {
			throw new IllegalStateException(
					"We don't like your X509 Certificate");
		}
		if (!storedCert.equals(cert)) {
			throw new IllegalArgumentException(
					"App must provide trusted X509 Certificate");
		}

	}

}
