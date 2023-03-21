package dml.user.repository;

/**
 * @author zheng chengdong
 */
interface CommonEntityRepository<E, ID> {
    E find(ID id);

    E take(ID id);

    void put(E entity);

    E putIfAbsent(E entity);

    E takeOrPutIfAbsent(ID id, E newEntity);

    E remove(ID id);
}
