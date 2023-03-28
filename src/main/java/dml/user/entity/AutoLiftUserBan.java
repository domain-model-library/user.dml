package dml.user.entity;

public interface AutoLiftUserBan {
    void setId(Object id);

    void setAutoLiftTime(long autoLiftTime);

    boolean checkLift(long currentTime);
}
