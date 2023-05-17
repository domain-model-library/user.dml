package dml.user.repository;

import dml.common.repository.CommonRepository;
import dml.user.entity.User;

/**
 * @author zheng chengdong
 */
public interface UserRepository<E extends User, ID> extends CommonRepository<E, ID> {
}
