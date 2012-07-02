//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import openadk.library.AgentProperties;
import openadk.library.TransportProperties;


/**
 * This class shall contain stateless, static helper methods related to HTTP and may also include final static constants or similar resources.
 *
 * @author ujensza
 */
public class HTTPUtil {
    public static class QualifiedToken implements Comparable<QualifiedToken> {
        private String token;
        private BigDecimal quality = BigDecimal.ONE;
        private String repr;

        public QualifiedToken(String _token, String _quality) throws NumberFormatException {
            token = _token;
            if (_quality != null) quality = new BigDecimal(_quality);
        }

        public QualifiedToken(String _token, BigDecimal _quality) {
            token = _token;
            quality = _quality;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (!(obj instanceof QualifiedToken)) return false;
            QualifiedToken qt = (QualifiedToken)obj;
            return token.equals(qt.token);
        }

        @Override
        public int hashCode() {
            return token.hashCode();
        }

        public String getToken() {
            return token;
        }

        @Override
        public String toString() {
            if (repr == null) {
                StringBuilder sb = new StringBuilder();
                sb.append(token);
                if (quality != null) {
                    sb.append(";q=");
                    NumberFormat numf = NumberFormat.getNumberInstance(Locale.US);
                    numf.setMinimumIntegerDigits(0);
                    numf.setMaximumFractionDigits(1);
                    numf.setMinimumFractionDigits(1);
                    sb.append(numf.format(quality));
                }
            }
            return repr;
        }

        public int compareTo(QualifiedToken o) {
            return -1 * quality.compareTo(o.quality);
        }
    }

    /**
     * Parses the acceptEncoding string into a list of QualifiedToken, sorts, then returns just the token strings.
     * @param acceptEncoding
     * @return list of token strings sorted by priority based on quality specified in the header
     */
    public static List<String> derivePreferredCodingFrom(String acceptEncoding) {
        if (acceptEncoding == null) return Collections.emptyList();

        acceptEncoding = acceptEncoding.trim();

        List<QualifiedToken> tokens = getSortedQualifiedTokensFrom(acceptEncoding);
        List<String> results = new ArrayList<String>();
        for (QualifiedToken token : tokens) {
            results.add(token.getToken());
        }
        return results;
    }

    public static Map<String, String> parseHTTPHeaderParams(String params) {
        params = params.trim();
        if (params.length() < 1) return Collections.emptyMap();
        Map<String, String> results = new HashMap<String, String>();
        for (String param : params.split(";")) {
            param = param.trim();
            if (param.length() < 2) continue;
            int equalIndex = param.indexOf('=');
            String key = param.substring(0, equalIndex).trim();
            String val = param.substring(equalIndex+1).trim();
            results.put(key, val);
        }
        return results;
    }

    public static List<QualifiedToken> getSortedQualifiedTokensFrom(String tokenString) {
        if (tokenString == null) return Collections.singletonList(new QualifiedToken("identity", (String)null));
        boolean foundNonZeroAsterisk = false;
        boolean foundIdentity = false;
        String asteriskQuality = null;
        List<QualifiedToken> results = new ArrayList<QualifiedToken>();
        String[] commaDelimitedTokens = tokenString.split(",");
        for (String fullToken : commaDelimitedTokens) {
            fullToken = fullToken.trim();
            String token;
            String quality = null;
            if (fullToken.length() > 0) {
                int splitIndex = fullToken.indexOf(';');
                if (splitIndex > -1) {
                    token = fullToken.substring(0, splitIndex);
                    String rawParams = fullToken.substring(splitIndex+1);
                    Map<String, String> params = parseHTTPHeaderParams(rawParams);
                    quality = params.get("q");
                } else {
                    token = fullToken;
                }
                if (quality == null) quality = "1.0";
                boolean zeroQ = quality.trim().matches("[\\.0]*");

                if (!"*".equals(token)) {
                    if ("identity".equals(token)) foundIdentity = true;
                    results.add(new QualifiedToken(token, quality));
                } else if (!zeroQ) {
                    foundNonZeroAsterisk = true;
                    asteriskQuality = quality;
                }
            }
        }

        if (foundNonZeroAsterisk && !foundIdentity) {
            results.add(new QualifiedToken("identity", asteriskQuality));
        }
        if (results.size() < 1) {
            results.add(new QualifiedToken("identity", (String)null));
        }
        Collections.sort(results);
        if (foundNonZeroAsterisk) {
            results.add(new QualifiedToken("*", asteriskQuality));
        }
        return results;
    }
    
    
	/**
	 * This ensures the proper timeouts are set on the connection.
	 * 
	 * It will:
	 * 1. Try to use the agent.cfg value if they specified it.
	 * 2. Fall back to using the system property if they specified it
	 * 3. Fall back on a default value
	 * 
	 * 
	 * @param properties - the transport properties from which this function will read the timeout properties
	 * @param conn - the URLConnection on which to set the properties
	 */
	public static void setTimeoutsOnConnection(TransportProperties properties, HttpURLConnection conn) {
		Object readTimeout = properties.get(AgentProperties.PROP_PULL_MODE_READ_TIMEOUT);
		Object connectTimeout = properties.get(AgentProperties.PROP_PULL_MODE_CONNECT_TIMEOUT);
		
		Properties systemProperties = System.getProperties();
		
		if ( readTimeout != null ) { //try agent.cfg
			int readTimeoutValue = Integer.parseInt(String.valueOf(readTimeout));
			conn.setReadTimeout(readTimeoutValue);
		} else if ( systemProperties.containsKey("sun.net.client.defaultReadTimeout") ) { //try system properties
			//use system property (happens automatically)
		} else { //use default
			conn.setReadTimeout(AgentProperties.DEFAULT_PULL_READ_TIMEOUT);
		}
		
		if ( connectTimeout != null ) { //try agent.cfg
			int connectTimeoutValue = Integer.parseInt(String.valueOf(connectTimeout));
			conn.setConnectTimeout(connectTimeoutValue);
		} else if (systemProperties.containsKey("sun.net.client.defaultConnectTimeout")) { //try system properties
			//use system property (happens automatically)
		} else { //use default
			conn.setConnectTimeout(AgentProperties.DEFAULT_PULL_CONNECT_TIMEOUT);
		}
	}   
	
	
    public static void main(String... argv) {
        String[] testInputs = new String[] {
          "gzip",
          "",
          "*",
          "gzip;q=1.0",
          "gzip;q=0.5, identity;q=0.7, *;q=0.0",
          "*;  q= 0.0, compress",
          "gzip;q=1.0, *;q=0.0",
          "gzip;q=1.0, identity;q=0.5, *;q=0.1"
        };

        for (String test : testInputs) {
            System.out.printf("%s :: %s\n", test, derivePreferredCodingFrom(test));
        }
    }
}
