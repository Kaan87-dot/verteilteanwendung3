package de.berlin.htw.control;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import de.berlin.htw.boundary.dto.Basket;
import de.berlin.htw.boundary.dto.Item;
import de.berlin.htw.entity.dao.UserRepository;
import de.berlin.htw.entity.dto.UserEntity;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.hash.HashCommands;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
@Dependent
public class BasketController {

    @Inject
    protected RedisDataSource redisDS;
    
    @Inject
    protected UserRepository userRepository;
    
    @Inject
    Logger logger;
    
    protected HashCommands<String, String, String> hashCommands;
    
    private static final Duration TTL = Duration.ofMinutes(2);
    
    @PostConstruct
    protected void init() {
        hashCommands = redisDS.hash(String.class, String.class, String.class);
    }
    
    private String getUserBasketKey(Integer userId) {
        return "basket:user:" + userId;
    }
    
    private String getItemKey(String productId) {
        return "item:" + productId;
    }
    
    private void setBasketExpiration(Integer userId) {
        String key = getUserBasketKey(userId);
        redisDS.key().expire(key, TTL);
    }
    
    public Basket getBasket(Integer userId) {
        String key = getUserBasketKey(userId);
        Map<String, String> basketData = hashCommands.hgetall(key);
        
        Basket basket = new Basket();
        List<Item> items = new ArrayList<>();
        float total = 0.0f;
        
        for (Map.Entry<String, String> entry : basketData.entrySet()) {
            if (entry.getKey().startsWith("item:")) {
                String[] parts = entry.getValue().split("\\|");
                if (parts.length == 4) {
                    Item item = new Item();
                    item.setProductId(parts[0]);
                    item.setProductName(parts[1]);
                    item.setCount(Integer.parseInt(parts[2]));
                    item.setPrice(Float.parseFloat(parts[3]));
                    items.add(item);
                    total += item.getCount() * item.getPrice();
                }
            }
        }
        
        basket.setItems(items);
        basket.setTotal(total);
        
        UserEntity user = userRepository.findUserById(userId);
        basket.setRemainingBalance(user.getBalance() - total);
        
        return basket;
    }
    
    public void clearBasket(Integer userId) {
        String key = getUserBasketKey(userId);
        redisDS.key().del(key);
    }
    
    public Basket addItem(Integer userId, String productId, Item item) {
        String key = getUserBasketKey(userId);
        String itemKey = getItemKey(productId);
        
        // Check if item already exists - if so, increment the count
        if (hashCommands.hexists(key, itemKey)) {
            // Get current item
            String currentValue = hashCommands.hget(key, itemKey);
            String[] parts = currentValue.split("\\|");
            
            // Calculate new count (current count + new count)
            int currentCount = Integer.parseInt(parts[2]);
            int newCount = currentCount + item.getCount();
            
            // Calculate new total
            Basket currentBasket = getBasket(userId);
            float oldItemTotal = currentCount * Float.parseFloat(parts[3]);
            float newItemTotal = newCount * Float.parseFloat(parts[3]);
            float newTotal = currentBasket.getTotal() - oldItemTotal + newItemTotal;
            
            // Check if user has sufficient balance
            UserEntity user = userRepository.findUserById(userId);
            if (user.getBalance() < newTotal) {
                throw new BadRequestException("Insufficient balance to add more of this item");
            }
            
            // Update item with new count
            String itemValue = String.format(java.util.Locale.US, "%s|%s|%d|%s", 
                parts[0], // productId
                parts[1], // productName
                newCount, // updated count
                parts[3]); // price
            hashCommands.hset(key, itemKey, itemValue);
            
            setBasketExpiration(userId);
            return getBasket(userId);
        }
        
        // Item doesn't exist - add as new item
        // Check if basket would exceed 10 items
        long itemCount = hashCommands.hlen(key);
        if (itemCount >= 10) {
            throw new BadRequestException("Basket cannot contain more than 10 items");
        }
        
        // Calculate new total
        Basket currentBasket = getBasket(userId);
        float newTotal = currentBasket.getTotal() + (item.getCount() * item.getPrice());
        
        // Check if user has sufficient balance
        UserEntity user = userRepository.findUserById(userId);
        if (user.getBalance() < newTotal) {
            throw new BadRequestException("Insufficient balance to add this item");
        }
        
        // Store item
        String itemValue = String.format(java.util.Locale.US, "%s|%s|%d|%.2f", 
            item.getProductId(), 
            item.getProductName(), 
            item.getCount(), 
            item.getPrice());
        hashCommands.hset(key, itemKey, itemValue);
        
        // Set expiration
        setBasketExpiration(userId);
        
        return getBasket(userId);
    }
    
    public Basket removeItem(Integer userId, String productId) {
        String key = getUserBasketKey(userId);
        String itemKey = getItemKey(productId);
        
        if (!hashCommands.hexists(key, itemKey)) {
            throw new NotFoundException("Product with ID " + productId + " not found in basket");
        }
        
        hashCommands.hdel(key, itemKey);
        setBasketExpiration(userId);
        
        return getBasket(userId);
    }
    
    public Basket changeCount(Integer userId, String productId, Item item) {
        String key = getUserBasketKey(userId);
        String itemKey = getItemKey(productId);
        
        if (!hashCommands.hexists(key, itemKey)) {
            throw new NotFoundException("Product with ID " + productId + " not found in basket");
        }
        
        // Get current item to preserve other fields
        String currentValue = hashCommands.hget(key, itemKey);
        String[] parts = currentValue.split("\\|");
        
        // Calculate new total
        Basket currentBasket = getBasket(userId);
        
        // Remove old item total and add new item total
        float oldItemTotal = Integer.parseInt(parts[2]) * Float.parseFloat(parts[3]);
        float newItemTotal = item.getCount() * Float.parseFloat(parts[3]);
        float newTotal = currentBasket.getTotal() - oldItemTotal + newItemTotal;
        
        // Check if user has sufficient balance
        UserEntity user = userRepository.findUserById(userId);
        if (user.getBalance() < newTotal) {
            throw new BadRequestException("Insufficient balance to change item count");
        }
        
        // Update item with new count
        String itemValue = String.format("%s|%s|%d|%s", 
            parts[0], // productId
            parts[1], // productName
            item.getCount(), // new count
            parts[3]); // price
        hashCommands.hset(key, itemKey, itemValue);
        
        setBasketExpiration(userId);
        
        return getBasket(userId);
    }
}
