package dml.user.service;

import dml.keepalive.entity.AliveKeeper;
import dml.keepalive.repository.AliveKeeperRepository;
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

        AliveKeeper aliveKeeper = sessionAliveKeeperRepository.take(sessionId);
        if (aliveKeeper.isAlive(currentTime, sessionKeepAliveInterval)) {
            return null;
        } else {
            UserSession removedUserSession = userSessionRepository.remove(sessionId);
            sessionAliveKeeperRepository.remove(sessionId);
            return removedUserSession;
        }

    }

}
