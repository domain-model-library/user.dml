package dml.user.repository;

import dml.user.entity.User;

/**
 * @author zheng chengdong
 */
public interface UserRepository<E extends User, ID> extends CommonEntityRepository<E, ID> {
}
