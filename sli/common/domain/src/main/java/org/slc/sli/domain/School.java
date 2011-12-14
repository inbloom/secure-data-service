package org.slc.sli.domain;

import javax.xml.bind.annotation.XmlRootElement;

import org.slc.sli.domain.enums.AdministrativeFundingControlType;
import org.slc.sli.domain.enums.CharterStatusType;
import org.slc.sli.domain.enums.MagnetSpecialProgramEmphasisSchoolType;
import org.slc.sli.domain.enums.OperationalStatusType;
import org.slc.sli.domain.enums.SchoolType;
import org.slc.sli.domain.enums.TitleIPartASchoolDesignationType;

/**
 * School Entity.
 * 
 * Entities should try to be as vanilla as possible. Ideally, persistence annotations and
 * marshalling hints to JSON and XML libraries will be in packages other than
 * domain.
 * 
 * TODO: Switch from String to Enums, Add missing data
 * 
 * @author rfarris@wgen.net
 * 
 */
@XmlRootElement
public class School {
    
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1675478766768L;
    
    private Integer schoolId;
    private String fullName;
    private String shortName;
    private String stateOrganizationId;
    private String webSite;
    
    private SchoolType schoolType;
    private CharterStatusType charterStatus;
    private TitleIPartASchoolDesignationType titleIPartASchoolDesignation;
    private MagnetSpecialProgramEmphasisSchoolType magnetSpecialProgramEmphasisSchool;
    private AdministrativeFundingControlType administrativeFundingControl;
    private OperationalStatusType operationalStatus;
    
    public School() {
        // ensure default constructor for JAXB
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((schoolId == null) ? 0 : schoolId.hashCode());
        return result;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        School other = (School) obj;
        if (schoolId == null) {
            if (other.schoolId != null)
                return false;
        } else if (!schoolId.equals(other.schoolId))
            return false;
        return true;
    }
    
    /**
     * @return the schoolId
     */
    public Integer getSchoolId() {
        return schoolId;
    }
    
    /**
     * @param schoolId
     *            the schoolId to set
     */
    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }
    
    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }
    
    /**
     * @param fullName
     *            the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    /**
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }
    
    /**
     * @param shortName
     *            the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    
    /**
     * @return the stateOrganizationId
     */
    public String getStateOrganizationId() {
        return stateOrganizationId;
    }
    
    /**
     * @param stateOrganizationId
     *            the stateOrganizationId to set
     */
    public void setStateOrganizationId(String stateOrganizationId) {
        this.stateOrganizationId = stateOrganizationId;
    }
    
    /**
     * @return the webSite
     */
    public String getWebSite() {
        return webSite;
    }
    
    /**
     * @param webSite
     *            the webSite to set
     */
    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }
    
    /**
     * @return the schoolType
     */
    public SchoolType getSchoolType() {
        return schoolType;
    }
    
    /**
     * @param schoolType
     *            the schoolType to set
     */
    public void setSchoolType(SchoolType schoolType) {
        this.schoolType = schoolType;
    }
    
    /**
     * @return the charterStatus
     */
    public CharterStatusType getCharterStatus() {
        return charterStatus;
    }
    
    /**
     * @param charterStatus
     *            the charterStatus to set
     */
    public void setCharterStatus(CharterStatusType charterStatus) {
        this.charterStatus = charterStatus;
    }
    
    /**
     * @return the titleIPartASchoolDesignation
     */
    public TitleIPartASchoolDesignationType getTitleIPartASchoolDesignation() {
        return titleIPartASchoolDesignation;
    }
    
    /**
     * @param titleIPartASchoolDesignation
     *            the titleIPartASchoolDesignation to set
     */
    public void setTitleIPartASchoolDesignation(TitleIPartASchoolDesignationType titleIPartASchoolDesignation) {
        this.titleIPartASchoolDesignation = titleIPartASchoolDesignation;
    }
    
    /**
     * @return the magnetSpecialProgramEmphasisSchool
     */
    public MagnetSpecialProgramEmphasisSchoolType getMagnetSpecialProgramEmphasisSchool() {
        return magnetSpecialProgramEmphasisSchool;
    }
    
    /**
     * @param magnetSpecialProgramEmphasisSchool
     *            the magnetSpecialProgramEmphasisSchool to set
     */
    public void setMagnetSpecialProgramEmphasisSchool(
            MagnetSpecialProgramEmphasisSchoolType magnetSpecialProgramEmphasisSchool) {
        this.magnetSpecialProgramEmphasisSchool = magnetSpecialProgramEmphasisSchool;
    }
    
    /**
     * @return the administrativeFundingControl
     */
    public AdministrativeFundingControlType getAdministrativeFundingControl() {
        return administrativeFundingControl;
    }
    
    /**
     * @param administrativeFundingControl
     *            the administrativeFundingControl to set
     */
    public void setAdministrativeFundingControl(AdministrativeFundingControlType administrativeFundingControl) {
        this.administrativeFundingControl = administrativeFundingControl;
    }
    
    /**
     * @return the operationalStatus
     */
    public OperationalStatusType getOperationalStatus() {
        return operationalStatus;
    }
    
    /**
     * @param operationalStatus
     *            the operationalStatus to set
     */
    public void setOperationalStatus(OperationalStatusType operationalStatus) {
        this.operationalStatus = operationalStatus;
    }
    
}
