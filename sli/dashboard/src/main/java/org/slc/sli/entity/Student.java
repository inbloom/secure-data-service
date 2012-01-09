package org.slc.sli.entity;
import com.google.gson.Gson;

/**
 * 
 * TODO: Write Javadoc
 *
 */
public class Student {

    private String id, studentUniqueStateId, sex, economicDisadvantaged;
    
    private NameData name;
    public NameData getName() {
        return name;
    }

    public void setName(NameData name) {
        this.name = name;
    }

    public BirthData getBirthData() {
        return birthData;
    }

    public void setBirthData(BirthData birthData) {
        this.birthData = birthData;
    }

    private BirthData birthData;
    
    private class BirthData {
        String birthDate;
    }
    
    
    public String getFirstName(){
        return name.getFirstName();
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastName(){
        return name.getLastSurname();
    }
    
    private class NameData {
        private String firstName, middleName, lastSurname;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public String getLastSurname() {
            return lastSurname;
        }

        public void setLastSurname(String lastSurname) {
            this.lastSurname = lastSurname;
        }
    }

}
