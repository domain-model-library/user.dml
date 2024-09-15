package dml.user.service.repositoryset;

import dml.largescaletaskmanagement.repository.LargeScaleTaskSegmentIDGeneratorRepository;
import dml.user.repository.ClearSessionTaskRepository;
import dml.user.repository.ClearSessionTaskSegmentRepository;
import dml.user.repository.UserSessionAliveKeeperRepository;
import dml.user.repository.UserSessionRepository;

public interface UserSessionCleanupServiceRepositorySet {
    UserSessionRepository getUserSessionRepository();

    UserSessionAliveKeeperRepository getSessionAliveKeeperRepository();

    ClearSessionTaskRepository getClearSessionTaskRepository();

    ClearSessionTaskSegmentRepository getClearSessionTaskSegmentRepository();

    LargeScaleTaskSegmentIDGeneratorRepository getClearSessionTaskSegmentIDGeneratorRepository();
}
