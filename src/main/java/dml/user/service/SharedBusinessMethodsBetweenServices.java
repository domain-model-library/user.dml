package dml.user.service;

import dml.id.entity.IdGenerator;
import dml.user.entity.User;
import dml.user.entity.UserSession;
import dml.user.repository.UserSessionIdGeneratorRepository;
import dml.user.repository.UserSessionRepository;

/**
 * @author zheng chengdong
 */
class SharedBusinessMethodsBetweenServices {
    static UserSession logout(UserSessionRepository<UserSession> userSessionRepository,
                              String token) {
        return userSessionRepository.remove(token);
    }

    static UserSession createUserSession(UserSessionIdGeneratorRepository userSessionIdGeneratorRepository,
                                         UserSessionRepository<UserSession> userSessionRepository,
                                         UserSession newUserSession,
                                         User user) {
        IdGenerator<String> sessionIdGenerator = userSessionIdGeneratorRepository.take();
        newUserSession.setId(sessionIdGenerator.generateId());
        newUserSession.setUser(user);
        userSessionRepository.put(newUserSession);

        return newUserSession;
    }

}
