package org.slc.sli.test.exportTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {
    private static String connectionString = "jdbc:jtds:sqlserver://10.211.55.3:1433/edfi;catalog=edfi";
    private static String userName = "sa";
    private static String password = "benerator";

    public static Connection getConnection() {
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

    public static ResultSet getResultSet(Connection conn, String query) {
        ResultSet rs = null;
        try {
            Statement st = conn.createStatement();
            rs = st.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
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
}
