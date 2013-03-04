package org.slc.sli.datastore.mongo;

import org.slc.sli.model.ModelEntity;


public class TestEntity extends ModelEntity {
        
    private static final long serialVersionUID = -9141924114017594220L;
    
    
    // Attributes
    private String type;
    private String firstName;
    private String lastName;
    private int age;

    
    // Constructors
    public TestEntity() {    
        super();
        this.setType("Test");
    };
    
    public TestEntity(String firstName, String lastName, int age) {
        super();
        this.setType("Test");
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setAge(age);
    }
    
    
    // Methods
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.getBody().put("firstName", this.firstName);
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.getBody().put("lastName", this.lastName);
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
        this.getBody().put("age", this.age);
    }
    
    @Override
    public String toString() {
        return "Test [id=" + this.getId() + ", firstName=" + firstName + ", lastName="
                + lastName + ", age=" + age + "]";
    }
}
