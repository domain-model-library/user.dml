package dml.user.service.repositoryset;

import dml.keepalive.repository.AliveKeeperRepository;
import dml.largescaletaskmanagement.repository.LargeScaleTaskSegmentIDGeneratorRepository;
import dml.user.repository.ClearSessionTaskRepository;
import dml.user.repository.ClearSessionTaskSegmentRepository;
import dml.user.repository.UserSessionRepository;

public interface UserSessionCleanupServiceRepositorySet {
    UserSessionRepository getUserSessionRepository();

    AliveKeeperRepository getSessionAliveKeeperRepository();

    ClearSessionTaskRepository getClearSessionTaskRepository();

    ClearSessionTaskSegmentRepository getClearSessionTaskSegmentRepository();

    LargeScaleTaskSegmentIDGeneratorRepository getClearSessionTaskSegmentIDGeneratorRepository();
}
