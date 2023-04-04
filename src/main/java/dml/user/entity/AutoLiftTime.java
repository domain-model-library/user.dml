package dml.user.entity;

/**
 * @author zheng chengdong
 */
public interface AutoLiftTime {
    void setId(Object id);

    Object getId();

    boolean timeToLift(long currentTime);
}
