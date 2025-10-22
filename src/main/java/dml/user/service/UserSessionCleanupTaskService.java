package dml.user.service;

import dml.largescaletaskmanagement.repository.LargeScaleSingletonTaskRepository;
import dml.largescaletaskmanagement.repository.LargeScaleTaskSegmentRepository;
import dml.largescaletaskmanagement.service.LargeScaleSingletonTaskService;
import dml.largescaletaskmanagement.service.repositoryset.LargeScaleSingletonTaskServiceRepositorySet;
import dml.largescaletaskmanagement.service.result.TakeTaskSegmentToExecuteResult;
import dml.user.entity.ClearSessionTask;
import dml.user.entity.ClearSessionTaskSegment;
import dml.user.entity.UserSession;
import dml.user.repository.*;
import dml.user.service.repositoryset.UserSessionCleanupTaskServiceRepositorySet;
import dml.user.service.shared.SharedBusinessMethodsBetweenServices;

import java.util.ArrayList;
import java.util.List;

public class UserSessionCleanupTaskService {

    /**
     * @return 是否成功创建。如果任务已存在，那不会创建，返回false
     */
    public static boolean createUserSessionCleanupTask(UserSessionCleanupTaskServiceRepositorySet repositorySet,
                                                       long currentTime) {
        ClearSessionTaskRepository clearSessionTaskRepository = repositorySet.getClearSessionTaskRepository();

        ClearSessionTask task = clearSessionTaskRepository.get();
        if (task == null) {
            task = (ClearSessionTask) LargeScaleSingletonTaskService.createTask(getLargeScaleSingletonTaskServiceRepositorySet(repositorySet),
                    new ClearSessionTask(), currentTime);
            return task != null;
        }
        return false;
    }

    public static void addAllSessionIdToUserSessionCleanupTask(UserSessionCleanupTaskServiceRepositorySet repositorySet,
                                                               int sessionBatchSize, List<String> sessionIdList) {
        ClearSessionTaskSegmentIDGeneratorRepository clearSessionTaskSegmentIDGeneratorRepository = repositorySet.getClearSessionTaskSegmentIDGeneratorRepository();
        //分批次
        int size = sessionIdList.size();
        int batchCount = size / sessionBatchSize;
        if (size % sessionBatchSize != 0) {
            batchCount++;
        }
        for (int i = 0; i < batchCount; i++) {
            int start = i * sessionBatchSize;
            int end = Math.min((i + 1) * sessionBatchSize, size);
            List<String> subList = new ArrayList<>(sessionIdList.subList(start, end));
            ClearSessionTaskSegment segment = new ClearSessionTaskSegment();
            segment.setId(clearSessionTaskSegmentIDGeneratorRepository.take().generateId());
            segment.setSessionIdList(subList);
            LargeScaleSingletonTaskService.addTaskSegment(getLargeScaleSingletonTaskServiceRepositorySet(repositorySet),
                    segment);
        }
        LargeScaleSingletonTaskService.setTaskReadyToProcess(getLargeScaleSingletonTaskServiceRepositorySet(repositorySet));
    }

    public static String takeUserSessionCleanupTaskSegmentToExecute(UserSessionCleanupTaskServiceRepositorySet repositorySet,
                                                                    long currentTime,
                                                                    long maxSegmentExecutionTime,
                                                                    long maxTimeToTaskReady) {
        TakeTaskSegmentToExecuteResult takeSegmentResult = LargeScaleSingletonTaskService.takeTaskSegmentToExecute(
                getLargeScaleSingletonTaskServiceRepositorySet(repositorySet),
                currentTime, maxSegmentExecutionTime, maxTimeToTaskReady);
        if (takeSegmentResult.isTaskCompleted()) {
            LargeScaleSingletonTaskService.removeTask(getLargeScaleSingletonTaskServiceRepositorySet(repositorySet));
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
        LargeScaleSingletonTaskService.completeTaskSegment(getLargeScaleSingletonTaskServiceRepositorySet(repositorySet),
                segment.getId());
    }

    private static LargeScaleSingletonTaskServiceRepositorySet getLargeScaleSingletonTaskServiceRepositorySet(
            UserSessionCleanupTaskServiceRepositorySet userSessionCleanupTaskServiceRepositorySet) {
        return new LargeScaleSingletonTaskServiceRepositorySet() {
            @Override
            public LargeScaleSingletonTaskRepository getLargeScaleSingletonTaskRepository() {
                return userSessionCleanupTaskServiceRepositorySet.getClearSessionTaskRepository();
            }

            @Override
            public LargeScaleTaskSegmentRepository getLargeScaleTaskSegmentRepository() {
                return userSessionCleanupTaskServiceRepositorySet.getClearSessionTaskSegmentRepository();
            }

        };
    }

}
