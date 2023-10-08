package dml.user.service;

import dml.user.entity.*;
import dml.user.repository.*;
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
     * @param repositorySet     仓储集合
     * @param openID            openID
     * @param newUser           新用户
     * @param newUserSession    新用户会话
     * @param newOpenIDUserBind 新openID用户绑定
     * @return 登录结果
     */
    public static LoginByOpenIDWithBanCheckResult loginByOpenIDWithBanCheck(LoginByOpenIDWithBanSupportServiceRepositorySet repositorySet,
                                                                            String openID,
                                                                            User newUser,
                                                                            UserSession newUserSession,
                                                                            OpenIDUserBind newOpenIDUserBind,
                                                                            UserLoginState newUserLoginState) {

        OpenIDUserBindRepository<OpenIDUserBind> openIDUserBindRepository = repositorySet.getOpenIDUserBindRepository();
        UserBanRepository<UserBan, Object> userBanRepository = repositorySet.getUserBanRepository();
        UserIDGeneratorRepository userIDGeneratorRepository = repositorySet.getUserIDGeneratorRepository();
        UserRepository<User, Object> userRepository = repositorySet.getUserRepository();
        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        UserSessionIDGeneratorRepository userSessionIDGeneratorRepository = repositorySet.getUserSessionIDGeneratorRepository();
        UserLoginStateRepository<UserLoginState, Object> userLoginStateRepository = repositorySet.getUserLoginStateRepository();

        LoginByOpenIDWithBanCheckResult result = new LoginByOpenIDWithBanCheckResult();

        OpenIDUserBind openIdUserBind = openIDUserBindRepository.take(openID);
        if (openIdUserBind != null) {
            boolean ban = SharedBusinessMethodsBetweenServices.checkBan(userBanRepository, openIdUserBind.getUser().getId());
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
                newUser,
                newOpenIDUserBind);
        result.setCreateNewUser(sharedLoginByOpenIDResult.isCreateNewUser());

        OpenIDUserBind openIDUserBind = sharedLoginByOpenIDResult.getOpenIDUserBind();
        result.setNewUserSession(SharedBusinessMethodsBetweenServices.createUserSession(userSessionIDGeneratorRepository,
                userSessionRepository,
                newUserSession,
                openIDUserBind.getUser()));

        String removedUserSessionID = SharedBusinessMethodsBetweenServices.newLoginKickOldLogin(
                userSessionRepository,
                userLoginStateRepository,
                newUserSession.getId(),
                newUserLoginState
        );
        result.setRemovedUserSessionID(removedUserSessionID);

        return result;

    }

}
