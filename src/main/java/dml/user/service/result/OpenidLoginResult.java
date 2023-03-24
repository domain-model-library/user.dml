package dml.user.service.result;

import dml.user.entity.Session;
import dml.user.entity.User;

/**
 * @author zheng chengdong
 */
public class OpenidLoginResult {
    private User user;
    private boolean createNewUser;
    private Session currentSession;
    private Session removedSession;

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

    public Session getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(Session currentSession) {
        this.currentSession = currentSession;
    }

    public Session getRemovedSession() {
        return removedSession;
    }

    public void setRemovedSession(Session removedSession) {
        this.removedSession = removedSession;
    }
}
