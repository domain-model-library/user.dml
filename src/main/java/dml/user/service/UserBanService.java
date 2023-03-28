package dml.user.service;

import dml.user.entity.UserBan;
import dml.user.repository.UserBanRepository;
import dml.user.service.repositoryset.UserBanServiceRepositorySet;

public class UserBanService {

    public static void banUser(UserBanServiceRepositorySet repositorySet,
                               Object userId,
                               UserBan newUserBan) {

        UserBanRepository<UserBan, Object> userBanRepository = repositorySet.getUserBanRepository();
        newUserBan.setId(userId);
        userBanRepository.put(newUserBan);
    }

    public static void liftBan(UserBanServiceRepositorySet repositorySet,
                               Object userId) {
        UserBanRepository<UserBan, Object> userBanRepository = repositorySet.getUserBanRepository();
        userBanRepository.remove(userId);
    }

    public static boolean checkBan(UserBanServiceRepositorySet repositorySet,
                                   Object userId) {
        UserBanRepository<UserBan, Object> userBanRepository = repositorySet.getUserBanRepository();
        UserBan userBan = userBanRepository.find(userId);
        return userBan != null;
    }

}
