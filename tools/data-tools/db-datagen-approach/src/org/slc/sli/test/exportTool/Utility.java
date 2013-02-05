/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.test.exportTool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class Utility {
    private static String connectionString ="jdbc:jtds:sqlserver://10.81.1.33:1433/edfi;catalog=edfi";
    private static String userName = "sa";
    private static String password = "benerator";
    private static boolean propertiesLoaded = false;

    public static void getProperties() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("exportTool.properties"));
            connectionString = prop.getProperty("connectionString");
            userName = prop.getProperty("userName");
            password = prop.getProperty("password");

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        propertiesLoaded = true;
    }

    public static Connection getConnection() {
        if (!propertiesLoaded)
            getProperties();

        Connection conn = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conn = DriverManager.getConnection(connectionString, userName, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    // if resultSet does not contain any record, we return null instead
    public static ResultSet getResultSet(Connection conn, String query) {
        ResultSet rs = null;
        try {
            System.out.println(query);
            System.out.println(conn);
            Statement st = conn.createStatement();
            rs = st.executeQuery(query);
            if (!rs.next())
                rs = null;
        } catch (SQLException e) {
            e.printStackTrace();
            rs = null;
        }
        return rs;
    }

    public static Calendar convertStringToCalendar(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        Calendar cal = Calendar.getInstance();
        try {
            Date date = (Date) sdf.parse(s);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }

    public static String replace(String original, String regex, String replacement) {
        if (replacement == null)
            replacement = "";
        return original.replaceAll(regex, replacement);
    }

    public static String generateXMLbasedOnTemplate(ResultSet resultSet, String template, List<String> keys) {
        String xml = template;
        try {
            for (String key : keys) {
                xml = Utility.replace(xml, "--"+key+"--", resultSet.getString(key));
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return xml;
    }

    private static String generateEmptyXMLbasedOnTemplate(String template, List<String> keys) {
        String xml = template;
        for (String key : keys) {
            xml = Utility.replace(xml, "--"+key+"--", null);
        }
        return xml;
    }

    public static String generateEmbeddedXMLbasedOnTemplate(ResultSet mainSet, List<String> joinKeys, ResultSet embeddedSet, String embeddedTemplate, List<String> keys) {
        StringBuilder embeddedXml = new StringBuilder("");
        boolean hasOne = false;
        if (embeddedSet != null) {
            try {
                while (foundMatch(mainSet, joinKeys, embeddedSet)) {
                    String temp = generateXMLbasedOnTemplate(embeddedSet, embeddedTemplate, keys);
                    embeddedXml.append(temp);
                    hasOne = true;
                    embeddedSet.next();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                embeddedSet = null;
            }
        }
        if (!hasOne) {
            String temp = generateEmptyXMLbasedOnTemplate(embeddedTemplate, keys);
            embeddedXml.append(temp);
            }

        return embeddedXml.toString();
    }

    private static boolean foundMatch(ResultSet mainSet, List<String> joinKeys, ResultSet embeddedSet) {
        for (String key : joinKeys) {
            try {
                if (!embeddedSet.getString(key).equals(mainSet.getString(key)))
                    return false;
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
