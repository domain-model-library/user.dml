package dml.user.service;

import dml.id.entity.IdGenerator;
import dml.user.entity.OpenIdUserBind;
import dml.user.entity.Session;
import dml.user.entity.User;
import dml.user.entity.UserLoginState;
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
                                                Session newSession,
                                                OpenIdUserBind newOpenIdUserBind,
                                                UserLoginState newUserLoginState) {

        OpenIdUserBindRepository<OpenIdUserBind> openIdUserBindRepository = repositorySet.getOpenIdUserBindRepository();
        UserIdGeneratorRepository userIdGeneratorRepository = repositorySet.getUserIdGeneratorRepository();
        UserRepository<User, Object> userRepository = repositorySet.getUserRepository();
        UserLoginStateRepository<UserLoginState> userLoginStateRepository = repositorySet.getUserLoginStateRepository();
        SessionRepository<Session> sessionRepository = repositorySet.getSessionRepository();
        SessionIdGeneratorRepository sessionIdGeneratorRepository = repositorySet.getSessionIdGeneratorRepository();

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
        Session currentSession = userLoginState.getCurrentSession();
        if (currentSession != null) {
            Session removedSession = sessionRepository.remove(currentSession.getId());
            result.setRemovedSession(removedSession);
        }
        IdGenerator<String> sessionIdGenerator = sessionIdGeneratorRepository.take();
        newSession.setId(sessionIdGenerator.generateId());
        sessionRepository.put(newSession);

        userLoginState.setCurrentSession(newSession);

        result.setCurrentSession(newSession);
        return result;
    }
}
