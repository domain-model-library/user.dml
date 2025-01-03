package dml.user.service.repositoryset;

import dml.user.repository.*;

public interface LoginByOpenIDWithBanSupportServiceRepositorySet {
    OpenIDUserBindRepository getOpenIDUserBindRepository();

    UserBanRepository getUserBanRepository();

    UserIDGeneratorRepository getUserIDGeneratorRepository();

    UserRepository getUserRepository();

    UserSessionRepository getUserSessionRepository();

    UserSessionIDGeneratorRepository getUserSessionIDGeneratorRepository();

    UserCurrentSessionRepository getUserCurrentSessionRepository();

    UserSessionAliveKeeperRepository getUserSessionAliveKeeperRepository();
}
