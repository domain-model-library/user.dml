package dml.user.service.repositoryset;

import dml.user.entity.UserAutoLiftBan;
import dml.user.repository.UserAutoLiftBanRepository;

/**
 * @author zheng chengdong
 */
public interface UserAutoLiftBanServiceRepositorySet {
    UserAutoLiftBanRepository<UserAutoLiftBan, Object> getUserAutoLiftBanRepository();
}
