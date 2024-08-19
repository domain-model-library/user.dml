package dml.user.service.repositoryset;

import dml.user.entity.UserSession;
import dml.user.repository.UserSessionRepository;

public interface AuthServiceRepositorySet {

    UserSessionRepository getUserSessionRepository();
}
