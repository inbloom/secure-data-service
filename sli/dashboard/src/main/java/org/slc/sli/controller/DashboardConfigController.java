package org.slc.sli.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slc.sli.client.APIClient;
import org.slc.sli.entity.CustomConfig;
import org.slc.sli.entity.CustomConfigString;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.UserEdOrgManager;
import org.slc.sli.util.CustomConfigValidator;
import org.slc.sli.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


/**
 * 
 * DashboardConfigController
 * This controller handles the dashboard config pages which are only accessible by IT Admins of a District.
 *
 */
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

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new CustomConfigValidator());
    }
    
    @Autowired
    public void setUserEdOrgManager(UserEdOrgManager userEdOrgManager) {
        this.userEdOrgManager = userEdOrgManager;
    }
    
    public UserEdOrgManager getUserEdOrgManager() {
        return this.userEdOrgManager;
    }
    
    /**
     * Generic layout handler
     *
     * @param id
     * @param request
     * @return
     * @throws IllegalAccessException 
     */
    @RequestMapping(value = CONFIG_URL, method = RequestMethod.GET)
    public ModelAndView getConfig(HttpServletRequest request) throws IllegalAccessException {
        ModelMap model = new ModelMap();
        
        String token = SecurityUtil.getToken();
        GenericEntity staffEntity = getApiClient().getStaffInfo(token);
        
        boolean isAdmin = isAdmin(staffEntity);
        if (isAdmin) {

            String edOrgSliId = (String) staffEntity.get("edOrgSliId");
            
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
            
            throw new IllegalAccessException("Access Denied");
        }
    }
    
    @RequestMapping(value = CONFIG_SAVE_URL, method = RequestMethod.POST)
    @ResponseBody public String saveConfig(@RequestParam(required = true) String configString) {
        
        // Formalize JSON from more flexible GSON format used in Dashboard configuration files
        configString = CustomConfig.formalizeJson(configString);
        
        CustomConfigString customConfigString = new CustomConfigString(configString);
        DataBinder binder = new DataBinder(customConfigString);
        binder.setValidator(new CustomConfigValidator());
        
        // bind to the target object
        //binder.bind(propertyValues);
        
        // validate the target object
        binder.validate();
        
        // get BindingResult that includes any validation errors
        BindingResult results = binder.getBindingResult();
        if (results.hasErrors()) {
            return "Invalid Input";
        }

        String token = SecurityUtil.getToken();
        GenericEntity staffEntity = getApiClient().getStaffInfo(token);
        String edOrgSliId = (String) staffEntity.get("edOrgSliId");
        
       
        boolean isAdmin = isAdmin(staffEntity);
        if (isAdmin) {
            getApiClient().putEdOrgCustomData(token, edOrgSliId, configString);
            return "Success";
        } else {
            return "Permission Denied";
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
