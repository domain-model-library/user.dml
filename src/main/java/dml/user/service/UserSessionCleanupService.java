package dml.user.service;

import dml.keepalive.repository.AliveKeeperRepository;
import dml.keepalive.service.KeepAliveService;
import dml.keepalive.service.repositoryset.AliveKeeperServiceRepositorySet;
import dml.largescaletaskmanagement.repository.LargeScaleTaskRepository;
import dml.largescaletaskmanagement.repository.LargeScaleTaskSegmentIDGeneratorRepository;
import dml.largescaletaskmanagement.repository.LargeScaleTaskSegmentRepository;
import dml.largescaletaskmanagement.service.LargeScaleTaskService;
import dml.largescaletaskmanagement.service.repositoryset.LargeScaleTaskServiceRepositorySet;
import dml.largescaletaskmanagement.service.result.TakeTaskSegmentToExecuteResult;
import dml.user.entity.ClearSessionTakeSegment;
import dml.user.entity.ClearSessionTask;
import dml.user.entity.UserSession;
import dml.user.repository.ClearSessionTaskRepository;
import dml.user.repository.UserSessionRepository;
import dml.user.service.repositoryset.UserSessionCleanupServiceRepositorySet;

import java.util.List;

public class UserSessionCleanupService {

    public static UserSession checkSessionDeadAndRemove(UserSessionCleanupServiceRepositorySet repositorySet,
                                                        String sessionId,
                                                        long currentTime,
                                                        long sessionKeepAliveInterval) {
        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();

        boolean alive = KeepAliveService.isAlive(getAliveKeeperServiceRepositorySet(repositorySet),
                sessionId, currentTime, sessionKeepAliveInterval);
        if (!alive) {
            UserSession removedSession = userSessionRepository.remove(sessionId);
            KeepAliveService.removeAliveKeeper(getAliveKeeperServiceRepositorySet(repositorySet)
                    , sessionId);
            return removedSession;
        }
        return null;

    }

    /**
     * 如果任务没有完成还需要继续执行，返回true
     */
    public static boolean executeUserSessionCleanupTask(UserSessionCleanupServiceRepositorySet repositorySet,
                                                        String taskName, long currentTime, int sessionBatchSize,
                                                        long maxSegmentExecutionTime, long maxTimeToTaskReady,
                                                        long sessionKeepAliveInterval, List<String> sessionIdList) {
        ClearSessionTaskRepository clearSessionTaskRepository = repositorySet.getClearSessionTaskRepository();

        ClearSessionTask task = clearSessionTaskRepository.find(taskName);
        if (task == null) {
            task = (ClearSessionTask) LargeScaleTaskService.createTask(getLargeScaleTaskServiceRepositorySet(repositorySet),
                    taskName, new ClearSessionTask(), currentTime);
            if (task != null) {
                if (sessionIdList.isEmpty()) {
                    return false;
                }
                //分批次
                int size = sessionIdList.size();
                int batchCount = size / sessionBatchSize;
                if (size % sessionBatchSize != 0) {
                    batchCount++;
                }
                for (int i = 0; i < batchCount; i++) {
                    int start = i * sessionBatchSize;
                    int end = Math.min((i + 1) * sessionBatchSize, size);
                    List<String> subList = sessionIdList.subList(start, end);
                    ClearSessionTakeSegment segment = new ClearSessionTakeSegment();
                    segment.setSessionIdList(subList);
                    LargeScaleTaskService.addTaskSegment(getLargeScaleTaskServiceRepositorySet(repositorySet),
                            taskName, segment);
                }
                LargeScaleTaskService.setTaskReadyToProcess(getLargeScaleTaskServiceRepositorySet(repositorySet),
                        taskName);
            }
            return true;
        }

        TakeTaskSegmentToExecuteResult takeSegmentResult = LargeScaleTaskService.takeTaskSegmentToExecute(
                getLargeScaleTaskServiceRepositorySet(repositorySet),
                taskName, currentTime, maxSegmentExecutionTime, maxTimeToTaskReady);
        if (takeSegmentResult.isTaskCompleted()) {
            LargeScaleTaskService.removeTask(getLargeScaleTaskServiceRepositorySet(repositorySet),
                    taskName);
            return false;
        }
        ClearSessionTakeSegment segment = (ClearSessionTakeSegment) takeSegmentResult.getTaskSegment();
        if (segment == null) {
            return false;
        }
        List<String> segmentSessionIdList = segment.getSessionIdList();
        for (String sessionId : segmentSessionIdList) {
            UserSessionCleanupService.checkSessionDeadAndRemove(repositorySet,
                    sessionId, currentTime, sessionKeepAliveInterval);
        }
        LargeScaleTaskService.completeTaskSegment(getLargeScaleTaskServiceRepositorySet(repositorySet),
                segment.getId());
        return true;
    }

    private static LargeScaleTaskServiceRepositorySet getLargeScaleTaskServiceRepositorySet(
            UserSessionCleanupServiceRepositorySet userSessionCleanupServiceRepositorySet) {
        return new LargeScaleTaskServiceRepositorySet() {
            @Override
            public LargeScaleTaskRepository getLargeScaleTaskRepository() {
                return userSessionCleanupServiceRepositorySet.getClearSessionTaskRepository();
            }

            @Override
            public LargeScaleTaskSegmentRepository getLargeScaleTaskSegmentRepository() {
                return userSessionCleanupServiceRepositorySet.getClearSessionTaskSegmentRepository();
            }

            @Override
            public LargeScaleTaskSegmentIDGeneratorRepository getLargeScaleTaskSegmentIDGeneratorRepository() {
                return userSessionCleanupServiceRepositorySet.getClearSessionTaskSegmentIDGeneratorRepository();
            }
        };
    }

    private static AliveKeeperServiceRepositorySet getAliveKeeperServiceRepositorySet(
            UserSessionCleanupServiceRepositorySet userSessionCleanupServiceRepositorySet) {
        return new AliveKeeperServiceRepositorySet() {
            @Override
            public AliveKeeperRepository getAliveKeeperRepository() {
                return userSessionCleanupServiceRepositorySet.getUserSessionAliveKeeperRepository();
            }
        };
    }

}
