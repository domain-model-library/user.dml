package dml.user.entity;

/**
 * @author zheng chengdong
 */
public interface UserAccount {
    void setAccount(String account);

    void setPassword(String password);

    boolean verifyPassword(String password);

    Object getUserID();

    void setUserID(Object userID);
}
