package dml.user.service;

import dml.user.entity.UserSession;
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
        
    }
}
