package com.dayle.shoppingcart.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import com.dayle.shoppingcart.domain.CartItem;
import com.dayle.shoppingcart.repository.CartRepository;

@Service
@Transactional(readOnly = true)
public class CartService {
    private final AtomicInteger idGeneration = new AtomicInteger(1000);
    
    @Autowired
    private CartRepository cartRepo;
    
    @Autowired
    private GaugeService gaugeService;
    
    @Autowired
    private CounterService counterService;
    
    @Autowired
    private ApplicationContext appContext;

    @Transactional
    public long loadCartItems(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.lines()
                         //.parallel()
                         .map(this::parseCartItem)
                         .map(this::saveCartItem)
                         .count();
        }
    }

    public List<CartItem> searchCartItems(String keyword, int page, int pageSize) {
        keyword = (keyword == null) ? "" : keyword.toLowerCase();

        StopWatch watch = new StopWatch();
        watch.start();
        List<CartItem> cartItems = cartRepo.searchCartItem(keyword, new PageRequest(page, pageSize));
        watch.stop();
        gaugeService.submit("query.by.keyword." + keyword, watch.getTotalTimeMillis());
        counterService.increment("search.by.keyword." + keyword);
        
        return cartItems;
    }

    public CartItem getCartItem(String id) {
        return cartRepo.findOne(id);
    }

    @Transactional
    public CartItem saveCartItem(@Valid CartItem item) {
        if (item == null) {
            return null;
        }
        
        if (item.getId() == null) {
        	item.setId(String.valueOf(idGeneration.incrementAndGet()));
        }

        return cartRepo.save(item);
    }

    @Transactional
    public void deleteCartItems(String... ids) {
        cartRepo.deleteCartItems(ids);
    }
    
    @Transactional
    public void deleteAllCartItems() {
        cartRepo.deleteAllInBatch();
    }
    
    /**
     * cartItem format: id|name|description|cost|optNotes
     */
    private CartItem parseCartItem(String cartItem) {
        String[] items = cartItem.split("\\|");
        if (items.length < 2) {
            throw new IllegalArgumentException("Invalid cartItem format: " + cartItem);
        }
        
        CartItem cItem = new CartItem();
        cItem.setId(items[0]);
        cItem.setName(items[1]);
        if (items.length > 2) {
        	cItem.setDescription(items[2]);
        }
        if (items.length > 3) {
        	cItem.setCost(items[3]);
        }
        if (items.length > 4) {
        	cItem.setOptNotes(items[4]);
        }
//        if (items.length > 5) {
//        	cItem.setMobile(items[5]);
//        }
//        if (items.length > 6) {
//        	cItem.setSkypeId(items[6]);
//        }
        
        return cItem;
    }
}
