package dml.user.service.repositoryset;

import dml.user.repository.NicknameForRandomAccessIDGeneratorRepository;
import dml.user.repository.NicknameForRandomAccessRepository;

public interface UserNicknameRandomGenerateServiceRepositorySet {
    NicknameForRandomAccessRepository getNicknameForRandomAccessRepository();

    NicknameForRandomAccessIDGeneratorRepository getNicknameForRandomAccessIDGeneratorRepository();
}
