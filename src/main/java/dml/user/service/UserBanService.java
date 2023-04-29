package dml.user.service;

import dml.user.entity.AutoLiftTime;
import dml.user.entity.UserBan;
import dml.user.entity.UserSession;
import dml.user.repository.UserBanRepository;
import dml.user.service.repositoryset.*;
import dml.user.service.result.CheckAutoLiftTimeAndLiftBanResult;
import dml.user.service.result.CheckBanAndAutoLiftResult;
import dml.user.service.result.LiftAutoLiftBanResult;

public class UserBanService {

    public static void banUser(UserBanServiceRepositorySet repositorySet,
                               Object userId,
                               UserBan newUserBan) {

        UserBanRepository<UserBan, Object> userBanRepository = repositorySet.getUserBanRepository();
        newUserBan.setId(userId);
        userBanRepository.put(newUserBan);
    }

    public static UserBan liftBan(UserBanServiceRepositorySet repositorySet,
                                  Object userId) {
        UserBanRepository<UserBan, Object> userBanRepository = repositorySet.getUserBanRepository();
        return userBanRepository.remove(userId);
    }

    public static boolean checkBan(UserBanServiceRepositorySet repositorySet,
                                   Object userId) {
        UserBanRepository<UserBan, Object> userBanRepository = repositorySet.getUserBanRepository();
        UserBan userBan = userBanRepository.find(userId);
        return userBan != null;
    }


    public static void banUserWithAutoLift(BanUserWithAutoLiftRepositorySet repositorySet,
                                           Object userId,
                                           UserBan newUserBan,
                                           AutoLiftTime newAutoLiftTime) {

        banUser(repositorySet,
                userId,
                newUserBan);

        UserBanAutoLiftService.setAutoLiftTime(repositorySet,
                userId,
                newAutoLiftTime);
    }

    public static LiftAutoLiftBanResult liftAutoLiftBan(LiftAutoLiftBanRepositorySet repositorySet,
                                                        Object userId) {

        LiftAutoLiftBanResult result = new LiftAutoLiftBanResult();

        UserBan userBan = liftBan(repositorySet,
                userId);
        if (userBan == null) {
            return result;
        }

        AutoLiftTime autoLiftTime = UserBanAutoLiftService.removeAutoLiftTime(repositorySet,
                userId);

        result.setUserBan(userBan);
        result.setAutoLiftTime(autoLiftTime);
        return result;
    }

    public static CheckBanAndAutoLiftResult checkBanAndAutoLift(CheckBanAndAutoLiftRepositorySet repositorySet,
                                                                Object userId,
                                                                long currentTime) {

        CheckBanAndAutoLiftResult result = new CheckBanAndAutoLiftResult();

        boolean banned = checkBan(repositorySet,
                userId);
        if (!banned) {
            return result;
        }

        CheckAutoLiftTimeAndLiftBanResult checkAutoLiftTimeAndLiftBanResult = UserBanAutoLiftService.checkAutoLiftTimeAndLiftBan(repositorySet,
                userId,
                currentTime);
        if (checkAutoLiftTimeAndLiftBanResult.isLiftSuccess()) {
            banned = false;
        }
        result.setCheckAutoLiftTimeAndLiftBanResult(checkAutoLiftTimeAndLiftBanResult);
        result.setBanned(banned);
        return result;

    }

    public static UserSession banUserAndForceLogout(BanUserAndForceLogoutRepositorySet repositorySet,
                                                    Object userId,
                                                    UserBan newUserBan) {

        banUser(repositorySet,
                userId,
                newUserBan);

        return UserBanForceLogoutService.forceLogout(repositorySet,
                userId);
    }

    public static UserSession banUserWithAutoLiftAndForceLogout(BanUserWithAutoLiftAndForceLogoutRepositorySet repositorySet,
                                                                Object userId,
                                                                UserBan newUserBan,
                                                                AutoLiftTime newAutoLiftTime) {

        banUserWithAutoLift(repositorySet,
                userId,
                newUserBan,
                newAutoLiftTime);

        return UserBanForceLogoutService.forceLogout(repositorySet,
                userId);
    }

}
