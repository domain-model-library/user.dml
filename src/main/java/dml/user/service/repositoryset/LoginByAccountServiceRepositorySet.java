package dml.user.service.repositoryset;

import dml.user.entity.UserAccount;
import dml.user.entity.UserLoginState;
import dml.user.entity.UserSession;
import dml.user.repository.UserAccountRepository;
import dml.user.repository.UserLoginStateRepository;
import dml.user.repository.UserSessionIDGeneratorRepository;
import dml.user.repository.UserSessionRepository;

/**
 * @author zheng chengdong
 */
public interface LoginByAccountServiceRepositorySet {
    UserAccountRepository<UserAccount> getUserAccountRepository();

    UserSessionRepository<UserSession> getUserSessionRepository();

    UserSessionIDGeneratorRepository getUserSessionIdGeneratorRepository();

    UserLoginStateRepository<UserLoginState, Object> getUserLoginStateRepository();
}
