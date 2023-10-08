package dml.user.service.repositoryset;

import dml.user.entity.UserBan;
import dml.user.entity.UserLoginState;
import dml.user.entity.UserSession;
import dml.user.repository.UserBanRepository;
import dml.user.repository.UserLoginStateRepository;
import dml.user.repository.UserSessionRepository;

public interface UserBanServiceRepositorySet {
    UserBanRepository<UserBan, Object> getUserBanRepository();

    UserLoginStateRepository<UserLoginState, Object> getUserLoginStateRepository();

    UserSessionRepository<UserSession> getUserSessionRepository();
}
