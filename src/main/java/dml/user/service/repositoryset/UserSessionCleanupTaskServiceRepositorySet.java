package dml.user.service.repositoryset;

import dml.user.repository.*;

public interface UserSessionCleanupTaskServiceRepositorySet {
    ClearSessionTaskRepository getClearSessionTaskRepository();

    ClearSessionTaskSegmentRepository getClearSessionTaskSegmentRepository();

    ClearSessionTaskSegmentIDGeneratorRepository getClearSessionTaskSegmentIDGeneratorRepository();

    UserSessionRepository getUserSessionRepository();

    UserSessionAliveKeeperRepository getUserSessionAliveKeeperRepository();

    ClearSessionTaskSegmentTimeoutStrategyRepository getClearSessionTaskSegmentTimeoutStrategyRepository();
}
