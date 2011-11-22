package models;

import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class IdProvider extends Model {

    public String domain;
    public String redirect;

    public IdProvider(String domain, String redirect) {
        this.domain = domain;
        this.redirect = redirect;
    }

}