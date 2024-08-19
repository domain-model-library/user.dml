package dml.user.service.repositoryset;

import dml.user.entity.*;
import dml.user.repository.*;

public interface LoginByOpenIDWithBanSupportServiceRepositorySet {
    OpenIDUserBindRepository<OpenIDUserBind> getOpenIDUserBindRepository();

    UserBanRepository<UserBan, Object> getUserBanRepository();

    UserIDGeneratorRepository getUserIDGeneratorRepository();

    UserRepository getUserRepository();

    UserSessionRepository<UserSession> getUserSessionRepository();

    UserSessionIDGeneratorRepository getUserSessionIDGeneratorRepository();

    UserCurrentSessionRepository<UserCurrentSession, Object> getUserCurrentSessionRepository();
}
