package dml.user.service;

import dml.user.entity.UserAccount;
import dml.user.entity.UserSession;
import dml.user.repository.UserAccountRepository;
import dml.user.repository.UserSessionIdGeneratorRepository;
import dml.user.repository.UserSessionRepository;
import dml.user.service.repositoryset.AccountLoginServiceSet;
import dml.user.service.result.AccountPasswordLoginResult;

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
}
