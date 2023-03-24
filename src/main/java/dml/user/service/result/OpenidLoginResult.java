package dml.user.service.result;

import dml.user.entity.User;
import dml.user.entity.UserSession;

/**
 * @author zheng chengdong
 */
public class OpenidLoginResult {
    private User user;
    private boolean createNewUser;
    private UserSession currentUserSession;
    private UserSession removedUserSession;

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

    public UserSession getCurrentUserSession() {
        return currentUserSession;
    }

    public void setCurrentUserSession(UserSession currentUserSession) {
        this.currentUserSession = currentUserSession;
    }

    public UserSession getRemovedUserSession() {
        return removedUserSession;
    }

    public void setRemovedUserSession(UserSession removedUserSession) {
        this.removedUserSession = removedUserSession;
    }
}
