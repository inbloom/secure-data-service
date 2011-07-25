package net.wgen.sli;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;
import java.util.HashMap;
import java.util.Map;
import java.lang.String;

import net.wgen.sli.*;

public class LoginValidator implements Validator {

  private static Map<String, String> users = new HashMap<String, String>();
  static {
      users.put("admin", "admin");
      users.put("dave","coleman");
      users.put("abdul","itani");
      users.put("bill","hazard");
      users.put("jorge","montoya");
  }

 @Override
  public boolean supports(Class clazz) {
  return Login.class.isAssignableFrom(clazz);
  }
  public void validate(Object obj, Errors errors) {
  Login login = (Login) obj;  
  if (login.getUsername() == null || login.getUsername().length() == 0) 
    {
    errors.rejectValue("username", "error.empty.field", "Please Enter User Name");
    }
  else if (!users.keySet().contains(login.getUsername())) 
    {
    errors.rejectValue("username", "unknown.user", "Unknown User");
    }
  if (login.getPassword() == null || login.getPassword().length() == 0) 
    {
    errors.rejectValue("password", "error.empty.field", "Please Enter Password");
    } 
  else if (!login.getPassword().equals(users.get(login.getUsername()))) 
    {
    errors.rejectValue("password", "wrong.password", "Wrong Password");
    }  
  }
}