package dml.user.service.repositoryset;

import dml.user.repository.*;

/**
 * @author zheng chengdong
 */
public interface LoginByAccountServiceRepositorySet {
    UserAccountRepository getUserAccountRepository();

    UserSessionRepository getUserSessionRepository();

    UserSessionIDGeneratorRepository getUserSessionIdGeneratorRepository();

    UserCurrentSessionRepository getUserCurrentSessionRepository();

    UserSessionAliveKeeperRepository getSessionAliveKeeperRepository();
}
