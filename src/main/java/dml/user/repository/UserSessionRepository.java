package dml.user.repository;

import dml.common.repository.CommonRepository;
import dml.user.entity.UserSession;

/**
 * @author zheng chengdong
 */
public interface UserSessionRepository<E extends UserSession> extends CommonRepository<E, String> {
}
