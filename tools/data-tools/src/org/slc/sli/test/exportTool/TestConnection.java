package org.slc.sli.test.exportTool;

import java.sql.*;

public class TestConnection
{
    public static void main(String[] args)
    {
        DB db = new DB();
        db.dbConnect();
    }
}

class DB
{
    public DB() {}

    public void dbConnect()
    {
        try
        {
            Connection conn = Utility.getConnection();
            System.out.println("connected");

            Statement st = conn.createStatement();
            ResultSet rec = st.executeQuery("SELECT StudentUSI,PersonalTitlePrefixTypeId,FirstName,MiddleName,LastSurname,GenerationCodeSuffixTypeId,MaidenName,PersonalInformationVerificationTypeId,SexTypeId,BirthDate,CityOfBirth,StateOfBirthAbbreviationTypeId,CountryOfBirthCodeTypeId,DateEnteredUS,MultipleBirthStatus,ProfileThumbnail,HispanicLatinoEthnicity,OldEthnicityTypeId,EconomicDisadvantaged,SchoolFoodServicesEligibilityTypeId,LimitedEnglishProficiencyTypeId,DisplacementStatusType,LoginId FROM Student");
            while (rec.next()) {
            	System.out.println(rec.getString("StudentUSI"));
            }
            st.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
};