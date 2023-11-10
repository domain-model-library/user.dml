package dml.user.service;

import dml.user.entity.AutoLiftTime;
import dml.user.repository.AutoLiftTimeRepository;
import dml.user.service.repositoryset.UserBanAutoLiftServiceRepositorySet;
import dml.user.service.result.CheckAndRemoveLiftTimeResult;

/**
 * @author zheng chengdong
 */
public class UserBanAutoLiftService {

    public static void setAutoLift(UserBanAutoLiftServiceRepositorySet repositorySet,
                                   Object userId,
                                   AutoLiftTime newAutoLiftTime) {
        AutoLiftTimeRepository<AutoLiftTime, Object> autoLiftTimeRepository = repositorySet.getAutoLiftTimeRepository();
        newAutoLiftTime.setUserID(userId);
        autoLiftTimeRepository.put(newAutoLiftTime);
    }

    public static CheckAndRemoveLiftTimeResult checkToLiftAndUnsetAutoLift(UserBanAutoLiftServiceRepositorySet repositorySet,
                                                                           Object userId,
                                                                           long currentTime) {

        AutoLiftTimeRepository<AutoLiftTime, Object> autoLiftTimeRepository = repositorySet.getAutoLiftTimeRepository();

        CheckAndRemoveLiftTimeResult result = new CheckAndRemoveLiftTimeResult();

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
