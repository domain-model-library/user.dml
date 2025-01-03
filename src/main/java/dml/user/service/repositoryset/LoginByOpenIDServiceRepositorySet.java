package dml.user.service.repositoryset;

import dml.user.repository.*;

/**
 * @author zheng chengdong
 */
public interface LoginByOpenIDServiceRepositorySet {
    OpenIDUserBindRepository getOpenIDUserBindRepository();

    UserRepository getUserRepository();

    UserSessionRepository getUserSessionRepository();

    UserCurrentSessionRepository getUserCurrentSessionRepository();

    UserSessionAliveKeeperRepository getUserSessionAliveKeeperRepository();
}
