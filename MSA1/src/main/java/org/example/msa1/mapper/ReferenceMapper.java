package org.example.msa1.mapper;

import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class ReferenceMapper {

    @PersistenceContext
    private EntityManager entityManager;

    public <T> T toReference(Class<T> entityClass, Long id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(entityClass, id);
    }
}