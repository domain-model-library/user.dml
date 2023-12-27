package dml.user.service;

import dml.user.entity.UserBan;
import dml.user.entity.UserCurrentSession;
import dml.user.entity.UserSession;
import dml.user.repository.UserBanRepository;
import dml.user.repository.UserCurrentSessionRepository;
import dml.user.repository.UserSessionRepository;
import dml.user.service.repositoryset.UserBanServiceRepositorySet;
import dml.user.service.shared.SharedBusinessMethodsBetweenServices;

public class UserBanService {

    public static UserSession banUser(UserBanServiceRepositorySet repositorySet,
                                      Object userId,
                                      UserBan newUserBan) {

        UserBanRepository<UserBan, Object> userBanRepository = repositorySet.getUserBanRepository();
        UserCurrentSessionRepository<UserCurrentSession, Object> userCurrentSessionRepository = repositorySet.getUserCurrentSessionRepository();
        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        newUserBan.setUserID(userId);
        userBanRepository.put(newUserBan);

        UserCurrentSession userCurrentSession = userCurrentSessionRepository.take(userId);
        if (userCurrentSession == null) {
            return null;
        }
        if (userCurrentSession.getCurrentSession() == null) {
            return null;
        }
        UserSession removedUserSession = SharedBusinessMethodsBetweenServices.logout(userSessionRepository,
                userCurrentSession.getCurrentSession().getId());
        SharedBusinessMethodsBetweenServices.updateUserCurrentSessionForLogout(userCurrentSessionRepository, userId);
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
