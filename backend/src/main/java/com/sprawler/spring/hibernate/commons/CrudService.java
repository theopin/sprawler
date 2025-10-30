package com.sprawler.spring.hibernate.commons;

import java.util.List;
import java.util.Optional;

public interface CrudService<T> {

    List<T> getAllEntities();

    Optional<T> getEntityById(Long id);

    T createEntity(T entity);

    T updateEntity(Long id, T updatedEntity);

    void deleteEntity(Long id);
}
