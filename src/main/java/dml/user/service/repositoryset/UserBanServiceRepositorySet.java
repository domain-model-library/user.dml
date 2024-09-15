package dml.user.service.repositoryset;

import dml.user.repository.UserBanRepository;
import dml.user.repository.UserCurrentSessionRepository;
import dml.user.repository.UserSessionAliveKeeperRepository;
import dml.user.repository.UserSessionRepository;

public interface UserBanServiceRepositorySet {
    UserBanRepository getUserBanRepository();

    UserCurrentSessionRepository getUserCurrentSessionRepository();

    UserSessionRepository getUserSessionRepository();

    UserSessionAliveKeeperRepository getSessionAliveKeeperRepository();
}
