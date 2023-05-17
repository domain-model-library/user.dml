package dml.user.repository;

import dml.common.repository.CommonSingletonRepository;
import dml.id.entity.IdGenerator;

/**
 * @author zheng chengdong
 */
public interface UserIdGeneratorRepository extends CommonSingletonRepository<IdGenerator<Object>> {
}
