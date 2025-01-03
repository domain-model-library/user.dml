package dml.user.service.repositoryset;

import dml.user.repository.UserAccountRepository;
import dml.user.repository.UserCurrentSessionRepository;
import dml.user.repository.UserSessionAliveKeeperRepository;
import dml.user.repository.UserSessionRepository;

/**
 * @author zheng chengdong
 */
public interface LoginByAccountServiceRepositorySet {
    UserAccountRepository getUserAccountRepository();

    UserSessionRepository getUserSessionRepository();

    UserCurrentSessionRepository getUserCurrentSessionRepository();

    UserSessionAliveKeeperRepository getUserSessionAliveKeeperRepository();
}
