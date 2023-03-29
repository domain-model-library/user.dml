package dml.user.service;

import dml.user.entity.UserLoginState;
import dml.user.entity.UserSession;
import dml.user.repository.UserLoginStateRepository;
import dml.user.repository.UserSessionRepository;
import dml.user.service.repositoryset.KickLoginServiceRepositorySet;

/**
 * @author zheng chengdong
 */
public class KickLoginService {

    public static String newLoginKickOldLogin(KickLoginServiceRepositorySet repositorySet,
                                              String newUserSessionId,
                                              UserLoginState newUserLoginState) {

        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        UserLoginStateRepository<UserLoginState, Object> userLoginStateRepository = repositorySet.getUserLoginStateRepository();

        UserSession newUserSession = userSessionRepository.find(newUserSessionId);
        newUserLoginState.setId(newUserSession.getUser().getId());
        UserLoginState userLoginState = userLoginStateRepository.takeOrPutIfAbsent(newUserSession.getUser().getId(), newUserLoginState);
        UserSession currentUserSession = userLoginState.getCurrentUserSession();
        String removedUserSessionId = null;
        if (currentUserSession != null) {
            UserSession removedUserSession = userSessionRepository.remove(currentUserSession.getId());
            removedUserSessionId = removedUserSession.getId();
        }
        userLoginState.setCurrentUserSession(newUserSession);
        return removedUserSessionId;
    }

    public static void setLoggedOut(KickLoginServiceRepositorySet repositorySet,
                                    Object userId) {

        UserLoginStateRepository<UserLoginState, Object> userLoginStateRepository = repositorySet.getUserLoginStateRepository();

        UserLoginState userLoginState = userLoginStateRepository.take(userId);
        userLoginState.setCurrentUserSession(null);
    }

}
