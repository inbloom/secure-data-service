package org.slc.sli.admin.web.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

/**
 * Fetches realm information
 * 
 * @author dkornishev
 * 
 */
@Controller
@RequestMapping("/realms")
public class RealmsController {
    
    private ObjectMapper mapper = new ObjectMapper();
    private RestTemplate rest   = new RestTemplate();
    
    @Autowired
    @Value("${security.realms.list.url}")
    private String       listUrl;
    
    @Autowired
    @Value("${security.realms.ssoInit.url}")
    private String       ssoInitUrl;
    
    /**
     * Calls api to list available realms and injects into model
     * 
     * @param model spring injected model
     * @return name of the template to use
     * @throws IOException
     */
    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    public String listRealms(@RequestParam("RelayState") String relayState, Model model) throws IOException {
        
        ResponseEntity<String> resp = rest.getForEntity(this.listUrl, String.class);
        
        Map<String, String> map = new HashMap<String, String>();
        
        JsonNode json = mapper.readTree(resp.getBody());
        
        Iterator<JsonNode> nodes = json.getElements();
        
        while (nodes.hasNext()) {
            JsonNode node = nodes.next();
            map.put(node.get("id").getTextValue(), node.get("state").getTextValue());
        }
        
        model.addAttribute("dummy", new HashMap<String, String>());
        model.addAttribute("realms", map);
        model.addAttribute("relayState", relayState);
        
        return "realms";
    }
    
    /**
     * Redirects user to the sso init url given valid id
     * 
     * @param realmId id of the realm
     * @return directive to redirect to sso init page
     */
    @RequestMapping(value = "sso.do", method = { RequestMethod.GET, RequestMethod.POST })
    public String ssoInit(@RequestParam("realmId") String realmId, @RequestParam("RelayState") String relayState) {
        ResponseEntity<String> redirect = rest.getForEntity(this.ssoInitUrl, String.class, realmId);
        return "redirect:" + redirect.getBody() + "&RelayState=" + relayState;
    }
    
    public void setRest(RestTemplate rest) {
        this.rest = rest;
    }
}
