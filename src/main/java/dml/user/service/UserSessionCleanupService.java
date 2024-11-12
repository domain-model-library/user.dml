package dml.user.service;

import dml.user.entity.UserSession;
import dml.user.repository.UserSessionAliveKeeperRepository;
import dml.user.repository.UserSessionRepository;
import dml.user.service.repositoryset.UserSessionCleanupServiceRepositorySet;
import dml.user.service.shared.SharedBusinessMethodsBetweenServices;

public class UserSessionCleanupService {

    public static UserSession checkSessionDeadAndRemove(UserSessionCleanupServiceRepositorySet repositorySet,
                                                        String sessionId,
                                                        long currentTime,
                                                        long sessionKeepAliveInterval) {
        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        UserSessionAliveKeeperRepository sessionAliveKeeperRepository = repositorySet.getUserSessionAliveKeeperRepository();

        return SharedBusinessMethodsBetweenServices.checkSessionDeadAndRemove(userSessionRepository, sessionAliveKeeperRepository,
                sessionId, currentTime, sessionKeepAliveInterval);
    }


}
