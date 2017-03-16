package com.dayle.shoppingcart.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dayle.shoppingcart.Application;
import com.dayle.shoppingcart.domain.CartItem;
import com.dayle.shoppingcart.service.CartService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class CartServiceTest {
    @Autowired
    private CartService cartService;
    
    @Before
    public void startUp() throws IOException {
        cartService.loadCartItems(getClass().getResource("/cartItems-test.txt").getFile());
    }
    
    @After
    public void tearDown() {
        cartService.deleteAllCartItems();
    }
    
    @Test
    public void testSearchCartItems() {
        List<CartItem> cartItems;
        
        // search all
        cartItems = cartService.searchCartItems("", 0, 10);
        assertThat(cartItems.size(), is(equalTo(5)));
        
        // search by id
        cartItems = cartService.searchCartItems("00", 0, 10);
        assertThat(cartItems.size(), is(equalTo(5)));
        
        // search by name
        cartItems = cartService.searchCartItems("name1", 0, 10);
        assertThat(cartItems.size(), is(equalTo(1)));
        assertThat(cartItems.get(0).getId(), is(equalTo("001")));
        
        // search by description
        cartItems = cartService.searchCartItems("description2", 0, 10);
        assertThat(cartItems.size(), is(equalTo(1)));
        assertThat(cartItems.get(0).getId(), is(equalTo("002")));
        
        // search by cost
        cartItems = cartService.searchCartItems("cost3", 0, 10);
        assertThat(cartItems.size(), is(equalTo(1)));
        assertThat(cartItems.get(0).getId(), is(equalTo("003")));
        
        // search by optional notes
        cartItems = cartService.searchCartItems("optNote4", 0, 10);
        assertThat(cartItems.size(), is(equalTo(1)));
        assertThat(cartItems.get(0).getId(), is(equalTo("004")));
    }
    
    @Test
    public void testSaveCartItem() {
        // create
        CartItem cartItem = new CartItem("new name", "new full name");
        cartService.saveCartItem(cartItem);
        
        List<CartItem> cartItems;
        cartItems = cartService.searchCartItems("new name", 0, 10);
        assertThat(cartItems.size(), is(equalTo(1)));
        cartItem = cartItems.get(0);
        assertThat(cartItem.getDescription(), is(equalTo("new full name")));
        
        // update
        cartItem = cartService.getCartItem("001");
        assertNotNull(cartItem);
        cartItem.setDescription("update full name");
        cartService.saveCartItem(cartItem);
        cartItem = cartService.getCartItem("001");
        assertThat(cartItem.getDescription(), is(equalTo("update full name")));
    }
    
    @Test
    public void testDeleteCartItem() {
        List<CartItem> cartItems;
        cartItems = cartService.searchCartItems("", 0, 10);
        assertThat(cartItems.size(), is(equalTo(5)));
        
        cartService.deleteCartItems("001", "002");
        
        cartItems = cartService.searchCartItems("", 0, 10);
        assertThat(cartItems.size(), is(equalTo(3)));
        
        cartItems.forEach(cartItem -> {
            assertThat(cartItem.getId(), is(not("001")));
            assertThat(cartItem.getId(), is(not("00")));
        });
    }
}
