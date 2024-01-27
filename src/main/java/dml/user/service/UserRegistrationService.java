package dml.user.service;

import dml.user.entity.User;
import dml.user.entity.UserAccount;
import dml.user.repository.UserAccountRepository;
import dml.user.repository.UserIDGeneratorRepository;
import dml.user.repository.UserRepository;
import dml.user.service.repositoryset.UserRegistrationServiceRepositorySet;
import dml.user.service.result.RegisterNewUserResult;

public class UserRegistrationService {

    public static RegisterNewUserResult registerNewUser(UserRegistrationServiceRepositorySet repositorySet,
                                                        String account,
                                                        String password,
                                                        UserAccount newUserAccount,
                                                        User newUser) {

        UserAccountRepository<UserAccount> userAccountRepository = repositorySet.getUserAccountRepository();
        UserIDGeneratorRepository userIdGeneratorRepository = repositorySet.getUserIdGeneratorRepository();
        UserRepository<User, Object> userRepository = repositorySet.getUserRepository();

        RegisterNewUserResult result = new RegisterNewUserResult();

        newUserAccount.setAccount(account);
        newUserAccount.setPassword(password);
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

}
