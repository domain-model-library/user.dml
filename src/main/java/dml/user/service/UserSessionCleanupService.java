package dml.user.service;

import dml.keepalive.entity.AliveKeeper;
import dml.keepalive.repository.AliveKeeperRepository;
import dml.keepalive.service.KeepAliveService;
import dml.keepalive.service.repositoryset.AliveKeeperServiceRepositorySet;
import dml.user.entity.UserSession;
import dml.user.repository.UserSessionRepository;
import dml.user.service.repositoryset.UserSessionCleanupServiceRepositorySet;

public class UserSessionCleanupService {

    public static UserSession checkSessionDeadAndRemove(UserSessionCleanupServiceRepositorySet repositorySet,
                                                        String sessionId,
                                                        long currentTime,
                                                        long sessionKeepAliveInterval) {
        AliveKeeperRepository<AliveKeeper, String> sessionAliveKeeperRepository = repositorySet.getSessionAliveKeeperRepository();
        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();

        boolean alive = KeepAliveService.isAlive(getAliveKeeperServiceRepositorySet(sessionAliveKeeperRepository),
                sessionId, currentTime, sessionKeepAliveInterval);
        if (!alive) {
            UserSession removedSession = userSessionRepository.remove(sessionId);
            KeepAliveService.removeAliveKeeper(getAliveKeeperServiceRepositorySet(sessionAliveKeeperRepository)
                    , sessionId);
            return removedSession;
        }
        return null;

    }

    private static AliveKeeperServiceRepositorySet getAliveKeeperServiceRepositorySet(AliveKeeperRepository<AliveKeeper, String> sessionAliveKeeperRepository) {
        return new AliveKeeperServiceRepositorySet() {
            @Override
            public AliveKeeperRepository getAliveKeeperRepository() {
                return sessionAliveKeeperRepository;
            }
        };
    }

}
