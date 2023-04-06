package dml.user.service.repositoryset;

import dml.user.entity.AutoLiftTime;
import dml.user.entity.UserBan;
import dml.user.repository.AutoLiftTimeRepository;
import dml.user.repository.UserBanRepository;

/**
 * @author zheng chengdong
 */
public interface UserBanAutoLiftServiceRepositorySet {
    AutoLiftTimeRepository<AutoLiftTime, Object> getAutoLiftTimeRepository();

    UserBanRepository<UserBan, Object> getUserBanRepository();
}
