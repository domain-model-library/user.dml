package dml.user.entity;

/**
 * @author zheng chengdong
 */
public interface UserAutoLiftBan {
    void setId(Object id);

    Object getId();

    boolean timeToLift(long currentTime);
}
