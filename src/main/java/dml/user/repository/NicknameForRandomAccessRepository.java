package dml.user.repository;

import dml.common.repository.CommonRepository;
import dml.user.entity.nickname.NicknameForRandomAccess;

public interface NicknameForRandomAccessRepository<E extends NicknameForRandomAccess> extends CommonRepository<E, Integer> {
}
