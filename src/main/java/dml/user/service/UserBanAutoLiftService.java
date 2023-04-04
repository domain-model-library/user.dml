package dml.user.service;

import dml.user.entity.AutoLiftTime;
import dml.user.repository.AutoLiftTimeRepository;
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

    public static AutoLiftTime checkAndLift(UserBanAutoLiftServiceRepositorySet repositorySet,
                                            Object userId,
                                            long currentTime) {
        AutoLiftTimeRepository<AutoLiftTime, Object> autoLiftTimeRepository = repositorySet.getAutoLiftTimeRepository();
        AutoLiftTime autoLiftTime = autoLiftTimeRepository.take(userId);
        if (autoLiftTime.timeToLift(currentTime)) {
            return autoLiftTimeRepository.remove(autoLiftTime.getId());
        }
        return null;
    }

}
