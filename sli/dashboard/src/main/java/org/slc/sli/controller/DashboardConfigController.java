package org.slc.sli.controller;

import javax.servlet.http.HttpServletRequest;

import org.slc.sli.entity.CustomConfig;
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
    private UserEdOrgManager userEdOrgManager;
    
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
    	String edOrgId = userEdOrgManager.getUserEdOrg(SecurityUtil.getToken()).getDistrictId();
    	CustomConfig customConfig = userEdOrgManager.getCustomConfig(SecurityUtil.getToken());
    	
    	if (customConfig != null) {
    		model.addAttribute("configJSON", customConfig.toJson()); 
    	} else {
    		model.addAttribute("configJSON", ""); 
    	}
    	
    	addHeaderFooter(model);
    	setContextPath(model, request);
        return new ModelAndView(DASHBOARD_CONFIG_FTL, model);
    }
    
    @RequestMapping(value = CONFIG_SAVE_URL, method = RequestMethod.POST)
    @ResponseBody public String saveConfig(@RequestParam(required = true) String customConfigJSON, HttpServletRequest request) {
    	
    	userEdOrgManager.putCustomConfig(SecurityUtil.getToken(), customConfigJSON);
    	return "Success";
    }
}
