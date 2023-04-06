package dml.user.service;

import dml.user.entity.AutoLiftTime;
import dml.user.entity.UserBan;
import dml.user.repository.AutoLiftTimeRepository;
import dml.user.repository.UserBanRepository;
import dml.user.service.repositoryset.UserBanAutoLiftServiceRepositorySet;

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

    public static UserBan checkAndLift(UserBanAutoLiftServiceRepositorySet repositorySet,
                                       Object userId,
                                       long currentTime) {
        AutoLiftTimeRepository<AutoLiftTime, Object> autoLiftTimeRepository = repositorySet.getAutoLiftTimeRepository();
        UserBanRepository<UserBan, Object> userBanRepository = repositorySet.getUserBanRepository();

        AutoLiftTime autoLiftTime = autoLiftTimeRepository.take(userId);
        if (autoLiftTime.timeToLift(currentTime)) {
            autoLiftTimeRepository.remove(autoLiftTime.getId());
            return userBanRepository.remove(autoLiftTime.getId());
        }
        return null;
    }

}
