package dml.user.service;

import dml.user.entity.UserBan;
import dml.user.entity.UserLoginState;
import dml.user.entity.UserSession;
import dml.user.repository.UserBanRepository;
import dml.user.repository.UserLoginStateRepository;
import dml.user.repository.UserSessionRepository;
import dml.user.service.repositoryset.UserBanServiceRepositorySet;
import dml.user.service.shared.SharedBusinessMethodsBetweenServices;

public class UserBanService {

    public static UserSession banUser(UserBanServiceRepositorySet repositorySet,
                                      Object userId,
                                      UserBan newUserBan) {

        UserBanRepository<UserBan, Object> userBanRepository = repositorySet.getUserBanRepository();
        UserLoginStateRepository<UserLoginState, Object> userLoginStateRepository = repositorySet.getUserLoginStateRepository();
        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        newUserBan.setId(userId);
        userBanRepository.put(newUserBan);

        UserLoginState userLoginState = userLoginStateRepository.take(userId);
        if (userLoginState == null) {
            return null;
        }
        if (userLoginState.getCurrentUserSession() == null) {
            return null;
        }
        UserSession removedUserSession = SharedBusinessMethodsBetweenServices.logout(userSessionRepository,
                userLoginState.getCurrentUserSession().getId());
        SharedBusinessMethodsBetweenServices.updateUserLoginStateForLogout(userLoginStateRepository, userId);
        return removedUserSession;
    }

    public static UserBan liftBan(UserBanServiceRepositorySet repositorySet,
                                  Object userId) {
        UserBanRepository<UserBan, Object> userBanRepository = repositorySet.getUserBanRepository();
        return userBanRepository.remove(userId);
    }

    public static boolean checkBan(UserBanServiceRepositorySet repositorySet,
                                   Object userId) {
        return SharedBusinessMethodsBetweenServices.checkBan(repositorySet.getUserBanRepository(),
                userId);
    }

}
