package dml.user.service.repositoryset;

import dml.largescaletaskmanagement.repository.LargeScaleTaskSegmentIDGeneratorRepository;
import dml.user.repository.ClearSessionTaskRepository;
import dml.user.repository.ClearSessionTaskSegmentRepository;
import dml.user.repository.UserSessionAliveKeeperRepository;
import dml.user.repository.UserSessionRepository;

public interface UserSessionCleanupTaskServiceRepositorySet {
    ClearSessionTaskRepository getClearSessionTaskRepository();

    ClearSessionTaskSegmentRepository getClearSessionTaskSegmentRepository();

    LargeScaleTaskSegmentIDGeneratorRepository getClearSessionTaskSegmentIDGeneratorRepository();

    UserSessionRepository getUserSessionRepository();

    UserSessionAliveKeeperRepository getUserSessionAliveKeeperRepository();
}
