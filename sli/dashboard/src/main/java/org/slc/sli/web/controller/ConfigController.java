package org.slc.sli.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.google.gson.GsonBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.entity.ConfigMap;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.ConfigManager;
import org.slc.sli.manager.UserEdOrgManager;
import org.slc.sli.util.Constants;
import org.slc.sli.util.SecurityUtil;


/**
 *
 * DashboardConfigController
 * This controller handles the dashboard config pages which are only accessible by IT Admins of a District.
 *
 */
@Controller
public class ConfigController extends GenericLayoutController {
    private static final String DASHBOARD_CONFIG_FTL = "dashboard_config";
    private static final String CONFIG_URL = "/service/config";
    private static final String CONFIG_SAVE_URL = "/service/config/ajaxSave";

    private UserEdOrgManager userEdOrgManager;
    private ConfigManager configManager;

    @Autowired
    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
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
     * @throws IllegalAccessException
     */
    @RequestMapping(value = CONFIG_URL, method = RequestMethod.GET)
    public ModelAndView getConfig(HttpServletRequest request) throws IllegalAccessException {
        ModelMap model = new ModelMap();

        String token = SecurityUtil.getToken();
        GenericEntity staffEntity = userEdOrgManager.getStaffInfo(token);

        Boolean isAdmin = (Boolean) staffEntity.get(Constants.ATTR_CREDENTIALS_CODE_FOR_IT_ADMIN);
        if (isAdmin != null && isAdmin.booleanValue()) {
            ConfigMap configMap = configManager.getCustomConfig(token, userEdOrgManager.getUserEdOrg(token));

            if (configMap != null) {
                model.addAttribute("configJSON", new GsonBuilder().create().toJson(configMap));
            } else {
                model.addAttribute("configJSON", "");
            }

            addHeaderFooter(model);
            setContextPath(model, request);
            return new ModelAndView(DASHBOARD_CONFIG_FTL, model);
        }
        throw new IllegalAccessException("Access Denied");
    }

    @RequestMapping(value = CONFIG_SAVE_URL, method = RequestMethod.POST)
    @ResponseBody public String saveConfig(@RequestBody @Valid ConfigMap configMap) {
        try {
            putCustomConfig(configMap);
        } catch (RuntimeException re) {
            logger.error("Error saving config", re);
            return "Permission Denied";
        }
        return "Success";
    }

    public void putCustomConfig(ConfigMap configMap) {
        String token = SecurityUtil.getToken();
        configManager.putCustomConfig(token, userEdOrgManager.getUserEdOrg(token), configMap);
    }
}
