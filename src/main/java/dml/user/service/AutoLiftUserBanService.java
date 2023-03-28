package dml.user.service;

import dml.user.entity.AutoLiftUserBan;
import dml.user.repository.AutoLiftUserBanRepository;
import dml.user.service.repositoryset.AutoLiftUserBanServiceRepositorySet;

/**
 * @author zheng chengdong
 */
public class AutoLiftUserBanService {
    public static void banUser(AutoLiftUserBanServiceRepositorySet repositorySet,
                               Object userId,
                               long autoLiftTime,
                               AutoLiftUserBan newAutoLiftUserBan) {

        AutoLiftUserBanRepository<AutoLiftUserBan, Object> autoLiftUserBanRepository = repositorySet.getAutoLiftUserBanRepository();
        newAutoLiftUserBan.setId(userId);
        newAutoLiftUserBan.setAutoLiftTime(autoLiftTime);
        autoLiftUserBanRepository.put(newAutoLiftUserBan);
    }

    public static void liftBan(AutoLiftUserBanServiceRepositorySet repositorySet,
                               Object userId) {
        AutoLiftUserBanRepository<AutoLiftUserBan, Object> autoLiftUserBanRepository = repositorySet.getAutoLiftUserBanRepository();
        autoLiftUserBanRepository.remove(userId);
    }

    /**
     * @param repositorySet
     * @param userId
     * @return 0未封禁，1封禁且未到解封时间，2原本处于封禁状态，现在到期解封了
     */
    public static int checkBanAndAutoLift(AutoLiftUserBanServiceRepositorySet repositorySet,
                                          Object userId,
                                          long currentTime) {
        AutoLiftUserBanRepository<AutoLiftUserBan, Object> autoLiftUserBanRepository = repositorySet.getAutoLiftUserBanRepository();
        AutoLiftUserBan autoLiftUserBan = autoLiftUserBanRepository.take(userId);
        if (autoLiftUserBan == null) {
            return 0;
        }
        boolean toLift = autoLiftUserBan.checkLift(currentTime);
        if (!toLift) {
            return 1;
        }
        autoLiftUserBanRepository.remove(userId);
        return 2;
    }

}
