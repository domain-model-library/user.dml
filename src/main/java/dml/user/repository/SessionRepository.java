package dml.user.repository;

import dml.user.entity.UserSession;

/**
 * @author zheng chengdong
 */
public interface SessionRepository<E extends UserSession> extends CommonEntityRepository<E, String> {
}
