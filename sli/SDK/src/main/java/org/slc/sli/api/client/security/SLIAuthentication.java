package org.slc.sli.api.client.security;


/**
 * 
 * @author asaarela
 */
public class SLIAuthentication {
    /**
     * private static final Logger LOG = LoggerFactory.getLogger(SLIAuthentication.class);
     * 
     * @Value("${oauth.redirect ")
     *                          private String callbackUrl;
     * @Value("${oauth.client.id ")
     *                           private String clientId;
     * @Value("${oauth.client.secret ")
     *                               private String clientSecret;
     * @Value("${api.server.url ")
     *                          private String apiUrl;
     * 
     *                          private static final String OAUTH_TOKEN = "OAUTH_TOKEN";
     *                          private static final String ENTRY_URL = "ENTRY_URL";
     * 
     *                          private RESTClient restClient;
     * 
     *                          public void setRestClient(RESTClient restClient) {
     *                          this.restClient = restClient;
     *                          }
     * 
     *                          private void addAuthentication(String token) {
     *                          JsonObject json = restClient.sessionCheck();
     *                          LOG.debug(json.toString());
     * 
     *                          // If the user is authenticated, create an SLI principal, and
     *                          authenticate
     *                          if (json.get("authenticated").getAsBoolean()) {
     *                          SLIPrincipal principal = new SLIPrincipal();
     *                          JsonElement nameElement = json.get("full_name");
     *                          principal.setName(nameElement.getAsString());
     *                          principal.setId(token);
     *                          JsonArray grantedAuthorities =
     *                          json.getAsJsonArray("granted_authorities");
     *                          Iterator<JsonElement> authIterator = grantedAuthorities.iterator();
     *                          LinkedList<GrantedAuthority> authList = new
     *                          LinkedList<GrantedAuthority>();
     * 
     *                          // Add authorities to user principal
     *                          while (authIterator.hasNext()) {
     *                          JsonElement nextElement = authIterator.next();
     *                          authList.add(new GrantedAuthorityImpl(nextElement.getAsString()));
     *                          }
     * 
     *                          SecurityContextHolder.getContext().setAuthentication(new
     *                          PreAuthenticatedAuthenticationToken(principal, token, authList));
     *                          }
     *                          }
     * @Override
     *           public void commence(HttpServletRequest request, HttpServletResponse response,
     *           AuthenticationException authException)
     *           throws IOException, ServletException {
     *           SliApi.setBaseUrl(apiUrl);
     *           LOG.debug("Client ID is " + clientId + ", clientSecret is " + clientSecret +
     *           ", callbackUrl is " + callbackUrl);
     *           OAuthService service = new ServiceBuilder().provider(SliApi.class).
     *           apiKey(clientId).apiSecret(clientSecret).callback(callbackUrl).
     *           build();
     * 
     *           HttpSession session = request.getSession();
     *           Object token = session.getAttribute(OAUTH_TOKEN);
     *           LOG.debug("Oauth token in session - " + session.getAttribute(OAUTH_TOKEN) +
     *           " and access code - " + request.getParameter("code") + " and request URL is " +
     *           request.getRequestURL());
     *           if (session.getAttribute(OAUTH_TOKEN) == null && request.getParameter("code") !=
     *           null) {
     *           Verifier verifier = new Verifier(request.getParameter("code"));
     *           Token accessToken = service.getAccessToken(null, verifier);
     *           session.setAttribute(OAUTH_TOKEN, accessToken.getToken());
     *           Object entryUrl = session.getAttribute(ENTRY_URL);
     *           if (entryUrl != null) {
     *           response.sendRedirect(session.getAttribute(ENTRY_URL).toString());
     *           } else {
     *           response.sendRedirect(request.getRequestURI());
     *           }
     *           } else if (session.getAttribute(OAUTH_TOKEN) == null) {
     *           session.setAttribute(ENTRY_URL, request.getRequestURL());
     * 
     *           //The request token doesn't matter for OAuth 2.0 which is why it's null
     *           String authUrl = service.getAuthorizationUrl(null);
     *           response.sendRedirect(authUrl);
     *           } else {
     *           LOG.debug("Using access token " + token);
     *           addAuthentication((String) token);
     *           response.sendRedirect(request.getRequestURI());
     *           }
     *           }
     * 
     *           public String getClientId() {
     *           return clientId;
     *           }
     * 
     *           public String getClientSecret() {
     *           return clientSecret;
     *           }
     * 
     *           public String getCallbackUrl() {
     *           return callbackUrl;
     *           }
     * 
     *           public void setClientId(String clientId) {
     *           this.clientId = clientId;
     *           }
     * 
     *           public void setClientSecret(String clientSecret) {
     *           this.clientSecret = clientSecret;
     *           }
     * 
     *           public void setCallbackUrl(String callbackUrl) {
     *           this.callbackUrl = callbackUrl;
     *           }
     * 
     *           public void setApiUrl(String apiUrl) {
     *           this.apiUrl = apiUrl;
     *           }
     * 
     *           public String getApiUrl() {
     *           return apiUrl;
     *           }
     */
}

