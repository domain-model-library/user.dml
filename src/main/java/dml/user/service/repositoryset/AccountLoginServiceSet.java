package dml.user.service.repositoryset;

import dml.user.entity.UserAccount;
import dml.user.entity.UserSession;
import dml.user.repository.UserAccountRepository;
import dml.user.repository.UserSessionIdGeneratorRepository;
import dml.user.repository.UserSessionRepository;

/**
 * @author zheng chengdong
 */
public interface AccountLoginServiceSet {
    UserAccountRepository<UserAccount> getUserAccountRepository();

    UserSessionRepository<UserSession> getUserSessionRepository();

    UserSessionIdGeneratorRepository getUserSessionIdGeneratorRepository();
}
