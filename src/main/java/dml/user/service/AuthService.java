package dml.user.service;

import dml.keepalive.repository.AliveKeeperRepository;
import dml.keepalive.service.KeepAliveService;
import dml.keepalive.service.repositoryset.AliveKeeperServiceRepositorySet;
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

    public static void keepSessionAlive(AuthServiceRepositorySet repositorySet,
                                        String token,
                                        long currentTime) {
        KeepAliveService.keepAlive(getAliveKeeperServiceRepositorySet(repositorySet),
                token, currentTime);
    }

    private static AliveKeeperServiceRepositorySet getAliveKeeperServiceRepositorySet(AuthServiceRepositorySet authServiceRepositorySet) {
        return new AliveKeeperServiceRepositorySet() {

            @Override
            public AliveKeeperRepository getAliveKeeperRepository() {
                return authServiceRepositorySet.getUserSessionAliveKeeperRepository();
            }
        };
    }

}
