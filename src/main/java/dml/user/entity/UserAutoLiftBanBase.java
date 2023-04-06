package dml.user.entity;

/**
 * @author zheng chengdong
 */
public abstract class UserAutoLiftBanBase implements UserAutoLiftBan {
    protected long liftTime;

    @Override
    public boolean timeToLift(long currentTime) {
        return currentTime >= liftTime;
    }
}
