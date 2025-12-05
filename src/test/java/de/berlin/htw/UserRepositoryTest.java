package de.berlin.htw;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

import org.junit.jupiter.api.Test;

import de.berlin.htw.entity.dao.UserRepository;
import de.berlin.htw.entity.dto.UserEntity;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class UserRepositoryTest {

    @PersistenceContext
    EntityManager entityManager;
    
    @Inject
    UserTransaction userTransaction;
    
    @Inject
    UserRepository repository;
    
    @Test
    void testAddUser() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
        UserEntity user = new UserEntity();
        user.setName("TestUser");
        user.setBalance(10f);
        repository.persistUser(user);
        
        userTransaction.begin();
        int deltedUser = entityManager
                .createQuery("DELETE FROM UserEntity u WHERE u.name = :userName")
                .setParameter("userName", user.getName())
                .executeUpdate();
        userTransaction.commit();
        assertEquals(1, deltedUser);
    }

}
