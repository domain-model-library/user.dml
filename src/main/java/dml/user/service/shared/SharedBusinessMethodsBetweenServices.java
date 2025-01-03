package dml.user.service.shared;

import dml.id.entity.IdGenerator;
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
                                     UserSessionAliveKeeperRepository sessionAliveKeeperRepository,
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

    public static UserSession createUserSession(UserSessionRepository<UserSession> userSessionRepository,
                                                AliveKeeperRepository<UserSessionAliveKeeper, String> sessionAliveKeeperRepository,
                                                UserSession newUserSession,
                                                Object userID,
                                                long currentTime) {
        newUserSession.setUserID(userID);
        userSessionRepository.put(newUserSession);

        KeepAliveService.createAliveKeeper(new AliveKeeperServiceRepositorySet() {
            @Override
            public AliveKeeperRepository getAliveKeeperRepository() {
                return sessionAliveKeeperRepository;
            }
        }, newUserSession.getId(), currentTime, new UserSessionAliveKeeper());

        return newUserSession;
    }

    public static String newLoginKickOldLogin(UserSessionRepository<UserSession> userSessionRepository,
                                              UserCurrentSessionRepository userCurrentSessionRepository,
                                              AliveKeeperRepository<UserSessionAliveKeeper, String> sessionAliveKeeperRepository,
                                              String newUserSessionId) {


        UserSession newUserSession = userSessionRepository.find(newUserSessionId);
        UserCurrentSession newUserCurrentSession = new UserCurrentSession();
        newUserCurrentSession.setUserID(newUserSession.getUserID());
        UserCurrentSession userCurrentSession = userCurrentSessionRepository.takeOrPutIfAbsent(newUserSession.getUserID(), newUserCurrentSession);
        String currentUserSessionID = userCurrentSession.getCurrentSessionID();
        String removedUserSessionId = null;
        if (currentUserSessionID != null) {
            UserSession removedUserSession = userSessionRepository.remove(currentUserSessionID);
            if (removedUserSession != null) {
                removedUserSessionId = removedUserSession.getId();
                KeepAliveService.removeAliveKeeper(new AliveKeeperServiceRepositorySet() {
                    @Override
                    public AliveKeeperRepository getAliveKeeperRepository() {
                        return sessionAliveKeeperRepository;
                    }
                }, removedUserSessionId);
            }
        }
        userCurrentSession.setCurrentSessionID(newUserSession.getId());
        return removedUserSessionId;
    }

    public static void updateUserCurrentSessionForLogout(UserCurrentSessionRepository userCurrentSessionRepository,
                                                         Object userId) {
        UserCurrentSession userCurrentSession = userCurrentSessionRepository.take(userId);
        userCurrentSession.setCurrentSessionID(null);
    }

    public static boolean checkBan(UserBanRepository<UserBan, Object> userBanRepository,
                                   Object userId) {
        UserBan userBan = userBanRepository.take(userId);
        return userBan != null;
    }

    public static SharedLoginByOpenIDResult loginByOpenID(OpenIDUserBindRepository openIDUserBindRepository,
                                                          UserIDGeneratorRepository userIDGeneratorRepository,
                                                          UserRepository<User, Object> userRepository,
                                                          String openID,
                                                          User newUser) {


        SharedLoginByOpenIDResult result = new SharedLoginByOpenIDResult();

        OpenIDUserBind openIDUserBind = openIDUserBindRepository.find(openID);
        if (openIDUserBind == null) {
            //需要创建新用户
            OpenIDUserBind newOpenIDUserBind = new OpenIDUserBind();
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

    public static UserSession checkSessionDeadAndRemove(UserSessionRepository<UserSession> userSessionRepository,
                                                        UserSessionAliveKeeperRepository sessionAliveKeeperRepository,
                                                        String sessionId,
                                                        long currentTime,
                                                        long sessionKeepAliveInterval) {

        boolean alive = KeepAliveService.isAlive(getAliveKeeperServiceRepositorySet(sessionAliveKeeperRepository),
                sessionId, currentTime, sessionKeepAliveInterval);
        if (!alive) {
            UserSession removedSession = userSessionRepository.remove(sessionId);
            KeepAliveService.removeAliveKeeper(getAliveKeeperServiceRepositorySet(sessionAliveKeeperRepository)
                    , sessionId);
            return removedSession;
        }
        return null;

    }

    private static AliveKeeperServiceRepositorySet getAliveKeeperServiceRepositorySet(
            UserSessionAliveKeeperRepository sessionAliveKeeperRepository) {
        return new AliveKeeperServiceRepositorySet() {
            @Override
            public AliveKeeperRepository getAliveKeeperRepository() {
                return sessionAliveKeeperRepository;
            }
        };
    }

}
