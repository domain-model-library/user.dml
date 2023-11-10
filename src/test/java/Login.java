import dml.common.repository.TestCommonRepository;
import dml.common.repository.TestCommonSingletonRepository;
import dml.id.entity.LongIdGenerator;
import dml.id.entity.UUIDStyleRandomStringIdGenerator;
import dml.user.entity.*;
import dml.user.repository.*;
import dml.user.service.*;
import dml.user.service.repositoryset.*;
import dml.user.service.result.CheckAndRemoveLiftTimeResult;
import dml.user.service.result.LoginByAccountPasswordResult;
import dml.user.service.result.LoginByOpenIDResult;
import dml.user.service.result.RegisterNewUserResult;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author zheng chengdong
 */
public class Login {

    @Test
    public void openIdLogin() {

        String openId1 = "1";
        LoginByOpenIDResult openidLoginResult1 = LoginByOpenIDService.loginByOpenID(loginByOpenIDServiceRepositorySet,
                openId1,
                new TestUser(),
                new TestSession(),
                new TestOpenIdUserBind(),
                new TestUserLoginState());
        assertTrue(openidLoginResult1.isCreateNewUser());
        assertNotNull(openidLoginResult1.getNewUserSession().getUser());
        assertNotNull(openidLoginResult1.getNewUserSession());
        String kickedSessionId1 = openidLoginResult1.getRemovedUserSessionID();
        assertNull(kickedSessionId1);

        long currentTime = System.currentTimeMillis();
        User user1 = AuthService.auth(authServiceRepositorySet,
                openidLoginResult1.getNewUserSession().getId());
        assertEquals(openidLoginResult1.getNewUserSession().getUser().getId(), user1.getId());

        LoginByOpenIDResult openidLoginResult2 = LoginByOpenIDService.loginByOpenID(loginByOpenIDServiceRepositorySet,
                openId1,
                new TestUser(),
                new TestSession(),
                new TestOpenIdUserBind(),
                new TestUserLoginState());
        assertFalse(openidLoginResult2.isCreateNewUser());
        assertEquals(openidLoginResult1.getNewUserSession().getId(), openidLoginResult2.getRemovedUserSessionID());

        User user2 = AuthService.auth(authServiceRepositorySet,
                openidLoginResult1.getNewUserSession().getId());
        assertNull(user2);

        LoginByOpenIDService.logout(loginByOpenIDServiceRepositorySet,
                openidLoginResult2.getNewUserSession().getId());
        User user3 = AuthService.auth(authServiceRepositorySet,
                openidLoginResult2.getNewUserSession().getId());
        assertNull(user3);

        UserBanService.banUser(userBanServiceRepositorySet,
                user1.getId(),
                new TestUserBan()
        );
        UserBanAutoLiftService.setAutoLift(userBanAutoLiftServiceRepositorySet,
                user1.getId()
                , new TestAutoLiftTime(currentTime + 60 * 1000L));
        User user4 = AuthService.auth(authServiceRepositorySet,
                openidLoginResult1.getNewUserSession().getId());
        assertNull(user4);

        currentTime += (30 * 1000L);

        boolean isBan1 = UserBanService.checkBan(userBanServiceRepositorySet,
                user1.getId());
        assertTrue(isBan1);
        CheckAndRemoveLiftTimeResult checkAndRemoveLiftTimeResult1 = UserBanAutoLiftService.checkToLiftAndUnsetAutoLift(userBanAutoLiftServiceRepositorySet,
                user1.getId(),
                currentTime);
        assertFalse(checkAndRemoveLiftTimeResult1.isToLift());

        currentTime += (31 * 1000L);

        CheckAndRemoveLiftTimeResult checkAndRemoveLiftTimeResult2 = UserBanAutoLiftService.checkToLiftAndUnsetAutoLift(userBanAutoLiftServiceRepositorySet,
                user1.getId(),
                currentTime);
        assertTrue(checkAndRemoveLiftTimeResult2.isToLift());

        UserBanService.liftBan(userBanServiceRepositorySet,
                user1.getId());
        boolean isBan2 = UserBanService.checkBan(userBanServiceRepositorySet,
                user1.getId());
        assertFalse(isBan2);
    }

    @Test
    public void accountIdLogin() {
        RegisterNewUserResult registerNewUserResult1 = UserRegistrationService.registerNewUser(userRegistrationServiceRepositorySet, "account1",
                new TestUserAccount("account1", "pass1"),
                new TestUser());
        assertFalse(registerNewUserResult1.isAccountExists());

        LoginByAccountPasswordResult loginByAccountPasswordResult1 = LoginByAccountService.loginByAccountPassword(loginByAccountServiceRepositorySet,
                "account1",
                "pass1",
                new TestSession(),
                new TestUserLoginState());
        assertTrue(loginByAccountPasswordResult1.isLoginSuccess());

        User user1 = AuthService.auth(authServiceRepositorySet,
                loginByAccountPasswordResult1.getNewUserSession().getId());
        assertEquals(loginByAccountPasswordResult1.getNewUserSession().getUser().getId(), user1.getId());

        LoginByAccountService.logout(loginByAccountServiceRepositorySet,
                loginByAccountPasswordResult1.getNewUserSession().getId());

        User user2 = AuthService.auth(authServiceRepositorySet,
                loginByAccountPasswordResult1.getNewUserSession().getId());
        assertNull(user2);

        LoginByAccountPasswordResult accountPasswordKickLoginResult1 = LoginByAccountService.loginByAccountPassword(loginByAccountServiceRepositorySet,
                "account1",
                "pass1",
                new TestSession(),
                new TestUserLoginState());
        LoginByAccountPasswordResult accountPasswordKickLoginResult2 = LoginByAccountService.loginByAccountPassword(loginByAccountServiceRepositorySet,
                "account1",
                "pass1",
                new TestSession(),
                new TestUserLoginState());
        User user3 = AuthService.auth(authServiceRepositorySet,
                accountPasswordKickLoginResult1.getNewUserSession().getId());
        assertNull(user3);

    }

    OpenIDUserBindRepository<OpenIDUserBind> openIdUserBindRepository = TestCommonRepository.instance(OpenIDUserBindRepository.class);
    UserIDGeneratorRepository userIdGeneratorRepository = TestCommonSingletonRepository.instance(UserIDGeneratorRepository.class,
            new LongIdGenerator(1L) {
            });
    UserRepository<User, Object> userRepository = TestCommonRepository.instance(UserRepository.class);
    UserLoginStateRepository<UserLoginState, Object> userLoginStateRepository = TestCommonRepository.instance(UserLoginStateRepository.class);
    UserSessionRepository<UserSession> userSessionRepository = TestCommonRepository.instance(UserSessionRepository.class);
    UserSessionIDGeneratorRepository userSessionIdGeneratorRepository = TestCommonSingletonRepository.instance(UserSessionIDGeneratorRepository.class,
            new UUIDStyleRandomStringIdGenerator() {
            });
    UserBanRepository<UserBan, Object> userBanRepository = TestCommonRepository.instance(UserBanRepository.class);
    AutoLiftTimeRepository<AutoLiftTime, Object> autoLiftTimeRepository = TestCommonRepository.instance(AutoLiftTimeRepository.class);
    UserAccountRepository<UserAccount> userAccountRepository = TestCommonRepository.instance(UserAccountRepository.class);

    UserRegistrationServiceRepositorySet userRegistrationServiceRepositorySet = new UserRegistrationServiceRepositorySet() {
        @Override
        public UserAccountRepository<UserAccount> getUserAccountRepository() {
            return userAccountRepository;
        }

        @Override
        public UserIDGeneratorRepository getUserIdGeneratorRepository() {
            return userIdGeneratorRepository;
        }

        @Override
        public UserRepository<User, Object> getUserRepository() {
            return userRepository;
        }
    };

    LoginByAccountServiceRepositorySet loginByAccountServiceRepositorySet = new LoginByAccountServiceRepositorySet() {
        @Override
        public UserAccountRepository<UserAccount> getUserAccountRepository() {
            return userAccountRepository;
        }

        @Override
        public UserSessionRepository<UserSession> getUserSessionRepository() {
            return userSessionRepository;
        }

        @Override
        public UserSessionIDGeneratorRepository getUserSessionIdGeneratorRepository() {
            return userSessionIdGeneratorRepository;
        }

        @Override
        public UserLoginStateRepository<UserLoginState, Object> getUserLoginStateRepository() {
            return userLoginStateRepository;
        }
    };

    LoginByOpenIDServiceRepositorySet loginByOpenIDServiceRepositorySet = new LoginByOpenIDServiceRepositorySet() {


        @Override
        public OpenIDUserBindRepository<OpenIDUserBind> getOpenIDUserBindRepository() {
            return openIdUserBindRepository;
        }

        @Override
        public UserIDGeneratorRepository getUserIDGeneratorRepository() {
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
        public UserSessionIDGeneratorRepository getUserSessionIDGeneratorRepository() {
            return userSessionIdGeneratorRepository;
        }

        @Override
        public UserLoginStateRepository<UserLoginState, Object> getUserLoginStateRepository() {
            return userLoginStateRepository;
        }
    };

    LoginByOpenIDWithBanSupportServiceRepositorySet loginByOpenIDWithBanSupportServiceRepositorySet = new LoginByOpenIDWithBanSupportServiceRepositorySet() {
        @Override
        public OpenIDUserBindRepository<OpenIDUserBind> getOpenIDUserBindRepository() {
            return openIdUserBindRepository;
        }

        @Override
        public UserBanRepository<UserBan, Object> getUserBanRepository() {
            return userBanRepository;
        }

        @Override
        public UserIDGeneratorRepository getUserIDGeneratorRepository() {
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
        public UserSessionIDGeneratorRepository getUserSessionIDGeneratorRepository() {
            return userSessionIdGeneratorRepository;
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

        @Override
        public UserLoginStateRepository<UserLoginState, Object> getUserLoginStateRepository() {
            return userLoginStateRepository;
        }

        @Override
        public UserSessionRepository<UserSession> getUserSessionRepository() {
            return userSessionRepository;
        }
    };

    UserBanAutoLiftServiceRepositorySet userBanAutoLiftServiceRepositorySet = new UserBanAutoLiftServiceRepositorySet() {
        @Override
        public AutoLiftTimeRepository<AutoLiftTime, Object> getAutoLiftTimeRepository() {
            return autoLiftTimeRepository;
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
        TestUser user;

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
            this.user = (TestUser) user;
        }

        @Override
        public User getUser() {
            return user;
        }
    }

    class TestOpenIdUserBind implements OpenIDUserBind {
        String openID;
        TestUser user;

        @Override
        public void setOpenID(String openID) {
            this.openID = openID;
        }

        @Override
        public void setUser(User user) {
            this.user = (TestUser) user;
        }

        @Override
        public User getUser() {
            return user;
        }
    }

    class TestUserLoginState implements UserLoginState {
        long userID;
        UserSession currentUserSession;

        @Override
        public void setUserID(Object userID) {
            this.userID = (long) userID;
        }

        @Override
        public Object getUserID() {
            return userID;
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
        long userID;

        @Override
        public void setUserID(Object userID) {
            this.userID = (long) userID;
        }
    }

    class TestAutoLiftTime extends AutoLiftTimeBase {
        long userID;

        public TestAutoLiftTime(long liftTime) {
            this.liftTime = liftTime;
        }

        @Override
        public void setUserID(Object userID) {
            this.userID = (long) userID;
        }

        @Override
        public Object getUserID() {
            return userID;
        }


    }

    class TestUserAccount extends UserAccountBase {
        String account;
        TestUser user;

        public TestUserAccount(String account, String password) {
            this.account = account;
            this.password = password;
        }

        @Override
        public void setAccount(String account) {
            this.account = account;
        }

        @Override
        public User getUser() {
            return user;
        }

        @Override
        public void setUser(User user) {
            this.user = (TestUser) user;
        }

    }
}
