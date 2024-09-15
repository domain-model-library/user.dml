package dml.user.repository;

import dml.keepalive.repository.AliveKeeperRepository;
import dml.user.entity.UserSessionAliveKeeper;

public interface UserSessionAliveKeeperRepository extends AliveKeeperRepository<UserSessionAliveKeeper, String> {
}
