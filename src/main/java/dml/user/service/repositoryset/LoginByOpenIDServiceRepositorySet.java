package dml.user.service.repositoryset;

import dml.keepalive.entity.AliveKeeper;
import dml.keepalive.repository.AliveKeeperRepository;
import dml.user.entity.OpenIDUserBind;
import dml.user.entity.User;
import dml.user.entity.UserCurrentSession;
import dml.user.entity.UserSession;
import dml.user.repository.*;

/**
 * @author zheng chengdong
 */
public interface LoginByOpenIDServiceRepositorySet {
    OpenIDUserBindRepository getOpenIDUserBindRepository();

    UserIDGeneratorRepository getUserIDGeneratorRepository();

    UserRepository getUserRepository();

    UserSessionRepository getUserSessionRepository();

    UserSessionIDGeneratorRepository getUserSessionIDGeneratorRepository();

    UserCurrentSessionRepository getUserCurrentSessionRepository();

    AliveKeeperRepository getSessionAliveKeeperRepository();
}
