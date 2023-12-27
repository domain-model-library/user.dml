package dml.user.service.repositoryset;

import dml.user.entity.OpenIDUserBind;
import dml.user.entity.User;
import dml.user.entity.UserCurrentSession;
import dml.user.entity.UserSession;
import dml.user.repository.*;

/**
 * @author zheng chengdong
 */
public interface LoginByOpenIDServiceRepositorySet {
    OpenIDUserBindRepository<OpenIDUserBind> getOpenIDUserBindRepository();

    UserIDGeneratorRepository getUserIDGeneratorRepository();

    UserRepository<User, Object> getUserRepository();

    UserSessionRepository<UserSession> getUserSessionRepository();

    UserSessionIDGeneratorRepository getUserSessionIDGeneratorRepository();

    UserCurrentSessionRepository<UserCurrentSession, Object> getUserCurrentSessionRepository();
}
