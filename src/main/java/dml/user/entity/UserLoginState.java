package dml.user.entity;

/**
 * @author zheng chengdong
 */
public interface UserLoginState {
    void setId(Object id);

    Object getId();

    Session getCurrentSession();

    void setCurrentSession(Session currentSession);
}
