package de.berlin.htw.entity.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import de.berlin.htw.entity.dto.OrderEntity;

import java.util.List;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
@ApplicationScoped
public class OrderRepository {

    @PersistenceContext
    EntityManager entityManager;
    
    public OrderEntity findOrderById(final Integer id) {
        return entityManager.find(OrderEntity.class, id);
    }
    
    public List<OrderEntity> findOrdersByUserId(final Integer userId) {
        TypedQuery<OrderEntity> query = entityManager.createQuery(
            "SELECT o FROM OrderEntity o WHERE o.user.id = :userId ORDER BY o.createdAt DESC", 
            OrderEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    @Transactional
    public void persistOrder(final OrderEntity order) {
        entityManager.persist(order);
    }
    
}
