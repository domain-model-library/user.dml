package dml.user.service;

import dml.user.entity.AutoLiftTime;
import dml.user.entity.UserBan;
import dml.user.repository.UserBanRepository;
import dml.user.service.repositoryset.UserBanAutoLiftServiceRepositorySet;
import dml.user.service.repositoryset.UserBanServiceRepositorySet;
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


    public static void banUserWithAutoLift(UserBanServiceRepositorySet userBanServiceRepositorySet,
                                           UserBanAutoLiftServiceRepositorySet userBanAutoLiftServiceRepositorySet,
                                           Object userId,
                                           UserBan newUserBan,
                                           AutoLiftTime newAutoLiftTime) {

        banUser(userBanServiceRepositorySet,
                userId,
                newUserBan);

        UserBanAutoLiftService.setAutoLiftTime(userBanAutoLiftServiceRepositorySet,
                userId,
                newAutoLiftTime);
    }

    public static LiftAutoLiftBanResult liftAutoLiftBan(UserBanServiceRepositorySet userBanServiceRepositorySet,
                                                        UserBanAutoLiftServiceRepositorySet userBanAutoLiftServiceRepositorySet,
                                                        Object userId) {

        LiftAutoLiftBanResult result = new LiftAutoLiftBanResult();

        UserBan userBan = liftBan(userBanServiceRepositorySet,
                userId);
        if (userBan == null) {
            return result;
        }

        AutoLiftTime autoLiftTime = UserBanAutoLiftService.removeAutoLiftTime(userBanAutoLiftServiceRepositorySet,
                userId);

        result.setUserBan(userBan);
        result.setAutoLiftTime(autoLiftTime);
        return result;
    }

    public static CheckBanAndAutoLiftResult checkBanAndAutoLift(UserBanServiceRepositorySet userBanServiceRepositorySet,
                                                                UserBanAutoLiftServiceRepositorySet userBanAutoLiftServiceRepositorySet,
                                                                Object userId,
                                                                long currentTime) {

        CheckBanAndAutoLiftResult result = new CheckBanAndAutoLiftResult();

        boolean banned = checkBan(userBanServiceRepositorySet,
                userId);
        if (!banned) {
            return result;
        }

        CheckAutoLiftTimeAndLiftBanResult checkAutoLiftTimeAndLiftBanResult = UserBanAutoLiftService.checkAutoLiftTimeAndLiftBan(userBanAutoLiftServiceRepositorySet,
                userId,
                currentTime);
        if (checkAutoLiftTimeAndLiftBanResult.isLiftSuccess()) {
            banned = false;
        }
        result.setCheckAutoLiftTimeAndLiftBanResult(checkAutoLiftTimeAndLiftBanResult);
        result.setBanned(banned);
        return result;

    }

}
