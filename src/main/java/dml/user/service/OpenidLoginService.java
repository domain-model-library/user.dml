package dml.user.service;

import dml.id.entity.IdGenerator;
import dml.user.entity.OpenIdUserBind;
import dml.user.entity.User;
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
                                                OpenIdUserBind newOpenIdUserBind) {

        OpenIdUserBindRepository<OpenIdUserBind> openIdUserBindRepository = repositorySet.getOpenIdUserBindRepository();
        UserIdGeneratorRepository userIdGeneratorRepository = repositorySet.getUserIdGeneratorRepository();
        UserRepository<User, Object> userRepository = repositorySet.getUserRepository();
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

        result.setNewUserSession(SharedBusinessMethodsBetweenServices.createUserSession(userSessionIdGeneratorRepository,
                userSessionRepository,
                newUserSession,
                openIdUserBind.getUser()));

        return result;
    }

    public static UserSession logout(OpenidLoginServiceRepositorySet repositorySet,
                                     String token) {

        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();

        return SharedBusinessMethodsBetweenServices.logout(userSessionRepository, token);
    }

}
