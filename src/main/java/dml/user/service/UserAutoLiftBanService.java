package dml.user.service;

import dml.user.entity.UserAutoLiftBan;
import dml.user.repository.UserAutoLiftBanRepository;
import dml.user.service.repositoryset.UserAutoLiftBanServiceRepositorySet;
import dml.user.service.result.CheckBanAndLiftResult;

/**
 * @author zheng chengdong
 */
public class UserAutoLiftBanService {

    public static void banUser(UserAutoLiftBanServiceRepositorySet repositorySet,
                               Object userId,
                               UserAutoLiftBan newUserAutoLiftBan) {
        UserAutoLiftBanRepository<UserAutoLiftBan, Object> userAutoLiftBanRepository = repositorySet.getUserAutoLiftBanRepository();

        newUserAutoLiftBan.setId(userId);
        userAutoLiftBanRepository.put(newUserAutoLiftBan);
    }

    public static UserAutoLiftBan liftBan(UserAutoLiftBanServiceRepositorySet repositorySet,
                                          Object userId) {
        UserAutoLiftBanRepository<UserAutoLiftBan, Object> userAutoLiftBanRepository = repositorySet.getUserAutoLiftBanRepository();
        return userAutoLiftBanRepository.remove(userId);
    }

    public static CheckBanAndLiftResult checkBanAndLift(UserAutoLiftBanServiceRepositorySet repositorySet,
                                                        Object userId,
                                                        long currentTime) {
        UserAutoLiftBanRepository<UserAutoLiftBan, Object> userAutoLiftBanRepository = repositorySet.getUserAutoLiftBanRepository();

        CheckBanAndLiftResult result = new CheckBanAndLiftResult();
        UserAutoLiftBan userAutoLiftBan = userAutoLiftBanRepository.take(userId);
        if (userAutoLiftBan == null) {
            result.setBanned(false);
            return result;
        }
        if (userAutoLiftBan.timeToLift(currentTime)) {
            result.setBanned(false);
            result.setAutoLiftBan(userAutoLiftBanRepository.remove(userId));
            return result;
        }
        result.setBanned(true);
        return result;
    }

}
