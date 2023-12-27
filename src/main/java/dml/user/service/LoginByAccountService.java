package dml.user.service;

import dml.user.entity.UserAccount;
import dml.user.entity.UserCurrentSession;
import dml.user.entity.UserSession;
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
                                                                      UserSession newUserSession,
                                                                      UserCurrentSession newUserCurrentSession) {

        UserAccountRepository<UserAccount> userAccountRepository = repositorySet.getUserAccountRepository();
        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        UserSessionIDGeneratorRepository userSessionIdGeneratorRepository = repositorySet.getUserSessionIdGeneratorRepository();
        UserCurrentSessionRepository<UserCurrentSession, Object> userCurrentSessionRepository = repositorySet.getUserCurrentSessionRepository();

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
                newUserSession,
                userAccount.getUser()));

        String removedUserSessionID = SharedBusinessMethodsBetweenServices.newLoginKickOldLogin(
                userSessionRepository,
                userCurrentSessionRepository,
                newUserSession.getId(),
                newUserCurrentSession
        );
        result.setRemovedUserSessionID(removedUserSessionID);

        result.setLoginSuccess(true);

        return result;
    }

    public static UserSession logout(LoginByAccountServiceRepositorySet repositorySet,
                                     String token) {

        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        UserCurrentSessionRepository<UserCurrentSession, Object> userCurrentSessionRepository = repositorySet.getUserCurrentSessionRepository();

        UserSession removedUserSession = SharedBusinessMethodsBetweenServices.logout(userSessionRepository, token);

        SharedBusinessMethodsBetweenServices.updateUserCurrentSessionForLogout(userCurrentSessionRepository,
                removedUserSession.getUser().getId());
        return removedUserSession;
    }
}
