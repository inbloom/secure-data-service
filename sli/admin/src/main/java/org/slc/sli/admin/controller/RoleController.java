package org.slc.sli.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
     */
    @RequestMapping(method = RequestMethod.GET)
    protected ModelAndView getRoles(ModelAndView view) {
        //TODO: replace null with a real username/token
        JsonArray jsonRoles = this.restClient.getRoles(null);
        List<Map<String, Object>> roleList = new ArrayList<Map<String, Object>>();

        for (Iterator<?> i = jsonRoles.iterator(); i.hasNext();) {
            JsonObject jsonRole = (JsonObject) i.next();
            Map<String, Object> roleData = new HashMap<String, Object>();
            
            //Name
            roleData.put("name", jsonRole.get("name").getAsString());
            
            //Aggregate
            List<String> rights = (new Gson()).fromJson(jsonRole.get("rights").getAsJsonArray(), 
                    (new ArrayList<String>()).getClass());
            roleData.put("aggregate", rights.contains("AGGREGATE_READ"));
            
            //Individual
            roleData.put("individual", "");   //TODO:  Not currently set in data
            
            //General student data
            String generalRight = null;
            if (rights.contains("READ_GENERAL") && rights.contains("WRITE_GENERAL"))
                generalRight = "R/W";
            else if (rights.contains("READ_GENERAL"))
                generalRight = "R";
            else if (rights.contains("WRITE_GENERAL"))
                generalRight = "W"; //This possible?
            roleData.put("general", generalRight);
            
            //Restircted student data
            String restrictedRight = null;
            if (rights.contains("READ_RESTRICTED") && rights.contains("WRITE_RESTRICTED"))
                restrictedRight = "R/W";
            else if (rights.contains("READ_RESTRICTED"))
                restrictedRight = "R";
            else if (rights.contains("WRITE_RESTRICTED"))
                restrictedRight = "W"; //This possible?
            roleData.put("restricted", restrictedRight);
            
            
            roleList.add(roleData);
        }
        
        view.addObject("roleJsonData", roleList);
        return view;
    }

}
