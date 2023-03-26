import dml.id.entity.LongIdGenerator;
import dml.id.entity.UUIDStyleRandomStringIdGenerator;
import dml.test.repository.TestRepository;
import dml.test.repository.TestSingletonRepository;
import dml.user.entity.OpenIdUserBind;
import dml.user.entity.User;
import dml.user.entity.UserLoginState;
import dml.user.entity.UserSession;
import dml.user.repository.*;
import dml.user.service.OpenidLoginService;
import dml.user.service.repositoryset.OpenidLoginServiceRepositorySet;
import dml.user.service.result.OpenidLoginResult;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author zheng chengdong
 */
public class Login {

    @Test
    public void openIdLogin() {

        String openId1 = "1";
        OpenidLoginResult openidLoginResult1 = OpenidLoginService.openidLogin(openidLoginServiceRepositorySet,
                openId1,
                new TestUser(),
                new TestSession(),
                new TestOpenIdUserBind(),
                new TestUserLoginState());
        assertTrue(openidLoginResult1.isCreateNewUser());
        assertNotNull(openidLoginResult1.getUser());
        assertNotNull(openidLoginResult1.getCurrentUserSession());

        OpenidLoginResult openidLoginResult2 = OpenidLoginService.openidLogin(openidLoginServiceRepositorySet,
                openId1,
                new TestUser(),
                new TestSession(),
                new TestOpenIdUserBind(),
                new TestUserLoginState());
        assertFalse(openidLoginResult2.isCreateNewUser());
        assertEquals(openidLoginResult1.getCurrentUserSession().getId(), openidLoginResult2.getRemovedUserSession().getId());
    }

    OpenidLoginServiceRepositorySet openidLoginServiceRepositorySet = new OpenidLoginServiceRepositorySet() {
        OpenIdUserBindRepository<OpenIdUserBind> openIdUserBindRepository = TestRepository.instance(OpenIdUserBindRepository.class);
        UserIdGeneratorRepository userIdGeneratorRepository = TestSingletonRepository.instance(UserIdGeneratorRepository.class,
                new LongIdGenerator(1L));
        UserRepository<User, Object> userRepository = TestRepository.instance(UserRepository.class);
        UserLoginStateRepository<UserLoginState, Object> userLoginStateRepository = TestRepository.instance(UserLoginStateRepository.class);
        UserSessionRepository<UserSession> userSessionRepository = TestRepository.instance(UserSessionRepository.class);
        UserSessionIdGeneratorRepository userSessionIdGeneratorRepository = TestSingletonRepository.instance(UserSessionIdGeneratorRepository.class,
                new UUIDStyleRandomStringIdGenerator());

        @Override
        public OpenIdUserBindRepository<OpenIdUserBind> getOpenIdUserBindRepository() {
            return openIdUserBindRepository;
        }

        @Override
        public UserIdGeneratorRepository getUserIdGeneratorRepository() {
            return userIdGeneratorRepository;
        }

        @Override
        public UserRepository<User, Object> getUserRepository() {
            return userRepository;
        }

        @Override
        public UserLoginStateRepository<UserLoginState, Object> getUserLoginStateRepository() {
            return userLoginStateRepository;
        }

        @Override
        public UserSessionRepository<UserSession> getUserSessionRepository() {
            return userSessionRepository;
        }

        @Override
        public UserSessionIdGeneratorRepository getUserSessionIdGeneratorRepository() {
            return userSessionIdGeneratorRepository;
        }
    };

    class TestUser implements User {

        long id;


        @Override
        public void setId(Object id) {
            this.id = (long) id;
        }

        @Override
        public Object getId() {
            return id;
        }
    }

    class TestSession implements UserSession {
        String id;
        User user;

        @Override
        public String getId() {
            return id;
        }

        @Override
        public void setId(String id) {
            this.id = id;
        }

        @Override
        public void setUser(User user) {
            this.user = user;
        }

        @Override
        public User getUser() {
            return user;
        }
    }

    class TestOpenIdUserBind implements OpenIdUserBind {
        String id;
        User user;

        @Override
        public void setId(String id) {
            this.id = id;
        }

        @Override
        public void setUser(User user) {
            this.user = user;
        }

        @Override
        public User getUser() {
            return user;
        }
    }

    class TestUserLoginState implements UserLoginState {
        long id;
        UserSession currentUserSession;

        @Override
        public void setId(Object id) {
            this.id = (long) id;
        }

        @Override
        public Object getId() {
            return id;
        }

        @Override
        public UserSession getCurrentUserSession() {
            return currentUserSession;
        }

        @Override
        public void setCurrentUserSession(UserSession currentUserSession) {
            this.currentUserSession = currentUserSession;
        }
    }
}
