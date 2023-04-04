package dml.user.service;

import dml.user.entity.UserSession;
import dml.user.repository.UserSessionRepository;

/**
 * @author zheng chengdong
 */
class SharedBusinessMethodsBetweenServices {
    static UserSession logout(UserSessionRepository<UserSession> userSessionRepository,
                              String token) {
        return userSessionRepository.remove(token);
    }
}
