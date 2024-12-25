package dml.user.service;

import dml.id.entity.reusableid.ReusableIntIdGenerator;
import dml.user.entity.nickname.NicknameForRandomAccess;
import dml.user.repository.NicknameForRandomAccessIDGeneratorRepository;
import dml.user.repository.NicknameForRandomAccessRepository;
import dml.user.service.repositoryset.UserNicknameRandomGenerateServiceRepositorySet;

import java.util.Random;

public class UserNicknameRandomGenerateService {
    public static NicknameForRandomAccess addCandidateNickname(UserNicknameRandomGenerateServiceRepositorySet repositorySet,
                                                               NicknameForRandomAccess newNickname) {
        NicknameForRandomAccessIDGeneratorRepository nicknameForRandomAccessIDGeneratorRepository =
                repositorySet.getNicknameForRandomAccessIDGeneratorRepository();
        NicknameForRandomAccessRepository<NicknameForRandomAccess> nicknameForRandomAccessRepository =
                repositorySet.getNicknameForRandomAccessRepository();

        int nicknameId = nicknameForRandomAccessIDGeneratorRepository.take().generateId();
        newNickname.setId(nicknameId);
        nicknameForRandomAccessRepository.put(newNickname);
        return newNickname;
    }

    public static NicknameForRandomAccess removeCandidateNickname(UserNicknameRandomGenerateServiceRepositorySet repositorySet,
                                                                  int nicknameId) {
        NicknameForRandomAccessRepository<NicknameForRandomAccess> nicknameForRandomAccessRepository =
                repositorySet.getNicknameForRandomAccessRepository();
        NicknameForRandomAccessIDGeneratorRepository nicknameForRandomAccessIDGeneratorRepository =
                repositorySet.getNicknameForRandomAccessIDGeneratorRepository();

        NicknameForRandomAccess removedNickname = nicknameForRandomAccessRepository.remove(nicknameId);
        ReusableIntIdGenerator nicknameIdGenerator = nicknameForRandomAccessIDGeneratorRepository.take();
        nicknameIdGenerator.recycleId(nicknameId);

        //优化缝隙
        if (nicknameIdGenerator.countIdRanges() > 1) {
            int toRemoveIdIdx = nicknameIdGenerator.countIds() - 1;
            int toRemoveId = nicknameIdGenerator.queryId(toRemoveIdIdx);
            NicknameForRandomAccess toMoveNickname = nicknameForRandomAccessRepository.remove(toRemoveId);
            nicknameIdGenerator.recycleId(toRemoveId);
            toMoveNickname.setId(nicknameIdGenerator.generateId());
            nicknameForRandomAccessRepository.put(toMoveNickname);
        }

        return removedNickname;
    }

    public static NicknameForRandomAccess randomPickNickname(UserNicknameRandomGenerateServiceRepositorySet repositorySet,
                                                             Random random) {
        NicknameForRandomAccessIDGeneratorRepository nicknameForRandomAccessIDGeneratorRepository =
                repositorySet.getNicknameForRandomAccessIDGeneratorRepository();
        NicknameForRandomAccessRepository<NicknameForRandomAccess> nicknameForRandomAccessRepository =
                repositorySet.getNicknameForRandomAccessRepository();

        ReusableIntIdGenerator nicknameIdGenerator = nicknameForRandomAccessIDGeneratorRepository.get();
        int idIndex = random.nextInt(nicknameIdGenerator.countIds());
        int id = nicknameIdGenerator.queryId(idIndex);
        if (id == -1) {
            return null;
        }
        return nicknameForRandomAccessRepository.find(id);
    }
}
