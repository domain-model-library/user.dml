package dml.user.service.repositoryset;

import dml.user.entity.User;
import dml.user.entity.UserAccount;
import dml.user.repository.UserAccountRepository;
import dml.user.repository.UserIDGeneratorRepository;
import dml.user.repository.UserRepository;

public interface UserRegistrationServiceRepositorySet {
    UserIDGeneratorRepository getUserIdGeneratorRepository();

    UserRepository getUserRepository();

    UserAccountRepository getUserAccountRepository();
}
