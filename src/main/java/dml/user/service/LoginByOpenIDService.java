package dml.user.service;

import dml.user.entity.OpenIDUserBind;
import dml.user.entity.User;
import dml.user.entity.UserCurrentSession;
import dml.user.entity.UserSession;
import dml.user.repository.*;
import dml.user.service.repositoryset.LoginByOpenIDServiceRepositorySet;
import dml.user.service.result.LoginByOpenIDResult;
import dml.user.service.shared.SharedBusinessMethodsBetweenServices;
import dml.user.service.shared.SharedLoginByOpenIDResult;

public class LoginByOpenIDService {

    /**
     * 通过openID登录。如果openID不存在，则创建新用户。登录成功后，创建用户会话，如果用户已经登录，则踢掉旧登录。
     *
     * @param repositorySet     仓储集合
     * @param openID            openID
     * @param newUser           新用户
     * @param newUserSession    新用户会话
     * @param newOpenIDUserBind 新openID用户绑定
     * @return 登录结果
     */
    public static LoginByOpenIDResult loginByOpenID(LoginByOpenIDServiceRepositorySet repositorySet,
                                                    String openID,
                                                    User newUser,
                                                    UserSession newUserSession,
                                                    OpenIDUserBind newOpenIDUserBind,
                                                    UserCurrentSession newUserCurrentSession) {

        OpenIDUserBindRepository<OpenIDUserBind> openIDUserBindRepository = repositorySet.getOpenIDUserBindRepository();
        UserIDGeneratorRepository userIDGeneratorRepository = repositorySet.getUserIDGeneratorRepository();
        UserRepository<User, Object> userRepository = repositorySet.getUserRepository();
        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        UserSessionIDGeneratorRepository userSessionIDGeneratorRepository = repositorySet.getUserSessionIDGeneratorRepository();
        UserCurrentSessionRepository<UserCurrentSession, Object> userCurrentSessionRepository = repositorySet.getUserCurrentSessionRepository();

        LoginByOpenIDResult result = new LoginByOpenIDResult();

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
                userCurrentSessionRepository,
                newUserSession.getId(),
                newUserCurrentSession
        );
        result.setRemovedUserSessionID(removedUserSessionID);

        return result;
    }

    public static UserSession logout(LoginByOpenIDServiceRepositorySet repositorySet,
                                     String token) {

        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        UserCurrentSessionRepository<UserCurrentSession, Object> userCurrentSessionRepository = repositorySet.getUserCurrentSessionRepository();

        UserSession removedUserSession = SharedBusinessMethodsBetweenServices.logout(userSessionRepository, token);

        SharedBusinessMethodsBetweenServices.updateUserCurrentSessionForLogout(userCurrentSessionRepository,
                removedUserSession.getUser().getId());
        return removedUserSession;

    }

}
