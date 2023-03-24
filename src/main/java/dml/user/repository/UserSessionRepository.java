package dml.user.repository;

import dml.user.entity.UserSession;

/**
 * @author zheng chengdong
 */
public interface UserSessionRepository<E extends UserSession> extends CommonEntityRepository<E, String> {
}
