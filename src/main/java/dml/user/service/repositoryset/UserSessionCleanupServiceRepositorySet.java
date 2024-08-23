package dml.user.service.repositoryset;

import dml.keepalive.repository.AliveKeeperRepository;
import dml.user.repository.UserSessionRepository;

public interface UserSessionCleanupServiceRepositorySet {
    UserSessionRepository getUserSessionRepository();

    AliveKeeperRepository getSessionAliveKeeperRepository();
}
