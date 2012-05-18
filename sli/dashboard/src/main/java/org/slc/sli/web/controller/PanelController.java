package org.slc.sli.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.ModelAndViewConfig;
import org.slc.sli.manager.component.CustomizationAssemblyFactory;
import org.slc.sli.util.Constants;
import org.slc.sli.web.entity.SafeUUID;


/**
 * Data controller returns JSON data. No freemarker templates are called.
 *
 * @author dwu
 */
@Controller
public class PanelController {

    private CustomizationAssemblyFactory customizationAssemblyFactory;

    /**
     * Controller for list of students
     *
     */
    @RequestMapping(value = "/service/component/{componentId:[a-zA-Z0-9]+}/{id:[a-f0-9-]+}", method = RequestMethod.GET)
    @ResponseBody public GenericEntity handle(
            @PathVariable final String componentId, @PathVariable final SafeUUID id, final HttpServletRequest request) {
        // is it a hack
        ModelAndViewConfig mac = customizationAssemblyFactory.getModelAndViewConfig(componentId, id.getId(), true);
        Config config = mac.getComponentViewConfigMap().get(componentId);
        GenericEntity ge = new GenericEntity();
        if (config != null && config.getData() != null) {
            ge.put(Constants.MM_KEY_DATA, mac.getData().get(config.getData().getCacheKey()));
        }
        ge.put(Constants.MM_KEY_VIEW_CONFIG, config);
        return ge;
    }

    @Autowired
    public void setCustomizedDataFactory(CustomizationAssemblyFactory customizedDataFactory) {
        this.customizationAssemblyFactory = customizedDataFactory;
    }
}
