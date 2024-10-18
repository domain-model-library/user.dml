package dml.user.service.repositoryset;

import dml.user.repository.UserSessionAliveKeeperRepository;
import dml.user.repository.UserSessionRepository;

public interface AuthServiceRepositorySet {

    UserSessionRepository getUserSessionRepository();

    UserSessionAliveKeeperRepository getUserSessionAliveKeeperRepository();
}
