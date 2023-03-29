package dml.user.service.result;

import dml.user.entity.User;
import dml.user.entity.UserSession;

/**
 * @author zheng chengdong
 */
public class OpenidLoginResult {
    private User user;
    private boolean createNewUser;
    private UserSession newUserSession;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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
}
