package dml.user.entity;

/**
 * @author zheng chengdong
 */
public class OpenIDUserBind {
    private String openID;
    private Object userID;

    public String getOpenID() {
        return openID;
    }

    public void setOpenID(String openID) {
        this.openID = openID;
    }

    public Object getUserID() {
        return userID;
    }

    public void setUserID(Object userID) {
        this.userID = userID;
    }
}
