package org.slc.sli.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author pwolf
 * 
 */
@Controller
@RequestMapping("/roles")
public class RoleController extends AdminController {
    
    /**
     * Queries the JSON data containing the role info and passes that to the
     * roles.jsp.
     * 
     * @param view
     * @return a view containing the "roleJsonData" object
     * @throws NoSessionException
     */
    @RequestMapping(method = RequestMethod.GET)
    protected ModelAndView getRoles(ModelAndView view, HttpSession session) {
        JsonArray jsonRoles = this.restClient.getRoles(getToken(session));
        
        List<Map<String, Object>> roleList = new ArrayList<Map<String, Object>>();
        
        for (Iterator<?> i = jsonRoles.iterator(); i.hasNext();) {
            JsonObject jsonRole = (JsonObject) i.next();
            Map<String, Object> roleData = new HashMap<String, Object>();
            
            // Name
            String role = jsonRole.get("name").getAsString();
            roleData.put("name", role);
            
            // Aggregate
            @SuppressWarnings("unchecked")
            List<String> rights = (new Gson()).fromJson(jsonRole.get("rights").getAsJsonArray(),
                    (new ArrayList<String>()).getClass());
            roleData.put("aggregate", rights.contains("AGGREGATE_READ"));
            
            // Individual
            roleData.put("individual", getIndividual(role));
            
            // General student data
            String generalRight = null;
            if (rights.contains("READ_GENERAL") && rights.contains("WRITE_GENERAL"))
                generalRight = "R/W";
            else if (rights.contains("READ_GENERAL"))
                generalRight = "R";
            else if (rights.contains("WRITE_GENERAL"))
                generalRight = "W"; // This possible?
            else
                generalRight = "None";
            roleData.put("general", generalRight);
            
            // Restricted student data
            String restrictedRight = null;
            if (rights.contains("READ_RESTRICTED") && rights.contains("WRITE_RESTRICTED"))
                restrictedRight = "R/W";
            else if (rights.contains("READ_RESTRICTED"))
                restrictedRight = "R";
            else if (rights.contains("WRITE_RESTRICTED"))
                restrictedRight = "W"; // This possible?
            else 
                restrictedRight = "None";
            roleData.put("restricted", restrictedRight);
            
            roleList.add(roleData);
        }
        
        view.addObject("roleJsonData", roleList);
        return view;
    }
    
    private String getIndividual(String role) {
        if (role.equalsIgnoreCase("Educator")) {
            return "student enrolled in my sections";
        } else if (role.equalsIgnoreCase("Leader")) {
            return "student enrolled in my district(s) or school(s)";
        } else if (role.equalsIgnoreCase("Aggregate Viewer")) {
            return "None";
        } else if (role.equals("SLI Administrator")) {
            return "N/A";
        } else {
            return "student enrolled in my district(s) or school(s)";
        }
        
    }
    
}
