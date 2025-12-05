package de.berlin.htw.entity.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import de.berlin.htw.entity.dto.UserEntity;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
@ApplicationScoped
public class UserRepository {

    @PersistenceContext
    EntityManager entityManager;
    
    public UserEntity findUserById(final Integer id) {
        return entityManager.find(UserEntity.class, id);
    }
    
    @Transactional
    public void persistUser(final UserEntity user) {
        entityManager.persist(user);
    }
    
}