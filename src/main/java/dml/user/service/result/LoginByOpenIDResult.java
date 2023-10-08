package dml.user.service.result;

import dml.user.entity.UserSession;

/**
 * @author zheng chengdong
 */
public class LoginByOpenIDResult {
    private boolean createNewUser;
    private UserSession newUserSession;
    private String removedUserSessionID;

    public boolean isCreateNewUser() {
        return createNewUser;
    }

    public void setCreateNewUser(boolean createNewUser) {
        this.createNewUser = createNewUser;
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
