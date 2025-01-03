package dml.user.service.repositoryset;

import dml.user.repository.UserAccountRepository;
import dml.user.repository.UserRepository;

public interface UserRegistrationServiceRepositorySet {

    UserRepository getUserRepository();

    UserAccountRepository getUserAccountRepository();
}
