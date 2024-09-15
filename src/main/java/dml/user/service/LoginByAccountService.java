package dml.user.service;

import dml.keepalive.entity.AliveKeeper;
import dml.keepalive.repository.AliveKeeperRepository;
import dml.user.entity.UserAccount;
import dml.user.entity.UserSession;
import dml.user.entity.UserSessionAliveKeeper;
import dml.user.repository.UserAccountRepository;
import dml.user.repository.UserCurrentSessionRepository;
import dml.user.repository.UserSessionIDGeneratorRepository;
import dml.user.repository.UserSessionRepository;
import dml.user.service.repositoryset.LoginByAccountServiceRepositorySet;
import dml.user.service.result.LoginByAccountPasswordResult;
import dml.user.service.shared.SharedBusinessMethodsBetweenServices;

public class LoginByAccountService {
    public static LoginByAccountPasswordResult loginByAccountPassword(LoginByAccountServiceRepositorySet repositorySet,
                                                                      String account,
                                                                      String password,
                                                                      long currentTime,
                                                                      UserSession newUserSession) {

        UserAccountRepository<UserAccount> userAccountRepository = repositorySet.getUserAccountRepository();
        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        UserSessionIDGeneratorRepository userSessionIdGeneratorRepository = repositorySet.getUserSessionIdGeneratorRepository();
        UserCurrentSessionRepository userCurrentSessionRepository = repositorySet.getUserCurrentSessionRepository();
        AliveKeeperRepository<UserSessionAliveKeeper, String> sessionAliveKeeperRepository = repositorySet.getSessionAliveKeeperRepository();

        LoginByAccountPasswordResult result = new LoginByAccountPasswordResult();

        UserAccount userAccount = userAccountRepository.find(account);
        if (userAccount == null) {
            result.setNoAccount(true);
            return result;
        }
        if (!userAccount.verifyPassword(password)) {
            result.setIncorrectPassword(true);
            return result;
        }

        result.setNewUserSession(SharedBusinessMethodsBetweenServices.createUserSession(userSessionIdGeneratorRepository,
                userSessionRepository,
                sessionAliveKeeperRepository,
                newUserSession,
                userAccount.getUserID(),
                currentTime));

        String removedUserSessionID = SharedBusinessMethodsBetweenServices.newLoginKickOldLogin(
                userSessionRepository,
                userCurrentSessionRepository,
                sessionAliveKeeperRepository,
                newUserSession.getId()
        );
        result.setRemovedUserSessionID(removedUserSessionID);

        result.setLoginSuccess(true);

        return result;
    }

    public static UserSession logout(LoginByAccountServiceRepositorySet repositorySet,
                                     String token) {

        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        UserCurrentSessionRepository userCurrentSessionRepository = repositorySet.getUserCurrentSessionRepository();
        AliveKeeperRepository<AliveKeeper, String> sessionAliveKeeperRepository = repositorySet.getSessionAliveKeeperRepository();

        UserSession removedUserSession = SharedBusinessMethodsBetweenServices.logout(userSessionRepository, sessionAliveKeeperRepository,
                token);

        SharedBusinessMethodsBetweenServices.updateUserCurrentSessionForLogout(userCurrentSessionRepository,
                removedUserSession.getUserID());
        return removedUserSession;
    }
}
