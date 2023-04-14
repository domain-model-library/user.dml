package dml.user.repository;

import dml.user.entity.UserAccount;

/**
 * @author zheng chengdong
 */
public interface UserAccountRepository<E extends UserAccount> extends CommonEntityRepository<E, String> {
}
