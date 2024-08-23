import dml.common.repository.TestCommonRepository;
import dml.common.repository.TestCommonSingletonRepository;
import dml.id.entity.LongIdGenerator;
import dml.id.entity.UUIDStyleRandomStringIdGenerator;
import dml.keepalive.entity.AliveKeeperBase;
import dml.keepalive.repository.AliveKeeperRepository;
import dml.user.entity.*;
import dml.user.repository.*;
import dml.user.service.*;
import dml.user.service.repositoryset.*;
import dml.user.service.result.CheckToLiftAndUnsetAutoLiftResult;
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

        long currentTime = System.currentTimeMillis();
        long sessionKeepAliveInterval = 1000L;
        String openId1 = "1";
        LoginByOpenIDResult openidLoginResult1 = LoginByOpenIDService.loginByOpenID(loginByOpenIDServiceRepositorySet,
                openId1,
                currentTime,
                new TestUser(),
                new TestSession(),
                new TestSessionAliveKeeper(),
                new TestOpenIdUserBind(),
                new TestUserCurrentSession());
        assertTrue(openidLoginResult1.isCreateNewUser());
        assertNotNull(openidLoginResult1.getNewUserSession().getUserID());
        assertNotNull(openidLoginResult1.getNewUserSession());
        String kickedSessionId1 = openidLoginResult1.getRemovedUserSessionID();
        assertNull(kickedSessionId1);

        Object userID1 = AuthService.auth(authServiceRepositorySet,
                openidLoginResult1.getNewUserSession().getId());
        assertEquals(openidLoginResult1.getNewUserSession().getUserID(), userID1);

        LoginByOpenIDResult openidLoginResult2 = LoginByOpenIDService.loginByOpenID(loginByOpenIDServiceRepositorySet,
                openId1,
                currentTime,
                new TestUser(),
                new TestSession(),
                new TestSessionAliveKeeper(),
                new TestOpenIdUserBind(),
                new TestUserCurrentSession());
        assertFalse(openidLoginResult2.isCreateNewUser());
        assertEquals(openidLoginResult1.getNewUserSession().getId(), openidLoginResult2.getRemovedUserSessionID());

        Object userID2 = AuthService.auth(authServiceRepositorySet,
                openidLoginResult1.getNewUserSession().getId());
        assertNull(userID2);

        LoginByOpenIDService.logout(loginByOpenIDServiceRepositorySet,
                openidLoginResult2.getNewUserSession().getId());
        Object userID3 = AuthService.auth(authServiceRepositorySet,
                openidLoginResult2.getNewUserSession().getId());
        assertNull(userID3);

        UserBanService.banUser(userBanServiceRepositorySet,
                userID1,
                new TestUserBan()
        );
        UserBanAutoLiftService.setAutoLift(userBanAutoLiftServiceRepositorySet,
                userID1
                , new TestAutoLiftTime(currentTime + 60 * 1000L));
        Object userID4 = AuthService.auth(authServiceRepositorySet,
                openidLoginResult1.getNewUserSession().getId());
        assertNull(userID4);

        currentTime += (30 * 1000L);

        boolean isBan1 = UserBanService.checkBan(userBanServiceRepositorySet,
                userID1);
        assertTrue(isBan1);
        CheckToLiftAndUnsetAutoLiftResult checkToLiftAndUnsetAutoLiftResult1 = UserBanAutoLiftService.checkToLiftAndUnsetAutoLift(userBanAutoLiftServiceRepositorySet,
                userID1,
                currentTime);
        assertFalse(checkToLiftAndUnsetAutoLiftResult1.isToLift());

        currentTime += (31 * 1000L);

        CheckToLiftAndUnsetAutoLiftResult checkToLiftAndUnsetAutoLiftResult2 = UserBanAutoLiftService.checkToLiftAndUnsetAutoLift(userBanAutoLiftServiceRepositorySet,
                userID1,
                currentTime);
        assertTrue(checkToLiftAndUnsetAutoLiftResult2.isToLift());

        UserBanService.liftBan(userBanServiceRepositorySet,
                userID1);
        boolean isBan2 = UserBanService.checkBan(userBanServiceRepositorySet,
                userID1);
        assertFalse(isBan2);
    }

    @Test
    public void accountIdLogin() {
        long currentTime = System.currentTimeMillis();
        long sessionKeepAliveInterval = 1000L;

        RegisterNewUserResult registerNewUserResult1 = UserRegistrationService
                .registerNewUser(userRegistrationServiceRepositorySet, "account1", "pass1",
                        new TestUserAccount("account1"),
                        new TestUser());
        assertFalse(registerNewUserResult1.isAccountExists());

        LoginByAccountPasswordResult loginByAccountPasswordResult1 = LoginByAccountService.loginByAccountPassword(loginByAccountServiceRepositorySet,
                "account1",
                "pass1",
                currentTime,
                new TestSession(),
                new TestSessionAliveKeeper(),
                new TestUserCurrentSession());
        assertTrue(loginByAccountPasswordResult1.isLoginSuccess());

        Object userID1 = AuthService.auth(authServiceRepositorySet,
                loginByAccountPasswordResult1.getNewUserSession().getId());
        assertEquals(loginByAccountPasswordResult1.getNewUserSession().getUserID(), userID1);

        LoginByAccountService.logout(loginByAccountServiceRepositorySet,
                loginByAccountPasswordResult1.getNewUserSession().getId());

        Object userID2 = AuthService.auth(authServiceRepositorySet,
                loginByAccountPasswordResult1.getNewUserSession().getId());
        assertNull(userID2);

        LoginByAccountPasswordResult accountPasswordKickLoginResult1 = LoginByAccountService.loginByAccountPassword(loginByAccountServiceRepositorySet,
                "account1",
                "pass1",
                currentTime,
                new TestSession(),
                new TestSessionAliveKeeper(),
                new TestUserCurrentSession());
        LoginByAccountPasswordResult accountPasswordKickLoginResult2 = LoginByAccountService.loginByAccountPassword(loginByAccountServiceRepositorySet,
                "account1",
                "pass1",
                currentTime,
                new TestSession(),
                new TestSessionAliveKeeper(),
                new TestUserCurrentSession());
        Object userID3 = AuthService.auth(authServiceRepositorySet,
                accountPasswordKickLoginResult1.getNewUserSession().getId());
        assertNull(userID3);

    }

    OpenIDUserBindRepository<OpenIDUserBind> openIdUserBindRepository = TestCommonRepository.instance(OpenIDUserBindRepository.class);
    UserIDGeneratorRepository userIdGeneratorRepository = TestCommonSingletonRepository.instance(UserIDGeneratorRepository.class,
            new LongIdGenerator(1L) {
            });
    UserRepository userRepository = TestCommonRepository.instance(UserRepository.class);
    UserCurrentSessionRepository userCurrentSessionRepository = TestCommonRepository.instance(UserCurrentSessionRepository.class);
    UserSessionRepository userSessionRepository = TestCommonRepository.instance(UserSessionRepository.class);
    AliveKeeperRepository sessionAliveKeeperRepository = TestCommonRepository.instance(AliveKeeperRepository.class);
    UserSessionIDGeneratorRepository userSessionIdGeneratorRepository = TestCommonSingletonRepository.instance(UserSessionIDGeneratorRepository.class,
            new UUIDStyleRandomStringIdGenerator() {
            });
    UserBanRepository userBanRepository = TestCommonRepository.instance(UserBanRepository.class);
    AutoLiftTimeRepository autoLiftTimeRepository = TestCommonRepository.instance(AutoLiftTimeRepository.class);
    UserAccountRepository userAccountRepository = TestCommonRepository.instance(UserAccountRepository.class);

    UserRegistrationServiceRepositorySet userRegistrationServiceRepositorySet = new UserRegistrationServiceRepositorySet() {
        @Override
        public UserAccountRepository getUserAccountRepository() {
            return userAccountRepository;
        }

        @Override
        public UserIDGeneratorRepository getUserIdGeneratorRepository() {
            return userIdGeneratorRepository;
        }

        @Override
        public UserRepository getUserRepository() {
            return userRepository;
        }
    };

    LoginByAccountServiceRepositorySet loginByAccountServiceRepositorySet = new LoginByAccountServiceRepositorySet() {
        @Override
        public UserAccountRepository getUserAccountRepository() {
            return userAccountRepository;
        }

        @Override
        public UserSessionRepository getUserSessionRepository() {
            return userSessionRepository;
        }

        @Override
        public UserSessionIDGeneratorRepository getUserSessionIdGeneratorRepository() {
            return userSessionIdGeneratorRepository;
        }

        @Override
        public UserCurrentSessionRepository getUserCurrentSessionRepository() {
            return userCurrentSessionRepository;
        }

        @Override
        public AliveKeeperRepository getSessionAliveKeeperRepository() {
            return sessionAliveKeeperRepository;
        }


    };

    LoginByOpenIDServiceRepositorySet loginByOpenIDServiceRepositorySet = new LoginByOpenIDServiceRepositorySet() {


        @Override
        public OpenIDUserBindRepository getOpenIDUserBindRepository() {
            return openIdUserBindRepository;
        }

        @Override
        public UserIDGeneratorRepository getUserIDGeneratorRepository() {
            return userIdGeneratorRepository;
        }

        @Override
        public UserRepository getUserRepository() {
            return userRepository;
        }

        @Override
        public UserSessionRepository getUserSessionRepository() {
            return userSessionRepository;
        }

        @Override
        public UserSessionIDGeneratorRepository getUserSessionIDGeneratorRepository() {
            return userSessionIdGeneratorRepository;
        }

        @Override
        public UserCurrentSessionRepository getUserCurrentSessionRepository() {
            return userCurrentSessionRepository;
        }

        @Override
        public AliveKeeperRepository getSessionAliveKeeperRepository() {
            return sessionAliveKeeperRepository;
        }
    };

    LoginByOpenIDWithBanSupportServiceRepositorySet loginByOpenIDWithBanSupportServiceRepositorySet = new LoginByOpenIDWithBanSupportServiceRepositorySet() {
        @Override
        public OpenIDUserBindRepository getOpenIDUserBindRepository() {
            return openIdUserBindRepository;
        }

        @Override
        public UserBanRepository getUserBanRepository() {
            return userBanRepository;
        }

        @Override
        public UserIDGeneratorRepository getUserIDGeneratorRepository() {
            return userIdGeneratorRepository;
        }

        @Override
        public UserRepository getUserRepository() {
            return userRepository;
        }

        @Override
        public UserSessionRepository getUserSessionRepository() {
            return userSessionRepository;
        }

        @Override
        public UserSessionIDGeneratorRepository getUserSessionIDGeneratorRepository() {
            return userSessionIdGeneratorRepository;
        }

        @Override
        public UserCurrentSessionRepository getUserCurrentSessionRepository() {
            return userCurrentSessionRepository;
        }

        @Override
        public AliveKeeperRepository getSessionAliveKeeperRepository() {
            return sessionAliveKeeperRepository;
        }
    };

    AuthServiceRepositorySet authServiceRepositorySet = new AuthServiceRepositorySet() {

        @Override
        public UserSessionRepository getUserSessionRepository() {
            return userSessionRepository;
        }
    };

    UserBanServiceRepositorySet userBanServiceRepositorySet = new UserBanServiceRepositorySet() {
        @Override
        public UserBanRepository getUserBanRepository() {
            return userBanRepository;
        }

        @Override
        public UserCurrentSessionRepository getUserCurrentSessionRepository() {
            return userCurrentSessionRepository;
        }

        @Override
        public UserSessionRepository getUserSessionRepository() {
            return userSessionRepository;
        }

        @Override
        public AliveKeeperRepository getSessionAliveKeeperRepository() {
            return sessionAliveKeeperRepository;
        }
    };

    UserBanAutoLiftServiceRepositorySet userBanAutoLiftServiceRepositorySet = new UserBanAutoLiftServiceRepositorySet() {
        @Override
        public AutoLiftTimeRepository getAutoLiftTimeRepository() {
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
        Object userID;

        @Override
        public String getId() {
            return id;
        }

        @Override
        public void setId(String id) {
            this.id = id;
        }

        @Override
        public void setUserID(Object userID) {
            this.userID = userID;
        }

        @Override
        public Object getUserID() {
            return userID;
        }

    }

    class TestSessionAliveKeeper extends AliveKeeperBase {
        String sessionId;

        @Override
        public void setId(Object id) {
            this.sessionId = (String) id;
        }

        @Override
        public Object getId() {
            return sessionId;
        }
    }

    class TestOpenIdUserBind implements OpenIDUserBind {
        String openID;
        Object userID;

        @Override
        public void setOpenID(String openID) {
            this.openID = openID;
        }

        @Override
        public void setUserID(Object userID) {
            this.userID = userID;
        }

        @Override
        public Object getUserID() {
            return userID;
        }
    }

    class TestUserCurrentSession implements UserCurrentSession {
        long userID;
        String currentUserSessionID;

        @Override
        public void setUserID(Object userID) {
            this.userID = (long) userID;
        }

        @Override
        public Object getUserID() {
            return userID;
        }

        @Override
        public String getCurrentSessionID() {
            return currentUserSessionID;
        }

        @Override
        public void setCurrentSessionID(String currentSessionID) {
            this.currentUserSessionID = currentSessionID;
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
        Object userID;

        public TestUserAccount(String account) {
            this.account = account;
        }

        @Override
        public void setAccount(String account) {
            this.account = account;
        }

        @Override
        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public Object getUserID() {
            return userID;
        }

        @Override
        public void setUserID(Object userID) {
            this.userID = userID;
        }

    }
}
