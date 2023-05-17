package dml.user.repository;

import dml.common.repository.CommonRepository;
import dml.user.entity.OpenIdUserBind;

/**
 * @author zheng chengdong
 */
public interface OpenIdUserBindRepository<E extends OpenIdUserBind> extends CommonRepository<E, String> {
}
