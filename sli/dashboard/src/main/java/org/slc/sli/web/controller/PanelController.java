package org.slc.sli.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.slc.sli.entity.ModelAndViewConfig;
import org.slc.sli.manager.component.CustomizationAssemblyFactory;
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
     * Controller for client side pulls
     *
     */
//    @RequestMapping(value = "/service/component/{componentId:[a-zA-Z0-9]+}/{id:[a-f0-9-]*}", method = RequestMethod.GET)

    @RequestMapping(value = "/service/component/{componentId:[a-zA-Z0-9]+}/{id:[A-Za-z0-9-]*}", method = RequestMethod.GET)
    @ResponseBody public ModelAndViewConfig handle(
            @PathVariable final String componentId, @PathVariable final SafeUUID id, final HttpServletRequest request) {
        return customizationAssemblyFactory.getModelAndViewConfig(componentId, id.getId(), true);
    }

    /**
     * Controller for client side data pulls without id
     *
     */
    @RequestMapping(value = "/service/component/{componentId:[a-zA-Z0-9]+}", method = RequestMethod.GET)
    @ResponseBody public ModelAndViewConfig handleWithoutId(
            @PathVariable final String componentId, final HttpServletRequest request) {
        return customizationAssemblyFactory.getModelAndViewConfig(componentId, null, false);
    }


    @Autowired
    public void setCustomizedDataFactory(CustomizationAssemblyFactory customizedDataFactory) {
        this.customizationAssemblyFactory = customizedDataFactory;
    }
}
