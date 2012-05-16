package org.slc.sli.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.component.CustomizationAssemblyFactory;
import org.slc.sli.web.entity.SafeUUID;


/**
 * Data controller returns JSON data. No freemarker templates are called.
 *
 * @author dwu
 */
@Controller
public class DataController {

    private CustomizationAssemblyFactory customizationAssemblyFactory;

    /**
     * Controller for list of students
     *
     */
    @RequestMapping(value = "/service/data/{componentId:[a-zA-Z0-9]+}", method = RequestMethod.GET)
    @ResponseBody public GenericEntity handle(
            @PathVariable final String componentId, @Valid final SafeUUID id, final HttpServletRequest request) {
        return customizationAssemblyFactory.getDataComponent(componentId, id.getId());
    }

    @Autowired
    public void setCustomizedDataFactory(CustomizationAssemblyFactory customizedDataFactory) {
        this.customizationAssemblyFactory = customizedDataFactory;
    }
}
