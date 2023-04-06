import dml.id.entity.LongIdGenerator;
import dml.id.entity.UUIDStyleRandomStringIdGenerator;
import dml.test.repository.TestRepository;
import dml.test.repository.TestSingletonRepository;
import dml.user.entity.*;
import dml.user.repository.*;
import dml.user.service.*;
import dml.user.service.repositoryset.*;
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
                new TestOpenIdUserBind());
        assertTrue(openidLoginResult1.isCreateNewUser());
        assertNotNull(openidLoginResult1.getUser());
        assertNotNull(openidLoginResult1.getNewUserSession());
        String kickedSessionId1 = KickLoginService.newLoginKickOldLogin(kickLoginServiceRepositorySet,
                openidLoginResult1.getNewUserSession().getId(),
                new TestUserLoginState());
        assertNull(kickedSessionId1);

        long currentTime = System.currentTimeMillis();
        User user1 = AuthService.auth(authServiceRepositorySet,
                openidLoginResult1.getNewUserSession().getId());
        assertEquals(openidLoginResult1.getUser().getId(), user1.getId());

        OpenidLoginResult openidLoginResult2 = OpenidLoginService.openidLogin(openidLoginServiceRepositorySet,
                openId1,
                new TestUser(),
                new TestSession(),
                new TestOpenIdUserBind());
        String kickedSessionId2 = KickLoginService.newLoginKickOldLogin(kickLoginServiceRepositorySet,
                openidLoginResult2.getNewUserSession().getId(),
                new TestUserLoginState());
        assertFalse(openidLoginResult2.isCreateNewUser());
        assertEquals(openidLoginResult1.getNewUserSession().getId(), kickedSessionId2);

        User user2 = AuthService.auth(authServiceRepositorySet,
                openidLoginResult1.getNewUserSession().getId());
        assertNull(user2);

        OpenidLoginService.logout(openidLoginServiceRepositorySet,
                openidLoginResult2.getNewUserSession().getId());
        User user3 = AuthService.auth(authServiceRepositorySet,
                openidLoginResult2.getNewUserSession().getId());
        assertNull(user3);

        UserBanService.banUser(userBanServiceRepositorySet,
                user1.getId(),
                new TestUserBan()
        );
        UserBanAutoLiftService.setAutoLiftTime(userBanAutoLiftServiceRepositorySet,
                user1.getId()
                , new TestAutoLiftTime(currentTime + 60 * 1000L));
        UserBanForceLogoutService.forceLogout(userBanForceLogoutServiceRepositorySet,
                user1.getId());
        User user4 = AuthService.auth(authServiceRepositorySet,
                openidLoginResult1.getNewUserSession().getId());
        assertNull(user4);

        currentTime += (30 * 1000L);

        boolean isBan1 = UserBanService.checkBan(userBanServiceRepositorySet,
                user1.getId());
        assertTrue(isBan1);
        UserBan userBan1 = UserBanAutoLiftService.checkAndLift(userBanAutoLiftServiceRepositorySet,
                user1.getId(),
                currentTime);
        assertNull(userBan1);

        currentTime += (31 * 1000L);

        UserBan userBan2 = UserBanAutoLiftService.checkAndLift(userBanAutoLiftServiceRepositorySet,
                user1.getId(),
                currentTime);
        assertNotNull(userBan2);
        boolean isBan2 = UserBanService.checkBan(userBanServiceRepositorySet,
                user1.getId());
        assertFalse(isBan2);

    }

    OpenIdUserBindRepository<OpenIdUserBind> openIdUserBindRepository = TestRepository.instance(OpenIdUserBindRepository.class);
    UserIdGeneratorRepository userIdGeneratorRepository = TestSingletonRepository.instance(UserIdGeneratorRepository.class,
            new LongIdGenerator(1L));
    UserRepository<User, Object> userRepository = TestRepository.instance(UserRepository.class);
    UserLoginStateRepository<UserLoginState, Object> userLoginStateRepository = TestRepository.instance(UserLoginStateRepository.class);
    UserSessionRepository<UserSession> userSessionRepository = TestRepository.instance(UserSessionRepository.class);
    UserSessionIdGeneratorRepository userSessionIdGeneratorRepository = TestSingletonRepository.instance(UserSessionIdGeneratorRepository.class,
            new UUIDStyleRandomStringIdGenerator());
    UserBanRepository<UserBan, Object> userBanRepository = TestRepository.instance(UserBanRepository.class);
    AutoLiftTimeRepository<AutoLiftTime, Object> autoLiftTimeRepository = TestRepository.instance(AutoLiftTimeRepository.class);

    OpenidLoginServiceRepositorySet openidLoginServiceRepositorySet = new OpenidLoginServiceRepositorySet() {


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
        public UserSessionRepository<UserSession> getUserSessionRepository() {
            return userSessionRepository;
        }

        @Override
        public UserSessionIdGeneratorRepository getUserSessionIdGeneratorRepository() {
            return userSessionIdGeneratorRepository;
        }
    };

    KickLoginServiceRepositorySet kickLoginServiceRepositorySet = new KickLoginServiceRepositorySet() {

        @Override
        public UserSessionRepository<UserSession> getUserSessionRepository() {
            return userSessionRepository;
        }

        @Override
        public UserLoginStateRepository<UserLoginState, Object> getUserLoginStateRepository() {
            return userLoginStateRepository;
        }
    };

    AuthServiceRepositorySet authServiceRepositorySet = new AuthServiceRepositorySet() {

        @Override
        public UserSessionRepository<UserSession> getUserSessionRepository() {
            return userSessionRepository;
        }
    };

    UserBanServiceRepositorySet userBanServiceRepositorySet = new UserBanServiceRepositorySet() {
        @Override
        public UserBanRepository<UserBan, Object> getUserBanRepository() {
            return userBanRepository;
        }
    };

    UserBanAutoLiftServiceRepositorySet userBanAutoLiftServiceRepositorySet = new UserBanAutoLiftServiceRepositorySet() {
        @Override
        public AutoLiftTimeRepository<AutoLiftTime, Object> getAutoLiftTimeRepository() {
            return autoLiftTimeRepository;
        }

        @Override
        public UserBanRepository<UserBan, Object> getUserBanRepository() {
            return userBanRepository;
        }
    };

    UserBanForceLogoutServiceRepositorySet userBanForceLogoutServiceRepositorySet = new UserBanForceLogoutServiceRepositorySet() {
        @Override
        public UserLoginStateRepository<UserLoginState, Object> getUserLoginStateRepository() {
            return userLoginStateRepository;
        }

        @Override
        public UserSessionRepository<UserSession> getUserSessionRepository() {
            return userSessionRepository;
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

    class TestUserBan implements UserBan {
        long id;

        @Override
        public void setId(Object id) {
            this.id = (long) id;
        }
    }

    class TestAutoLiftTime extends AutoLiftTimeBase {
        long id;

        public TestAutoLiftTime(long liftTime) {
            this.liftTime = liftTime;
        }

        @Override
        public void setId(Object id) {
            this.id = (long) id;
        }

        @Override
        public Object getId() {
            return id;
        }


    }
}
