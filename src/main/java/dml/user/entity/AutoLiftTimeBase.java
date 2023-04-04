package dml.user.entity;

/**
 * @author zheng chengdong
 */
public abstract class AutoLiftTimeBase implements AutoLiftTime {

    protected long liftTime;

    @Override
    public boolean timeToLift(long currentTime) {
        return currentTime >= liftTime;
    }
}
