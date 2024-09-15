package dml.user.service;

import dml.keepalive.repository.AliveKeeperRepository;
import dml.user.entity.*;
import dml.user.repository.*;
import dml.user.service.repositoryset.LoginByOpenIDServiceRepositorySet;
import dml.user.service.repositoryset.LoginByOpenIDWithBanSupportServiceRepositorySet;
import dml.user.service.result.LoginByOpenIDWithBanCheckResult;
import dml.user.service.shared.SharedBusinessMethodsBetweenServices;
import dml.user.service.shared.SharedLoginByOpenIDResult;

public class LoginByOpenIDWithBanSupportService {

    /**
     * 通过openID登录。如果openID不存在，则创建新用户。登录成功后，创建用户会话，如果用户已经登录，则踢掉旧登录。
     * <br/>
     * 如果用户被封禁，则登录失败。
     *
     * @param repositorySet  仓储集合
     * @param openID         openID
     * @param newUser        新用户
     * @param newUserSession 新用户会话
     * @return 登录结果
     */
    public static LoginByOpenIDWithBanCheckResult loginByOpenIDWithBanCheck(LoginByOpenIDWithBanSupportServiceRepositorySet repositorySet,
                                                                            String openID,
                                                                            long currentTime,
                                                                            User newUser,
                                                                            UserSession newUserSession) {

        OpenIDUserBindRepository openIDUserBindRepository = repositorySet.getOpenIDUserBindRepository();
        UserBanRepository<UserBan, Object> userBanRepository = repositorySet.getUserBanRepository();
        UserIDGeneratorRepository userIDGeneratorRepository = repositorySet.getUserIDGeneratorRepository();
        UserRepository<User, Object> userRepository = repositorySet.getUserRepository();
        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        UserSessionIDGeneratorRepository userSessionIDGeneratorRepository = repositorySet.getUserSessionIDGeneratorRepository();
        UserCurrentSessionRepository userCurrentSessionRepository = repositorySet.getUserCurrentSessionRepository();
        AliveKeeperRepository<UserSessionAliveKeeper, String> sessionAliveKeeperRepository = repositorySet.getSessionAliveKeeperRepository();

        LoginByOpenIDWithBanCheckResult result = new LoginByOpenIDWithBanCheckResult();

        OpenIDUserBind openIdUserBind = openIDUserBindRepository.take(openID);
        if (openIdUserBind != null) {
            boolean ban = SharedBusinessMethodsBetweenServices.checkBan(userBanRepository, openIdUserBind.getUserID());
            if (ban) {
                result.setBan(true);
                return result;
            }
        }

        SharedLoginByOpenIDResult sharedLoginByOpenIDResult = SharedBusinessMethodsBetweenServices.loginByOpenID(
                openIDUserBindRepository,
                userIDGeneratorRepository,
                userRepository,
                openID,
                newUser);
        result.setCreateNewUser(sharedLoginByOpenIDResult.isCreateNewUser());

        OpenIDUserBind openIDUserBind = sharedLoginByOpenIDResult.getOpenIDUserBind();
        result.setNewUserSession(SharedBusinessMethodsBetweenServices.createUserSession(userSessionIDGeneratorRepository,
                userSessionRepository,
                sessionAliveKeeperRepository,
                newUserSession,
                openIDUserBind.getUserID(),
                currentTime));

        String removedUserSessionID = SharedBusinessMethodsBetweenServices.newLoginKickOldLogin(
                userSessionRepository,
                userCurrentSessionRepository,
                sessionAliveKeeperRepository,
                newUserSession.getId()
        );
        result.setRemovedUserSessionID(removedUserSessionID);

        return result;

    }

    public static UserSession logout(LoginByOpenIDServiceRepositorySet repositorySet,
                                     String token) {

        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        UserCurrentSessionRepository userCurrentSessionRepository = repositorySet.getUserCurrentSessionRepository();
        UserSessionAliveKeeperRepository sessionAliveKeeperRepository = repositorySet.getSessionAliveKeeperRepository();

        UserSession removedUserSession = SharedBusinessMethodsBetweenServices.logout(userSessionRepository,
                sessionAliveKeeperRepository, token);

        SharedBusinessMethodsBetweenServices.updateUserCurrentSessionForLogout(userCurrentSessionRepository,
                removedUserSession.getUserID());
        return removedUserSession;

    }

}
