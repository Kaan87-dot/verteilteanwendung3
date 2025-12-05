package de.berlin.htw.control;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import de.berlin.htw.boundary.dto.Order;
import de.berlin.htw.boundary.dto.Orders;
import de.berlin.htw.entity.dao.OrderRepository;
import de.berlin.htw.entity.dao.UserRepository;
import de.berlin.htw.entity.dto.OrderEntity;
import de.berlin.htw.entity.dto.UserEntity;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
@Dependent
public class OrderController {

    @Inject
    OrderRepository orderRepository;
    
    @Inject
    UserRepository userRepository;
    
    @Inject
    Logger logger;

    public Orders getOrders(Integer userId) {
        List<OrderEntity> orderEntities = orderRepository.findOrdersByUserId(userId);
        
        Orders orders = new Orders();
        List<Order> orderList = new ArrayList<>();
        
        for (OrderEntity orderEntity : orderEntities) {
            Order order = new Order();
            order.setTotal(orderEntity.getTotal());
            
            List<de.berlin.htw.boundary.dto.Item> items = new ArrayList<>();
            for (de.berlin.htw.entity.dto.OrderItemEntity itemEntity : orderEntity.getItems()) {
                de.berlin.htw.boundary.dto.Item item = new de.berlin.htw.boundary.dto.Item();
                item.setProductId(itemEntity.getProductId());
                item.setProductName(itemEntity.getProductName());
                item.setCount(itemEntity.getCount());
                item.setPrice(itemEntity.getPrice());
                items.add(item);
            }
            order.setItems(items);
            orderList.add(order);
        }
        
        orders.setOrders(orderList);
        
        UserEntity user = userRepository.findUserById(userId);
        orders.setBalance(user.getBalance());
        
        return orders;
    }
    
    @Transactional
    public OrderEntity createOrder(Integer userId, de.berlin.htw.boundary.dto.Basket basket) {
        UserEntity user = userRepository.findUserById(userId);
        
        // Create order entity
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUser(user);
        orderEntity.setTotal(basket.getTotal());
        
        // Add items
        for (de.berlin.htw.boundary.dto.Item item : basket.getItems()) {
            de.berlin.htw.entity.dto.OrderItemEntity itemEntity = new de.berlin.htw.entity.dto.OrderItemEntity();
            itemEntity.setProductId(item.getProductId());
            itemEntity.setProductName(item.getProductName());
            itemEntity.setCount(item.getCount());
            itemEntity.setPrice(item.getPrice());
            orderEntity.addItem(itemEntity);
        }
        
        // Deduct balance
        float newBalance = user.getBalance() - basket.getTotal();
        user.setBalance(newBalance);
        
        // Persist order
        orderRepository.persistOrder(orderEntity);
        
        logger.info("Order created for user " + userId + " with total " + basket.getTotal());
        
        return orderEntity;
    }

}
