package dml.user.service.shared;

import dml.id.entity.IdGenerator;
import dml.user.entity.*;
import dml.user.repository.*;

/**
 * @author zheng chengdong
 */
public class SharedBusinessMethodsBetweenServices {
    public static UserSession logout(UserSessionRepository<UserSession> userSessionRepository,
                                     String token) {
        return userSessionRepository.remove(token);
    }

    public static UserSession createUserSession(UserSessionIDGeneratorRepository userSessionIdGeneratorRepository,
                                                UserSessionRepository<UserSession> userSessionRepository,
                                                UserSession newUserSession,
                                                User user) {
        IdGenerator<String> sessionIdGenerator = userSessionIdGeneratorRepository.take();
        newUserSession.setId(sessionIdGenerator.generateId());
        newUserSession.setUser(user);
        userSessionRepository.put(newUserSession);

        return newUserSession;
    }

    public static String newLoginKickOldLogin(UserSessionRepository<UserSession> userSessionRepository,
                                              UserLoginStateRepository<UserLoginState, Object> userLoginStateRepository,
                                              String newUserSessionId,
                                              UserLoginState newUserLoginState) {


        UserSession newUserSession = userSessionRepository.find(newUserSessionId);
        newUserLoginState.setId(newUserSession.getUser().getId());
        UserLoginState userLoginState = userLoginStateRepository.takeOrPutIfAbsent(newUserSession.getUser().getId(), newUserLoginState);
        UserSession currentUserSession = userLoginState.getCurrentUserSession();
        String removedUserSessionId = null;
        if (currentUserSession != null) {
            UserSession removedUserSession = userSessionRepository.remove(currentUserSession.getId());
            removedUserSessionId = removedUserSession.getId();
        }
        userLoginState.setCurrentUserSession(newUserSession);
        return removedUserSessionId;
    }

    public static void updateUserLoginStateForLogout(UserLoginStateRepository<UserLoginState, Object> userLoginStateRepository,
                                                     Object userId) {
        UserLoginState userLoginState = userLoginStateRepository.take(userId);
        userLoginState.setCurrentUserSession(null);
    }

    public static boolean checkBan(UserBanRepository<UserBan, Object> userBanRepository,
                                   Object userId) {
        UserBan userBan = userBanRepository.take(userId);
        return userBan != null;
    }

    public static SharedLoginByOpenIDResult loginByOpenID(OpenIDUserBindRepository<OpenIDUserBind> openIDUserBindRepository,
                                                          UserIDGeneratorRepository userIDGeneratorRepository,
                                                          UserRepository<User, Object> userRepository,
                                                          String openID,
                                                          User newUser,
                                                          OpenIDUserBind newOpenIDUserBind) {


        SharedLoginByOpenIDResult result = new SharedLoginByOpenIDResult();

        OpenIDUserBind openIDUserBind = openIDUserBindRepository.find(openID);
        if (openIDUserBind == null) {
            //需要创建新用户
            newOpenIDUserBind.setId(openID);
            OpenIDUserBind existsOpenIDUserBind = openIDUserBindRepository.putIfAbsent(newOpenIDUserBind);
            if (existsOpenIDUserBind != null) {
                openIDUserBind = existsOpenIDUserBind;

                result.setCreateNewUser(false);
            } else {
                IdGenerator<Object> userIDGenerator = userIDGeneratorRepository.take();
                newUser.setId(userIDGenerator.generateId());
                userRepository.put(newUser);
                newOpenIDUserBind.setUser(newUser);
                openIDUserBind = newOpenIDUserBind;

                result.setCreateNewUser(true);
            }
        } else {
            result.setCreateNewUser(false);
        }
        result.setOpenIDUserBind(openIDUserBind);

        return result;
    }

}
