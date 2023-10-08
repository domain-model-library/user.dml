package dml.user.service;

import dml.user.entity.User;
import dml.user.entity.UserAccount;
import dml.user.entity.UserLoginState;
import dml.user.entity.UserSession;
import dml.user.repository.*;
import dml.user.service.repositoryset.LoginByAccountServiceRepositorySet;
import dml.user.service.result.LoginByAccountPasswordResult;
import dml.user.service.result.RegisterNewUserResult;
import dml.user.service.shared.SharedBusinessMethodsBetweenServices;

public class LoginByAccountService {
    public static LoginByAccountPasswordResult loginByAccountPassword(LoginByAccountServiceRepositorySet repositorySet,
                                                                      String account,
                                                                      String password,
                                                                      UserSession newUserSession,
                                                                      UserLoginState newUserLoginState) {

        UserAccountRepository<UserAccount> userAccountRepository = repositorySet.getUserAccountRepository();
        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        UserSessionIDGeneratorRepository userSessionIdGeneratorRepository = repositorySet.getUserSessionIdGeneratorRepository();
        UserLoginStateRepository<UserLoginState, Object> userLoginStateRepository = repositorySet.getUserLoginStateRepository();

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
                userLoginStateRepository,
                newUserSession.getId(),
                newUserLoginState
        );
        result.setRemovedUserSessionID(removedUserSessionID);

        result.setLoginSuccess(true);

        return result;
    }

    public static RegisterNewUserResult registerNewUser(LoginByAccountServiceRepositorySet repositorySet,
                                                        UserAccount newUserAccount,
                                                        User newUser) {

        UserAccountRepository<UserAccount> userAccountRepository = repositorySet.getUserAccountRepository();
        UserIDGeneratorRepository userIdGeneratorRepository = repositorySet.getUserIdGeneratorRepository();
        UserRepository<User, Object> userRepository = repositorySet.getUserRepository();

        RegisterNewUserResult result = new RegisterNewUserResult();

        UserAccount existsUserAccount = userAccountRepository.putIfAbsent(newUserAccount);
        if (existsUserAccount != null) {
            result.setAccountExists(true);
            return result;
        }

        newUser.setId(userIdGeneratorRepository.take().generateId());
        userRepository.put(newUser);
        newUserAccount.setUser(newUser);
        result.setNewUser(newUser);
        return result;
    }

    public static UserSession logout(LoginByAccountServiceRepositorySet repositorySet,
                                     String token) {

        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        UserLoginStateRepository<UserLoginState, Object> userLoginStateRepository = repositorySet.getUserLoginStateRepository();

        UserSession removedUserSession = SharedBusinessMethodsBetweenServices.logout(userSessionRepository, token);

        SharedBusinessMethodsBetweenServices.updateUserLoginStateForLogout(userLoginStateRepository,
                removedUserSession.getUser().getId());
        return removedUserSession;
    }
}
