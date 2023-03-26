package dml.user.service;

import dml.id.entity.IdGenerator;
import dml.user.entity.OpenIdUserBind;
import dml.user.entity.User;
import dml.user.entity.UserLoginState;
import dml.user.entity.UserSession;
import dml.user.repository.*;
import dml.user.service.repositoryset.OpenidLoginServiceRepositorySet;
import dml.user.service.result.OpenidLoginResult;

/**
 * @author zheng chengdong
 */
public class OpenidLoginService {
    public static OpenidLoginResult openidLogin(OpenidLoginServiceRepositorySet repositorySet,
                                                String openid,
                                                User newUser,
                                                UserSession newUserSession,
                                                OpenIdUserBind newOpenIdUserBind,
                                                UserLoginState newUserLoginState) {

        OpenIdUserBindRepository<OpenIdUserBind> openIdUserBindRepository = repositorySet.getOpenIdUserBindRepository();
        UserIdGeneratorRepository userIdGeneratorRepository = repositorySet.getUserIdGeneratorRepository();
        UserRepository<User, Object> userRepository = repositorySet.getUserRepository();
        UserLoginStateRepository<UserLoginState, Object> userLoginStateRepository = repositorySet.getUserLoginStateRepository();
        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        UserSessionIdGeneratorRepository userSessionIdGeneratorRepository = repositorySet.getUserSessionIdGeneratorRepository();

        OpenidLoginResult result = new OpenidLoginResult();

        OpenIdUserBind openIdUserBind = openIdUserBindRepository.find(openid);
        if (openIdUserBind == null) {
            //需要创建新用户
            newOpenIdUserBind.setId(openid);
            OpenIdUserBind existsOpenIdUserBind = openIdUserBindRepository.putIfAbsent(newOpenIdUserBind);
            if (existsOpenIdUserBind != null) {
                openIdUserBind = existsOpenIdUserBind;

                result.setCreateNewUser(false);
            } else {
                IdGenerator<Object> userIdGenerator = userIdGeneratorRepository.take();
                newUser.setId(userIdGenerator.generateId());
                userRepository.put(newUser);
                newOpenIdUserBind.setUser(newUser);
                openIdUserBind = newOpenIdUserBind;

                result.setCreateNewUser(true);
            }
        } else {
            result.setCreateNewUser(false);
        }

        result.setUser(openIdUserBind.getUser());

        newUserLoginState.setId(openIdUserBind.getUser().getId());
        UserLoginState userLoginState = userLoginStateRepository.takeOrPutIfAbsent(newUserLoginState.getId(), newUserLoginState);
        UserSession currentUserSession = userLoginState.getCurrentUserSession();
        if (currentUserSession != null) {
            UserSession removedUserSession = userSessionRepository.remove(currentUserSession.getId());
            result.setRemovedUserSession(removedUserSession);
        }
        IdGenerator<String> sessionIdGenerator = userSessionIdGeneratorRepository.take();
        newUserSession.setId(sessionIdGenerator.generateId());
        newUserSession.setUser(openIdUserBind.getUser());
        userSessionRepository.put(newUserSession);

        userLoginState.setCurrentUserSession(newUserSession);

        result.setCurrentUserSession(newUserSession);
        return result;
    }

    public static UserSession logout(OpenidLoginServiceRepositorySet repositorySet,
                                     String token) {

        UserLoginStateRepository<UserLoginState, Object> userLoginStateRepository = repositorySet.getUserLoginStateRepository();
        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();

        UserSession userSession = userSessionRepository.remove(token);
        if (userSession == null) {
            return null;
        }

        UserLoginState userLoginState = userLoginStateRepository.take(userSession.getUser().getId());
        userLoginState.setCurrentUserSession(null);

        return userSession;
    }
}
