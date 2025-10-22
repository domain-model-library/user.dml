import dml.common.repository.TestCommonRepository;
import dml.common.repository.TestCommonSingletonRepository;
import dml.id.entity.LongIdGenerator;
import dml.id.entity.UUIDGenerator;
import dml.user.entity.User;
import dml.user.entity.UserAccountBase;
import dml.user.entity.UserBan;
import dml.user.entity.UserSession;
import dml.user.repository.*;
import dml.user.service.*;
import dml.user.service.repositoryset.*;
import dml.user.service.result.CheckToLiftAndUnsetAutoLiftResult;
import dml.user.service.result.LoginByAccountPasswordResult;
import dml.user.service.result.LoginByOpenIDResult;
import dml.user.service.result.RegisterNewUserResult;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

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
                new TestSession(UUID.randomUUID().toString()));
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
                new TestSession(UUID.randomUUID().toString()));
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
                userID1,
                currentTime + 60 * 1000L);
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
                new TestSession(UUID.randomUUID().toString()));
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
                new TestSession(UUID.randomUUID().toString()));
        LoginByAccountPasswordResult accountPasswordKickLoginResult2 = LoginByAccountService.loginByAccountPassword(loginByAccountServiceRepositorySet,
                "account1",
                "pass1",
                currentTime,
                new TestSession(UUID.randomUUID().toString()));
        Object userID3 = AuthService.auth(authServiceRepositorySet,
                accountPasswordKickLoginResult1.getNewUserSession().getId());
        assertNull(userID3);

    }

    @Test
    public void clearSession() {
        long currentTime = System.currentTimeMillis();
        long sessionKeepAliveInterval = 1000L;
        int sessionBatchSize = 10;
        long maxSegmentExecutionTime = 1000L;
        long maxTimeToTaskReady = 1000L;
        String openId1 = "1";
        LoginByOpenIDResult openidLoginResult1 = LoginByOpenIDService.loginByOpenID(loginByOpenIDServiceRepositorySet,
                openId1,
                currentTime,
                new TestUser(),
                new TestSession(UUID.randomUUID().toString()));

        //创建清理任务
        boolean createTaskSuccess = UserSessionCleanupTaskService.createUserSessionCleanupTask(userSessionCleanupTaskServiceRepositorySet,
                currentTime);
        assertTrue(createTaskSuccess);

        //给任务分配sessionId
        UserSessionCleanupTaskService.addAllSessionIdToUserSessionCleanupTask(userSessionCleanupTaskServiceRepositorySet,
                sessionBatchSize,
                List.of(openidLoginResult1.getNewUserSession().getId()));

        //取出任务段，准备执行
        String segmentId = UserSessionCleanupTaskService.takeUserSessionCleanupTaskSegmentToExecute(userSessionCleanupTaskServiceRepositorySet,
                currentTime,
                maxSegmentExecutionTime,
                maxTimeToTaskReady);
        assertNotNull(segmentId);

        //执行任务段
        UserSessionCleanupTaskService.executeUserSessionCleanupTaskSegment(userSessionCleanupTaskServiceRepositorySet,
                segmentId,
                currentTime,
                sessionKeepAliveInterval);

        //验证会话,会话停滞时间不够没有清理掉，验证成功
        Object userID1 = AuthService.auth(authServiceRepositorySet,
                openidLoginResult1.getNewUserSession().getId());
        assertNotNull(userID1);

        //再次取出任务段，目的是触发验证发现任务段都完成，任务完成，然后删除任务
        segmentId = UserSessionCleanupTaskService.takeUserSessionCleanupTaskSegmentToExecute(userSessionCleanupTaskServiceRepositorySet,
                currentTime,
                maxSegmentExecutionTime,
                maxTimeToTaskReady);
        assertNull(segmentId);

        //这时候上一个任务已经执行完毕删除了，需要重新创建任务
        createTaskSuccess = UserSessionCleanupTaskService.createUserSessionCleanupTask(userSessionCleanupTaskServiceRepositorySet,
                currentTime);
        assertTrue(createTaskSuccess);

        //给任务分配sessionId
        UserSessionCleanupTaskService.addAllSessionIdToUserSessionCleanupTask(userSessionCleanupTaskServiceRepositorySet,
                sessionBatchSize,
                List.of(openidLoginResult1.getNewUserSession().getId()));

        //时间过去了，再次执行清理任务
        currentTime += sessionKeepAliveInterval + 1;
        segmentId = UserSessionCleanupTaskService.takeUserSessionCleanupTaskSegmentToExecute(userSessionCleanupTaskServiceRepositorySet,
                currentTime,
                maxSegmentExecutionTime,
                maxTimeToTaskReady);
        assertNotNull(segmentId);

        UserSessionCleanupTaskService.executeUserSessionCleanupTaskSegment(userSessionCleanupTaskServiceRepositorySet,
                segmentId,
                currentTime,
                sessionKeepAliveInterval);

        //验证会话,会话停滞时间够清理掉，验证失败
        Object userID2 = AuthService.auth(authServiceRepositorySet,
                openidLoginResult1.getNewUserSession().getId());
        assertNull(userID2);

    }

    OpenIDUserBindRepository openIdUserBindRepository = TestCommonRepository.instance(OpenIDUserBindRepository.class);
    UserIDGeneratorRepository userIdGeneratorRepository = TestCommonSingletonRepository.instance(UserIDGeneratorRepository.class,
            new LongIdGenerator(1L));
    UserRepository userRepository = TestCommonRepository.instance(UserRepository.class);
    UserCurrentSessionRepository userCurrentSessionRepository = TestCommonRepository.instance(UserCurrentSessionRepository.class);
    UserSessionRepository userSessionRepository = TestCommonRepository.instance(UserSessionRepository.class);
    UserSessionAliveKeeperRepository userSessionAliveKeeperRepository = TestCommonRepository.instance(UserSessionAliveKeeperRepository.class);
    UserBanRepository userBanRepository = TestCommonRepository.instance(UserBanRepository.class);
    AutoLiftTimeRepository autoLiftTimeRepository = TestCommonRepository.instance(AutoLiftTimeRepository.class);
    UserAccountRepository userAccountRepository = TestCommonRepository.instance(UserAccountRepository.class);
    ClearSessionTaskRepository clearSessionTaskRepository = TestCommonSingletonRepository.instance(ClearSessionTaskRepository.class);
    ClearSessionTaskSegmentRepository clearSessionTaskSegmentRepository = TestCommonRepository.instance(ClearSessionTaskSegmentRepository.class);
    ClearSessionTaskSegmentIDGeneratorRepository clearSessionTaskSegmentIDGeneratorRepository = TestCommonSingletonRepository.instance(ClearSessionTaskSegmentIDGeneratorRepository.class,
            new UUIDGenerator());

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
        public UserCurrentSessionRepository getUserCurrentSessionRepository() {
            return userCurrentSessionRepository;
        }

        @Override
        public UserSessionAliveKeeperRepository getUserSessionAliveKeeperRepository() {
            return userSessionAliveKeeperRepository;
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
        public UserCurrentSessionRepository getUserCurrentSessionRepository() {
            return userCurrentSessionRepository;
        }

        @Override
        public UserSessionAliveKeeperRepository getUserSessionAliveKeeperRepository() {
            return userSessionAliveKeeperRepository;
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
        public UserCurrentSessionRepository getUserCurrentSessionRepository() {
            return userCurrentSessionRepository;
        }

        @Override
        public UserSessionAliveKeeperRepository getUserSessionAliveKeeperRepository() {
            return userSessionAliveKeeperRepository;
        }
    };

    UserSessionCleanupServiceRepositorySet userSessionCleanupServiceRepositorySet = new UserSessionCleanupServiceRepositorySet() {
        @Override
        public UserSessionRepository getUserSessionRepository() {
            return userSessionRepository;
        }

        @Override
        public UserSessionAliveKeeperRepository getUserSessionAliveKeeperRepository() {
            return userSessionAliveKeeperRepository;
        }
    };

    UserSessionCleanupTaskServiceRepositorySet userSessionCleanupTaskServiceRepositorySet = new UserSessionCleanupTaskServiceRepositorySet() {
        @Override
        public ClearSessionTaskRepository getClearSessionTaskRepository() {
            return clearSessionTaskRepository;
        }

        @Override
        public ClearSessionTaskSegmentRepository getClearSessionTaskSegmentRepository() {
            return clearSessionTaskSegmentRepository;
        }

        @Override
        public ClearSessionTaskSegmentIDGeneratorRepository getClearSessionTaskSegmentIDGeneratorRepository() {
            return clearSessionTaskSegmentIDGeneratorRepository;
        }

        @Override
        public UserSessionRepository getUserSessionRepository() {
            return userSessionRepository;
        }

        @Override
        public UserSessionAliveKeeperRepository getUserSessionAliveKeeperRepository() {
            return userSessionAliveKeeperRepository;
        }
    };

    AuthServiceRepositorySet authServiceRepositorySet = new AuthServiceRepositorySet() {

        @Override
        public UserSessionRepository getUserSessionRepository() {
            return userSessionRepository;
        }

        @Override
        public UserSessionAliveKeeperRepository getUserSessionAliveKeeperRepository() {
            return userSessionAliveKeeperRepository;
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
        public UserSessionAliveKeeperRepository getUserSessionAliveKeeperRepository() {
            return userSessionAliveKeeperRepository;
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

        TestSession(String id) {
            this.id = id;
        }

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

    class TestUserBan implements UserBan {
        long userID;

        @Override
        public void setUserID(Object userID) {
            this.userID = (long) userID;
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
