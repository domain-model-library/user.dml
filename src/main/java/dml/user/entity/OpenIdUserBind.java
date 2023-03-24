package dml.user.entity;

/**
 * @author zheng chengdong
 */
public interface OpenIdUserBind {
    void setId(String id);

    void setUser(User user);

    User getUser();
}
