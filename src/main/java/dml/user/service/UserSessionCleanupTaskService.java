package dml.user.service;

import dml.largescaletaskmanagement.repository.LargeScaleTaskRepository;
import dml.largescaletaskmanagement.repository.LargeScaleTaskSegmentIDGeneratorRepository;
import dml.largescaletaskmanagement.repository.LargeScaleTaskSegmentRepository;
import dml.largescaletaskmanagement.service.LargeScaleTaskService;
import dml.largescaletaskmanagement.service.repositoryset.LargeScaleTaskServiceRepositorySet;
import dml.largescaletaskmanagement.service.result.TakeTaskSegmentToExecuteResult;
import dml.user.entity.ClearSessionTask;
import dml.user.entity.ClearSessionTaskSegment;
import dml.user.entity.UserSession;
import dml.user.repository.ClearSessionTaskRepository;
import dml.user.repository.ClearSessionTaskSegmentRepository;
import dml.user.repository.UserSessionAliveKeeperRepository;
import dml.user.repository.UserSessionRepository;
import dml.user.service.repositoryset.UserSessionCleanupTaskServiceRepositorySet;
import dml.user.service.shared.SharedBusinessMethodsBetweenServices;

import java.util.List;

public class UserSessionCleanupTaskService {

    /**
     * @return 是否成功创建。如果任务已存在，那不会创建，返回false
     */
    public static boolean createUserSessionCleanupTask(UserSessionCleanupTaskServiceRepositorySet repositorySet,
                                                       String taskName, long currentTime) {
        ClearSessionTaskRepository clearSessionTaskRepository = repositorySet.getClearSessionTaskRepository();

        ClearSessionTask task = clearSessionTaskRepository.find(taskName);
        if (task == null) {
            task = (ClearSessionTask) LargeScaleTaskService.createTask(getLargeScaleTaskServiceRepositorySet(repositorySet),
                    taskName, new ClearSessionTask(), currentTime);
            return task != null;
        }
        return false;
    }

    public static void addAllSessionIdToUserSessionCleanupTask(UserSessionCleanupTaskServiceRepositorySet repositorySet,
                                                               String taskName, int sessionBatchSize, List<String> sessionIdList) {
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
            ClearSessionTaskSegment segment = new ClearSessionTaskSegment();
            segment.setSessionIdList(subList);
            LargeScaleTaskService.addTaskSegment(getLargeScaleTaskServiceRepositorySet(repositorySet),
                    taskName, segment);
        }
        LargeScaleTaskService.setTaskReadyToProcess(getLargeScaleTaskServiceRepositorySet(repositorySet),
                taskName);
    }

    public static String takeUserSessionCleanupTaskSegmentToExecute(UserSessionCleanupTaskServiceRepositorySet repositorySet,
                                                                    String taskName, long currentTime,
                                                                    long maxSegmentExecutionTime,
                                                                    long maxTimeToTaskReady) {
        TakeTaskSegmentToExecuteResult takeSegmentResult = LargeScaleTaskService.takeTaskSegmentToExecute(
                getLargeScaleTaskServiceRepositorySet(repositorySet),
                taskName, currentTime, maxSegmentExecutionTime, maxTimeToTaskReady);
        if (takeSegmentResult.isTaskCompleted()) {
            LargeScaleTaskService.removeTask(getLargeScaleTaskServiceRepositorySet(repositorySet),
                    taskName);
            return null;
        }
        if (takeSegmentResult.getTaskSegment() == null) {
            return null;
        }
        return (String) takeSegmentResult.getTaskSegment().getId();
    }

    public static void executeUserSessionCleanupTaskSegment(UserSessionCleanupTaskServiceRepositorySet repositorySet,
                                                            String segmentId,
                                                            long currentTime,
                                                            long sessionKeepAliveInterval) {
        ClearSessionTaskSegmentRepository clearSessionTaskSegmentRepository = repositorySet.getClearSessionTaskSegmentRepository();
        UserSessionRepository<UserSession> userSessionRepository = repositorySet.getUserSessionRepository();
        UserSessionAliveKeeperRepository sessionAliveKeeperRepository = repositorySet.getUserSessionAliveKeeperRepository();


        ClearSessionTaskSegment segment = clearSessionTaskSegmentRepository.find(segmentId);
        if (segment == null) {
            return;
        }
        List<String> segmentSessionIdList = segment.getSessionIdList();
        for (String sessionId : segmentSessionIdList) {
            SharedBusinessMethodsBetweenServices.checkSessionDeadAndRemove(userSessionRepository, sessionAliveKeeperRepository,
                    sessionId, currentTime, sessionKeepAliveInterval);
        }
        LargeScaleTaskService.completeTaskSegment(getLargeScaleTaskServiceRepositorySet(repositorySet),
                segment.getId());
    }

    private static LargeScaleTaskServiceRepositorySet getLargeScaleTaskServiceRepositorySet(
            UserSessionCleanupTaskServiceRepositorySet userSessionCleanupTaskServiceRepositorySet) {
        return new LargeScaleTaskServiceRepositorySet() {
            @Override
            public LargeScaleTaskRepository getLargeScaleTaskRepository() {
                return userSessionCleanupTaskServiceRepositorySet.getClearSessionTaskRepository();
            }

            @Override
            public LargeScaleTaskSegmentRepository getLargeScaleTaskSegmentRepository() {
                return userSessionCleanupTaskServiceRepositorySet.getClearSessionTaskSegmentRepository();
            }

            @Override
            public LargeScaleTaskSegmentIDGeneratorRepository getLargeScaleTaskSegmentIDGeneratorRepository() {
                return userSessionCleanupTaskServiceRepositorySet.getClearSessionTaskSegmentIDGeneratorRepository();
            }
        };
    }

}
