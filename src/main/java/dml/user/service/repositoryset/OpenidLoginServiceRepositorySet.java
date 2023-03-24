package dml.user.service.repositoryset;

import dml.user.entity.OpenIdUserBind;
import dml.user.entity.Session;
import dml.user.entity.User;
import dml.user.entity.UserLoginState;
import dml.user.repository.*;

/**
 * @author zheng chengdong
 */
public interface OpenidLoginServiceRepositorySet {
    OpenIdUserBindRepository<OpenIdUserBind> getOpenIdUserBindRepository();

    UserIdGeneratorRepository getUserIdGeneratorRepository();

    UserRepository<User, Object> getUserRepository();

    UserLoginStateRepository<UserLoginState> getUserLoginStateRepository();

    SessionRepository<Session> getSessionRepository();

    SessionIdGeneratorRepository getSessionIdGeneratorRepository();
}
