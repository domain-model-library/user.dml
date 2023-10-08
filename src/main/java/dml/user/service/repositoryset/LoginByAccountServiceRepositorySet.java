package dml.user.service.repositoryset;

import dml.user.entity.User;
import dml.user.entity.UserAccount;
import dml.user.entity.UserLoginState;
import dml.user.entity.UserSession;
import dml.user.repository.*;

/**
 * @author zheng chengdong
 */
public interface LoginByAccountServiceRepositorySet {
    UserAccountRepository<UserAccount> getUserAccountRepository();

    UserSessionRepository<UserSession> getUserSessionRepository();

    UserSessionIDGeneratorRepository getUserSessionIdGeneratorRepository();

    UserIDGeneratorRepository getUserIdGeneratorRepository();

    UserRepository<User, Object> getUserRepository();

    UserLoginStateRepository<UserLoginState, Object> getUserLoginStateRepository();
}
