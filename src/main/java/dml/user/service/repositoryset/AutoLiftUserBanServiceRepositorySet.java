package dml.user.service.repositoryset;

import dml.user.entity.AutoLiftUserBan;
import dml.user.repository.AutoLiftUserBanRepository;

public interface AutoLiftUserBanServiceRepositorySet {
    AutoLiftUserBanRepository<AutoLiftUserBan, Object> getAutoLiftUserBanRepository();
}
