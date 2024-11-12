package dml.user.service.repositoryset;

import dml.user.repository.UserSessionAliveKeeperRepository;
import dml.user.repository.UserSessionRepository;

public interface UserSessionCleanupServiceRepositorySet {
    UserSessionRepository getUserSessionRepository();

    UserSessionAliveKeeperRepository getUserSessionAliveKeeperRepository();
}
