package dml.user.service;

import dml.user.entity.UserSession;
import dml.user.repository.UserSessionRepository;
import dml.user.service.repositoryset.AuthServiceRepositorySet;

public class AuthService {

    public static Object auth(AuthServiceRepositorySet repositorySet,
                              String token) {
        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        UserSession userSession = userSessionRepository.find(token);
        if (userSession == null) {
            return null;
        }
        return userSession.getUserID();
    }

}
