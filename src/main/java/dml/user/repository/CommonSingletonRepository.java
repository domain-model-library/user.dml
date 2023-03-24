package dml.user.repository;

interface CommonSingletonRepository<E> {
    E get();

    E take();

    void put(E entity);
}
