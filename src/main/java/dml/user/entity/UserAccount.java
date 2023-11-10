package dml.user.entity;

/**
 * @author zheng chengdong
 */
public interface UserAccount {
    void setAccount(String account);

    boolean verifyPassword(String password);

    User getUser();

    void setUser(User user);
}
