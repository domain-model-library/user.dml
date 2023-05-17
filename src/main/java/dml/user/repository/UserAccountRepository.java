package dml.user.repository;

import dml.common.repository.CommonRepository;
import dml.user.entity.UserAccount;

/**
 * @author zheng chengdong
 */
public interface UserAccountRepository<E extends UserAccount> extends CommonRepository<E, String> {
}
