package dml.user.service;

import dml.user.entity.AutoLiftTime;
import dml.user.repository.AutoLiftTimeRepository;
import dml.user.service.repositoryset.UserBanAutoLiftServiceRepositorySet;
import dml.user.service.result.CheckToLiftAndUnsetAutoLiftResult;

/**
 * @author zheng chengdong
 */
public class UserBanAutoLiftService {

    public static void setAutoLift(UserBanAutoLiftServiceRepositorySet repositorySet,
                                   Object userId,
                                   long liftTime) {
        AutoLiftTimeRepository autoLiftTimeRepository = repositorySet.getAutoLiftTimeRepository();
        AutoLiftTime newAutoLiftTime = new AutoLiftTime();
        newAutoLiftTime.setUserID(userId);
        newAutoLiftTime.setLiftTime(liftTime);
        autoLiftTimeRepository.put(newAutoLiftTime);
    }

    public static CheckToLiftAndUnsetAutoLiftResult checkToLiftAndUnsetAutoLift(UserBanAutoLiftServiceRepositorySet repositorySet,
                                                                                Object userId,
                                                                                long currentTime) {

        AutoLiftTimeRepository autoLiftTimeRepository = repositorySet.getAutoLiftTimeRepository();

        CheckToLiftAndUnsetAutoLiftResult result = new CheckToLiftAndUnsetAutoLiftResult();

        AutoLiftTime autoLiftTime = autoLiftTimeRepository.take(userId);
        if (autoLiftTime.timeToLift(currentTime)) {
            autoLiftTimeRepository.remove(autoLiftTime.getUserID());
            result.setToLift(true);
            result.setAutoLiftTime(autoLiftTime);
            return result;
        }
        return result;
    }

}
