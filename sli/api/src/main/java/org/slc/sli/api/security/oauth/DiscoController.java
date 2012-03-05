package org.slc.sli.api.security.oauth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.saml.SamlHelper;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for Discovery Service
 * 
 * @author dkornishev
 * 
 */
@Controller
@Scope("request")
@RequestMapping("/oauth")
public class DiscoController {

    private static final Logger LOG = LoggerFactory.getLogger(DiscoController.class);

    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    private MongoAuthorizationCodeServices authCodeService;

    @Autowired
    private SamlHelper saml;

    private EntityService service;

    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName("realm");
        service = def.getService();
    }

    /**
     * Calls api to list available realms and injects into model
     * 
     * @param model
     *            spring injected model
     * @return name of the template to use
     * @throws IOException
     */
    @RequestMapping(value = "authorize", method = RequestMethod.GET)
    public String listRealms(@RequestParam(value = "redirect_uri", required = false) final String relayState,
            @RequestParam(value = "RealmName", required = false) final String realmName, 
            @RequestParam(value = "client_id", required = true) final String clientId, final Model model) throws IOException {

        Object result = SecurityUtil.sudoRun(new SecurityTask<Object>() {
            @Override
            public Object execute() {
                Iterable<String> realmList = service.list(0, 100);
                Map<String, String> map = new HashMap<String, String>();
                for (String realmId : realmList) {
                    EntityBody node = service.get(realmId);
                    map.put(node.get("id").toString(), node.get("state").toString());
                    if (realmName != null && realmName.length() > 0) {
                        if (realmName.equals(node.get("state"))) {
                            try {
                                return ssoInit(node.get("id").toString(), relayState, clientId, model);
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return map;
            }

        });

        if (result instanceof String) {
            return (String) result;
        }

        Map<String, String> map = (Map<String, String>) result;
        model.addAttribute("dummy", new HashMap<String, String>());
        model.addAttribute("realms", map);
        model.addAttribute("relayState", relayState != null ? relayState : "");
        model.addAttribute("clientId", clientId);

        if (relayState == null) {
            model.addAttribute("errorMsg", "No relay state provided.  User won't be redirected back to the application");
        }

        return "realms";
    }

    /**
     * Redirects user to the sso init url given valid id
     * 
     * @param realmId
     *            id of the realm
     * @return directive to redirect to sso init page
     * @throws IOException
     */
    @RequestMapping(value = "sso", method = { RequestMethod.GET, RequestMethod.POST })
    public String ssoInit(@RequestParam(value = "realmId", required = true) final String realmId,
            @RequestParam(value = "RelayState", required = false) String appRelayState, 
            @RequestParam(value = "clientId", required = true) final String clientId, Model model) throws IOException {
        String endpoint = SecurityUtil.sudoRun(new SecurityTask<String>() {
            @Override
            public String execute() {
                EntityBody eb = service.get(realmId);
                if (eb == null) {
                    throw new IllegalArgumentException("Couldn't locate idp for realm: " + realmId);
                }

                @SuppressWarnings("unchecked")
                Map<String, String> idpData = (Map<String, String>) eb.get("idp");
                return (String) idpData.get("redirectEndpoint");
            }
        });
        if (endpoint == null) {
            throw new IllegalArgumentException("Realm " + realmId + " doesn't have an endpoint");
        }

        // {messageId,encodedSAML}
        Pair<String, String> tuple = saml.createSamlAuthnRequestForRedirect(endpoint);

        authCodeService.create(clientId, tuple.getLeft());
        LOG.debug("redirecting to: " + endpoint);
        return "redirect:" + endpoint + "?SAMLRequest=" + tuple.getRight();
    }

}
