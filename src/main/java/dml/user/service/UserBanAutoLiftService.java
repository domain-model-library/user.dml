package dml.user.service;

import dml.user.entity.AutoLiftTime;
import dml.user.entity.UserBan;
import dml.user.repository.AutoLiftTimeRepository;
import dml.user.repository.UserBanRepository;
import dml.user.service.repositoryset.UserBanAutoLiftServiceRepositorySet;
import dml.user.service.result.CheckAutoLiftTimeAndLiftBanResult;

/**
 * @author zheng chengdong
 */
public class UserBanAutoLiftService {

    public static void setAutoLiftTime(UserBanAutoLiftServiceRepositorySet repositorySet,
                                       Object userId,
                                       AutoLiftTime newAutoLiftTime) {
        AutoLiftTimeRepository<AutoLiftTime, Object> autoLiftTimeRepository = repositorySet.getAutoLiftTimeRepository();
        newAutoLiftTime.setId(userId);
        autoLiftTimeRepository.put(newAutoLiftTime);
    }

    public static CheckAutoLiftTimeAndLiftBanResult checkAutoLiftTimeAndLiftBan(UserBanAutoLiftServiceRepositorySet repositorySet,
                                                                                Object userId,
                                                                                long currentTime) {

        AutoLiftTimeRepository<AutoLiftTime, Object> autoLiftTimeRepository = repositorySet.getAutoLiftTimeRepository();
        UserBanRepository<UserBan, Object> userBanRepository = repositorySet.getUserBanRepository();

        CheckAutoLiftTimeAndLiftBanResult result = new CheckAutoLiftTimeAndLiftBanResult();

        AutoLiftTime autoLiftTime = autoLiftTimeRepository.take(userId);
        if (autoLiftTime.timeToLift(currentTime)) {
            autoLiftTimeRepository.remove(autoLiftTime.getId());
            UserBan removedBan = userBanRepository.remove(autoLiftTime.getId());
            result.setLiftSuccess(true);
            result.setUserBan(removedBan);
            result.setAutoLiftTime(autoLiftTime);
            return result;
        }
        return null;
    }

    public static AutoLiftTime removeAutoLiftTime(UserBanAutoLiftServiceRepositorySet repositorySet,
                                                  Object userId) {

        AutoLiftTimeRepository<AutoLiftTime, Object> autoLiftTimeRepository = repositorySet.getAutoLiftTimeRepository();

        return autoLiftTimeRepository.remove(userId);
    }

}
