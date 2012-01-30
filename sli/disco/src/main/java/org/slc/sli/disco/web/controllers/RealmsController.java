package org.slc.sli.disco.web.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Fetches realm information
 * 
 * @author dkornishev
 * 
 */
@Controller
@Scope("request")
@RequestMapping("/realms")
public class RealmsController {
    
    private static final Logger LOG    = LoggerFactory.getLogger(RealmsController.class);
    
    private ObjectMapper        mapper = new ObjectMapper();
    private RestTemplate        rest   = new RestTemplate();
    
    @Autowired
    @Value("${sli.security.realms.list.url}")
    private String              listUrl;
    
    @Autowired
    @Value("${sli.security.realms.ssoInit.url}")
    private String              ssoInitUrl;
    
    /**
     * Calls api to list available realms and injects into model
     * 
     * @param model spring injected model
     * @return name of the template to use
     * @throws IOException
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String listRealms(@RequestParam(value = "RelayState", required = false) String relayState, Model model) throws IOException {
        
        ResponseEntity<String> resp = rest.getForEntity(this.listUrl, String.class);
        
        LOG.debug(resp.getBody());
        
        Map<String, String> map = new HashMap<String, String>();
        
        JsonNode json = mapper.readTree(resp.getBody());
        
        Iterator<JsonNode> nodes = json.getElements();
        
        while (nodes.hasNext()) {
            JsonNode node = nodes.next();
            map.put(node.get("id").getTextValue(), node.get("state").getTextValue());
        }
        
        model.addAttribute("dummy", new HashMap<String, String>());
        model.addAttribute("realms", map);
        model.addAttribute("relayState", relayState != null ? relayState : "");
        
        if (relayState == null) {
            model.addAttribute("errorMsg", "No relay state provided.  User won't be redirected back to the application");
        }
        
        return "realms";
    }
    
    /**
     * Redirects user to the sso init url given valid id
     * 
     * @param realmId id of the realm
     * @return directive to redirect to sso init page
     * @throws IOException
     */
    @RequestMapping(value = "sso", method = { RequestMethod.GET, RequestMethod.POST })
    public String ssoInit(@RequestParam(value = "realmId", required = false) String realmId, @RequestParam(value = "RelayState", required = false) String relayState, Model model) throws IOException {
        try {
            ResponseEntity<String> redirect = rest.getForEntity(this.ssoInitUrl, String.class, realmId);
            
            String body = redirect.getBody().replaceAll("\"", "");
            
            return "redirect:" + body + "&RelayState=" + relayState;
        } catch (RestClientException e) {
            LOG.error("Error Calling API", e);
            
            model.addAttribute("errorMsg", realmId == null ? "No realm selected.  Please select your realm." : "Error calling server");
            return this.listRealms(relayState, model);
        }
    }
    
    public void setRest(RestTemplate rest) {
        this.rest = rest;
    }
}
