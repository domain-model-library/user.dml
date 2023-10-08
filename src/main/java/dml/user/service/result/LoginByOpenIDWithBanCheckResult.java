package dml.user.service.result;

import dml.user.entity.UserSession;

public class LoginByOpenIDWithBanCheckResult {
    private boolean ban;
    private boolean createNewUser;
    private UserSession newUserSession;
    private String removedUserSessionID;

    public void setBan(boolean ban) {
        this.ban = ban;
    }

    public boolean isBan() {
        return ban;
    }

    public boolean isCreateNewUser() {
        return createNewUser;
    }

    public void setCreateNewUser(boolean createNewUser) {
        this.createNewUser = createNewUser;
    }

    public void setNewUserSession(UserSession newUserSession) {
        this.newUserSession = newUserSession;
    }

    public UserSession getNewUserSession() {
        return newUserSession;
    }

    public void setRemovedUserSessionID(String removedUserSessionID) {
        this.removedUserSessionID = removedUserSessionID;
    }

    public String getRemovedUserSessionID() {
        return removedUserSessionID;
    }
}
