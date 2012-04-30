package org.slc.sli.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slc.sli.client.APIClient;
import org.slc.sli.entity.CustomConfig;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.UserEdOrgManager;
import org.slc.sli.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardConfigController extends GenericLayoutController {
	private static final Logger LOG = LoggerFactory.getLogger(DashboardConfigController.class);
	private static final String DASHBOARD_CONFIG_FTL = "dashboard_config";
    private static final String CONFIG_URL = "/service/config";
    private static final String CONFIG_SAVE_URL = "/service/config/ajaxSave";
    private static final String CREDENTIALS_CODE_FOR_IT_ADMIN = "IT Admin";
    private static final String CREDENTIALS_LIST_ATTRIBUTE = "credentials";
    private static final String CREDENTIAL_FIELD_ATTRIBUTE = "credentialField";
    private static final String CREDENTIAL_CODE_ATTRIBUTE = "codeValue";
    private static final String EDORG_SLI_ID_ATTRIBUTE = "edOrgSliId";
    private UserEdOrgManager userEdOrgManager;
    private APIClient apiClient;
    
    @Autowired
    public void setApiClient(APIClient apiClient) {
        this.apiClient = apiClient;
    }
    
    public APIClient getApiClient() {
        return apiClient;
    }

    @Autowired
    public void setUserEdOrgManager(UserEdOrgManager userEdOrgManager) {
        this.userEdOrgManager = userEdOrgManager;
    }
    
    /**
     * Generic layout handler
     *
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = CONFIG_URL, method = RequestMethod.GET)
    public ModelAndView getConfig(HttpServletRequest request) {
        ModelMap model = new ModelMap();
        
        String token = SecurityUtil.getToken();
        GenericEntity staffEntity = getApiClient().getStaffInfo(token);
        String edOrgSliId = (String) staffEntity.get("edOrgSliId");
        boolean isAdmin = isAdmin(staffEntity);
        if (isAdmin) {
            CustomConfig customConfig = getApiClient().getEdOrgCustomData(token, edOrgSliId);
            if (customConfig != null) {
                model.addAttribute("configJSON", customConfig.toJson());
            } else {
                model.addAttribute("configJSON", "");
            }
            
            addHeaderFooter(model);
            setContextPath(model, request);
            return new ModelAndView(DASHBOARD_CONFIG_FTL, model);
        } else {
            
            // TODO - Redirect to exception page for unauthorized Admin page access
            return null;
        }
    }
    
    @RequestMapping(value = CONFIG_SAVE_URL, method = RequestMethod.POST)
    @ResponseBody public String saveConfig(@RequestParam(required = true) String customConfigJSON, HttpServletRequest request) {
        
        String token = SecurityUtil.getToken();
        GenericEntity staffEntity = getApiClient().getStaffInfo(token);
        String edOrgSliId = (String) staffEntity.get("edOrgSliId");
        boolean isAdmin = isAdmin(staffEntity);
        if (isAdmin) {
            if ((customConfigJSON != null) && (customConfigJSON.length() > 0)) {
                getApiClient().putEdOrgCustomData(token, edOrgSliId, customConfigJSON);
            }
            return "Success";
        } else {
            
            // TODO - Redirect to exception page for unauthorized Admin page access
            return null;
        }
    }

    /**
     * Determine if user has admin credentials as derived from the staff entity.
     * 
     * @param The staff entity
     * @return The user's admin credentials indication
     */
    protected boolean isAdmin(GenericEntity staffEntity) {
        boolean isAdmin = false;
        
        if (staffEntity != null) {
            List credentialsList = (List) staffEntity.get(CREDENTIALS_LIST_ATTRIBUTE);
            if ((credentialsList != null) && (credentialsList.size() > 0)) {
                Map credentials = (Map) credentialsList.get(0);
                if (credentials != null) {
                    List<Map> credentialFieldsList = (List<Map>) credentials.get(CREDENTIAL_FIELD_ATTRIBUTE);
                    if ((credentialFieldsList != null) && (credentialFieldsList.size() > 0)) {
                        for (Map credentialField : credentialFieldsList) {
                            String credentialCode = (String) credentialField.get(CREDENTIAL_CODE_ATTRIBUTE);
                            if ((credentialCode != null)
                                    && (credentialCode.equalsIgnoreCase(CREDENTIALS_CODE_FOR_IT_ADMIN))) {
                                isAdmin = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        return isAdmin;
    }

}
