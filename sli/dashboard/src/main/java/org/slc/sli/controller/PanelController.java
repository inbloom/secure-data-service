package org.slc.sli.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.ModelAndViewConfig;
import org.slc.sli.manager.component.CustomizationAssemblyFactory;
import org.slc.sli.util.Constants;


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
    @RequestMapping(value = "/service/component/{componentId}/{id}", method = RequestMethod.GET)
    @ResponseBody public GenericEntity handle(
            @PathVariable final String componentId, @PathVariable final String id, final HttpServletRequest request) {
        // is it a hack
        ModelAndViewConfig mac = customizationAssemblyFactory.getModelAndViewConfig(componentId, id, true);
        GenericEntity ge = new GenericEntity();
        ge.put(Constants.MM_KEY_DATA, mac.getData().get(componentId));
        ge.put(Constants.MM_KEY_VIEW_CONFIG, mac.getComponentViewConfigMap().get(componentId));
        return ge;
    }

    @Autowired
    public void setCustomizedDataFactory(CustomizationAssemblyFactory customizedDataFactory) {
        this.customizationAssemblyFactory = customizedDataFactory;
    }
}
