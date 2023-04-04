package dml.user.service.repositoryset;

import dml.user.entity.AutoLiftTime;
import dml.user.repository.AutoLiftTimeRepository;

/**
 * @author zheng chengdong
 */
public interface UserBanAutoLiftServiceRepositorySet {
    AutoLiftTimeRepository<AutoLiftTime, Object> getAutoLiftTimeRepository();
}
