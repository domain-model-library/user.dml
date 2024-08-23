package dml.user.service.shared;

import dml.id.entity.IdGenerator;
import dml.keepalive.entity.AliveKeeper;
import dml.keepalive.repository.AliveKeeperRepository;
import dml.keepalive.service.KeepAliveService;
import dml.keepalive.service.repositoryset.AliveKeeperServiceRepositorySet;
import dml.user.entity.*;
import dml.user.repository.*;

/**
 * @author zheng chengdong
 */
public class SharedBusinessMethodsBetweenServices {
    public static UserSession logout(UserSessionRepository<UserSession> userSessionRepository,
                                     AliveKeeperRepository<AliveKeeper, String> sessionAliveKeeperRepository,
                                     String token) {
        UserSession removedUserSession = userSessionRepository.remove(token);
        if (removedUserSession != null) {
            KeepAliveService.removeAliveKeeper(new AliveKeeperServiceRepositorySet() {
                @Override
                public AliveKeeperRepository getAliveKeeperRepository() {
                    return sessionAliveKeeperRepository;
                }
            }, token);
        }
        return removedUserSession;
    }

    public static UserSession createUserSession(UserSessionIDGeneratorRepository userSessionIdGeneratorRepository,
                                                UserSessionRepository<UserSession> userSessionRepository,
                                                AliveKeeperRepository<AliveKeeper, String> sessionAliveKeeperRepository,
                                                UserSession newUserSession,
                                                AliveKeeper newSessionAliveKeeper,
                                                Object userID,
                                                long currentTime) {
        IdGenerator<String> sessionIdGenerator = userSessionIdGeneratorRepository.take();
        newUserSession.setId(sessionIdGenerator.generateId());
        newUserSession.setUserID(userID);
        userSessionRepository.put(newUserSession);

        KeepAliveService.createAliveKeeper(new AliveKeeperServiceRepositorySet() {
            @Override
            public AliveKeeperRepository getAliveKeeperRepository() {
                return sessionAliveKeeperRepository;
            }
        }, newUserSession.getId(), currentTime, newSessionAliveKeeper);

        return newUserSession;
    }

    public static String newLoginKickOldLogin(UserSessionRepository<UserSession> userSessionRepository,
                                              UserCurrentSessionRepository<UserCurrentSession, Object> userCurrentSessionRepository,
                                              String newUserSessionId,
                                              UserCurrentSession newUserCurrentSession) {


        UserSession newUserSession = userSessionRepository.find(newUserSessionId);
        newUserCurrentSession.setUserID(newUserSession.getUserID());
        UserCurrentSession userCurrentSession = userCurrentSessionRepository.takeOrPutIfAbsent(newUserSession.getUserID(), newUserCurrentSession);
        String currentUserSessionID = userCurrentSession.getCurrentSessionID();
        String removedUserSessionId = null;
        if (currentUserSessionID != null) {
            UserSession removedUserSession = userSessionRepository.remove(currentUserSessionID);
            removedUserSessionId = removedUserSession.getId();
        }
        userCurrentSession.setCurrentSessionID(newUserSession.getId());
        return removedUserSessionId;
    }

    public static void updateUserCurrentSessionForLogout(UserCurrentSessionRepository<UserCurrentSession, Object> userCurrentSessionRepository,
                                                         Object userId) {
        UserCurrentSession userCurrentSession = userCurrentSessionRepository.take(userId);
        userCurrentSession.setCurrentSessionID(null);
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
                newOpenIDUserBind.setUserID(newUser.getId());
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
