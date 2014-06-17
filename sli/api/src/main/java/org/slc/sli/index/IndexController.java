package org.slc.sli.index;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


/**
 * Created by lloydengebretsen on 5/14/14.
 */
@Controller
@RequestMapping("/index")
public class IndexController {

    @Value("${bootstrap.app.admin.url}")
    protected String adminUrl;

    @Value("${bootstrap.app.databrowser.url}")
    protected String dataBrowserUrl;

    @Value("${bootstrap.app.dashboard.url}")
    protected String dashboardUrl;

    @Value("${api.server.url}")
    protected String apiUrl;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index(){

        ModelAndView model = new ModelAndView("index");
        model.addObject("adminUrl", adminUrl);
        model.addObject("dataBrowserUrl", dataBrowserUrl);
        model.addObject("dashboardUrl", dashboardUrl);
        model.addObject("apiUrl", apiUrl);

        return model;
    }
}
