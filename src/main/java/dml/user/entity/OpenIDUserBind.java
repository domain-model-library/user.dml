package dml.user.entity;

/**
 * @author zheng chengdong
 */
public interface OpenIDUserBind {
    void setOpenID(String openID);

    void setUserID(Object userID);

    Object getUserID();
}
