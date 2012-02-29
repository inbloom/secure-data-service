package org.slc.sli.controller;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author dwu
 *
 */
@Controller
@RequestMapping(value = "/service/layout/")
public class GenericController {

    private static final String LAYOUT = "layout/";

    /**
     * Controller for student profile
     * 
     * @param panelIds
     * @return
     */
    @RequestMapping(value = "/student", method = RequestMethod.GET)
    public ModelAndView handleStudentProfile(@RequestParam String id,
                                             @RequestParam("pid") String[] panelIds) {
        
        // set up model map - hardcoded for now
        ModelMap model = new ModelMap();
        Map<String, List<String>> pageMap = new LinkedHashMap<String, List<String>>();
        pageMap.put("Overview", Arrays.asList("csi", "csi"));
        pageMap.put("Attendance and Discipline", Arrays.asList("test", "csi", "test"));
        pageMap.put("Assessments", Arrays.asList("csi", "csi"));
        pageMap.put("Grades and Credits", Arrays.asList("test", "csi", "test"));
        pageMap.put("Advanced Academics", Arrays.asList("test", "csi"));
        
        
        //model.addAttribute("panelIds", panelIds);
        model.addAttribute("pageMap", pageMap);

        return new ModelAndView(LAYOUT + "studentProfile", model);
    }
    
}
