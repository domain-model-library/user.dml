package dml.user.service.result;

import dml.user.entity.UserSession;

/**
 * @author zheng chengdong
 */
public class LoginByAccountPasswordResult {
    private boolean noAccount;
    private boolean incorrectPassword;
    private boolean loginSuccess;
    private UserSession newUserSession;
    private String removedUserSessionID;

    public boolean isNoAccount() {
        return noAccount;
    }

    public void setNoAccount(boolean noAccount) {
        this.noAccount = noAccount;
    }

    public boolean isIncorrectPassword() {
        return incorrectPassword;
    }

    public void setIncorrectPassword(boolean incorrectPassword) {
        this.incorrectPassword = incorrectPassword;
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

    public void setLoginSuccess(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }

    public UserSession getNewUserSession() {
        return newUserSession;
    }

    public void setNewUserSession(UserSession newUserSession) {
        this.newUserSession = newUserSession;
    }

    public void setRemovedUserSessionID(String removedUserSessionID) {
        this.removedUserSessionID = removedUserSessionID;
    }

    public String getRemovedUserSessionID() {
        return removedUserSessionID;
    }
}
