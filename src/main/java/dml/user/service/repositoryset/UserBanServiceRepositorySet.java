package dml.user.service.repositoryset;

import dml.user.entity.UserBan;
import dml.user.entity.UserCurrentSession;
import dml.user.entity.UserSession;
import dml.user.repository.UserBanRepository;
import dml.user.repository.UserCurrentSessionRepository;
import dml.user.repository.UserSessionRepository;

public interface UserBanServiceRepositorySet {
    UserBanRepository<UserBan, Object> getUserBanRepository();

    UserCurrentSessionRepository<UserCurrentSession, Object> getUserCurrentSessionRepository();

    UserSessionRepository<UserSession> getUserSessionRepository();
}
