package dml.user.service.repositoryset;

import dml.user.entity.UserLoginState;
import dml.user.entity.UserSession;
import dml.user.repository.UserLoginStateRepository;
import dml.user.repository.UserSessionRepository;

/**
 * @author zheng chengdong
 */
public interface KickLoginServiceRepositorySet {
    UserSessionRepository<UserSession> getUserSessionRepository();

    UserLoginStateRepository<UserLoginState, Object> getUserLoginStateRepository();
}
