package models;

import javax.persistence.*;

import play.data.validation.Required;
import play.db.jpa.*;

@Entity
public class IdProvider extends Model {

    @Required
    public String name;
    @Required
    public String domain;
    @Required
    public String redirect;

    public IdProvider(String name, String domain, String redirect) {
        this.name = name;
        this.domain = domain;
        this.redirect = redirect;
    }
    
    public String toString() {
        return name + " - " + domain;
    }

}