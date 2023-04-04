package dml.user.service.repositoryset;

import dml.user.entity.UserLoginState;
import dml.user.entity.UserSession;
import dml.user.repository.UserLoginStateRepository;
import dml.user.repository.UserSessionRepository;

/**
 * @author zheng chengdong
 */
public interface UserBanForceLogoutServiceRepositorySet {
    UserLoginStateRepository<UserLoginState, Object> getUserLoginStateRepository();

    UserSessionRepository<UserSession> getUserSessionRepository();
}
