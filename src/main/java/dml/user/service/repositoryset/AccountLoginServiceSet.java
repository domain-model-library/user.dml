package dml.user.service.repositoryset;

import dml.user.entity.User;
import dml.user.entity.UserAccount;
import dml.user.entity.UserSession;
import dml.user.repository.*;

/**
 * @author zheng chengdong
 */
public interface AccountLoginServiceSet {
    UserAccountRepository<UserAccount> getUserAccountRepository();

    UserSessionRepository<UserSession> getUserSessionRepository();

    UserSessionIdGeneratorRepository getUserSessionIdGeneratorRepository();

    UserIdGeneratorRepository getUserIdGeneratorRepository();

    UserRepository<User, Object> getUserRepository();
}
