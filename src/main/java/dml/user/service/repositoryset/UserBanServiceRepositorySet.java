package dml.user.service.repositoryset;

import dml.user.entity.UserBan;
import dml.user.repository.UserBanRepository;

public interface UserBanServiceRepositorySet {
    UserBanRepository<UserBan, Object> getUserBanRepository();
}
