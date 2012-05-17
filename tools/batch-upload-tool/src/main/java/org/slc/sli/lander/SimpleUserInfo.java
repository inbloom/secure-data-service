package org.slc.sli.lander;

import com.jcraft.jsch.UserInfo;

public class SimpleUserInfo implements UserInfo {

    private String password = null;

    public SimpleUserInfo(String password) {
        this.password = password;
    }

    public String getPassphrase() {
        return password;
    }

    public String getPassword() {
        return password;
    }

    public boolean promptPassphrase(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean promptPassword(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean promptYesNo(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public void showMessage(String arg0) {
        // TODO Auto-generated method stub

    }

}
