package org.slc.sli.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.component.CustomizationAssemblyFactory;


/**
 * Layout controller for all types of requests. 
 * NOTE: This controller was introduced after the student list and app selector controllers. 
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
    @RequestMapping(value = "/service/data/", method = RequestMethod.GET)
    @ResponseBody public GenericEntity handle(
            @RequestParam final String componentId, @RequestParam final String sectionId, final HttpServletRequest request) {
        return customizationAssemblyFactory.getDataComponent(componentId, sectionId);
    }
    
    @Autowired
    public void setCustomizedDataFactory(CustomizationAssemblyFactory customizedDataFactory) {
        this.customizationAssemblyFactory = customizedDataFactory;
    }
}
