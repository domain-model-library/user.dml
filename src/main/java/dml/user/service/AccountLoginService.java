package dml.user.service;

import dml.user.entity.User;
import dml.user.entity.UserAccount;
import dml.user.entity.UserLoginState;
import dml.user.entity.UserSession;
import dml.user.repository.*;
import dml.user.service.repositoryset.AccountLoginServiceSet;
import dml.user.service.repositoryset.AccountLogoutAndUpdateStateForNewLoginKickRepositorySet;
import dml.user.service.repositoryset.AccountPasswordKickLoginRepositorySet;
import dml.user.service.result.AccountPasswordKickLoginResult;
import dml.user.service.result.AccountPasswordLoginResult;
import dml.user.service.result.RegisterNewUserResult;

/**
 * @author zheng chengdong
 */
public class AccountLoginService {
    public static AccountPasswordLoginResult accountPasswordLogin(AccountLoginServiceSet repositorySet,
                                                                  String account,
                                                                  String password,
                                                                  UserSession newUserSession) {

        UserAccountRepository<UserAccount> userAccountRepository = repositorySet.getUserAccountRepository();
        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        UserSessionIdGeneratorRepository userSessionIdGeneratorRepository = repositorySet.getUserSessionIdGeneratorRepository();

        AccountPasswordLoginResult result = new AccountPasswordLoginResult();

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

        result.setLoginSuccess(true);

        return result;
    }

    public static RegisterNewUserResult registerNewUser(AccountLoginServiceSet repositorySet,
                                                        UserAccount newUserAccount,
                                                        User newUser) {

        UserAccountRepository<UserAccount> userAccountRepository = repositorySet.getUserAccountRepository();
        UserIdGeneratorRepository userIdGeneratorRepository = repositorySet.getUserIdGeneratorRepository();
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

    public static UserSession logout(AccountLoginServiceSet repositorySet,
                                     String token) {

        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();

        return SharedBusinessMethodsBetweenServices.logout(userSessionRepository, token);
    }

    public static AccountPasswordKickLoginResult accountPasswordKickLogin(AccountPasswordKickLoginRepositorySet repositorySet,
                                                                          String account,
                                                                          String password,
                                                                          UserSession newUserSession,
                                                                          UserLoginState newUserLoginState) {

        AccountPasswordKickLoginResult result = new AccountPasswordKickLoginResult();
        AccountPasswordLoginResult accountPasswordLoginResult = accountPasswordLogin(repositorySet,
                account,
                password,
                newUserSession);
        result.setLoginResult(accountPasswordLoginResult);
        if (!accountPasswordLoginResult.isLoginSuccess()) {
            return result;
        }

        String removedUserSessionId = KickLoginService.newLoginKickOldLogin(repositorySet,
                accountPasswordLoginResult.getNewUserSession().getId(),
                newUserLoginState);
        result.setRemovedUserSessionId(removedUserSessionId);
        return result;
    }

    public static UserSession logoutAndUpdateStateForNewLoginKick(AccountLogoutAndUpdateStateForNewLoginKickRepositorySet repositorySet,
                                                                  String token) {

        UserSession removedUserSession = logout(repositorySet, token);
        if (removedUserSession == null) {
            return null;
        }

        KickLoginService.setLoggedOut(repositorySet,
                removedUserSession.getUser().getId());
        return removedUserSession;

    }

}
