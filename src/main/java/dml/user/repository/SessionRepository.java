package dml.user.repository;

import dml.user.entity.Session;

/**
 * @author zheng chengdong
 */
public interface SessionRepository<E extends Session> extends CommonEntityRepository<E, String> {
}
