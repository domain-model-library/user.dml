package dml.user.entity;

/**
 * @author zheng chengdong
 */
public interface UserLoginState {
    void setId(Object id);

    Object getId();

    UserSession getCurrentUserSession();

    void setCurrentUserSession(UserSession currentUserSession);
}
