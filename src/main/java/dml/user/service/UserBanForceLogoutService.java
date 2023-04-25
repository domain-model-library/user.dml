package dml.user.service;

import dml.user.entity.UserLoginState;
import dml.user.entity.UserSession;
import dml.user.repository.UserLoginStateRepository;
import dml.user.repository.UserSessionRepository;
import dml.user.service.repositoryset.UserBanForceLogoutServiceRepositorySet;

/**
 * @author zheng chengdong
 */
public class UserBanForceLogoutService {

    public static UserSession forceLogout(UserBanForceLogoutServiceRepositorySet repositorySet,
                                          Object userId) {
        UserLoginStateRepository<UserLoginState, Object> userLoginStateRepository = repositorySet.getUserLoginStateRepository();
        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();

        UserLoginState userLoginState = userLoginStateRepository.take(userId);
        if (userLoginState == null) {
            return null;
        }
        UserSession userSession = userLoginState.getCurrentUserSession();
        if (userSession == null) {
            return null;
        }

        UserSession removedUserSession = SharedBusinessMethodsBetweenServices.logout(userSessionRepository, userSession.getId());
        userLoginState.setCurrentUserSession(null);
        return removedUserSession;
    }

}
