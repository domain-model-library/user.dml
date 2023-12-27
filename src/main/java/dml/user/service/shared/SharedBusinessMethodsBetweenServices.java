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
                                              UserCurrentSessionRepository<UserCurrentSession, Object> userCurrentSessionRepository,
                                              String newUserSessionId,
                                              UserCurrentSession newUserCurrentSession) {


        UserSession newUserSession = userSessionRepository.find(newUserSessionId);
        newUserCurrentSession.setUserID(newUserSession.getUser().getId());
        UserCurrentSession userCurrentSession = userCurrentSessionRepository.takeOrPutIfAbsent(newUserSession.getUser().getId(), newUserCurrentSession);
        UserSession currentUserSession = userCurrentSession.getCurrentSession();
        String removedUserSessionId = null;
        if (currentUserSession != null) {
            UserSession removedUserSession = userSessionRepository.remove(currentUserSession.getId());
            removedUserSessionId = removedUserSession.getId();
        }
        userCurrentSession.setCurrentSession(newUserSession);
        return removedUserSessionId;
    }

    public static void updateUserCurrentSessionForLogout(UserCurrentSessionRepository<UserCurrentSession, Object> userCurrentSessionRepository,
                                                         Object userId) {
        UserCurrentSession userCurrentSession = userCurrentSessionRepository.take(userId);
        userCurrentSession.setCurrentSession(null);
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
            newOpenIDUserBind.setOpenID(openID);
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
