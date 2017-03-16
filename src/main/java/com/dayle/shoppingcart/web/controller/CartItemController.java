package com.dayle.shoppingcart.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dayle.shoppingcart.domain.CartItem;
import com.dayle.shoppingcart.service.CartService;

@RestController
@RequestMapping(value = "/rest/cartItems")
public class CartItemController extends BaseController {
    @Autowired
    private CartService cartService;
    
    @RequestMapping(method = GET)
    public List<CartItem> searchCartItems(
            @RequestParam(defaultValue="") String keyword, 
            @RequestParam(defaultValue="0") int page, 
            @RequestParam(defaultValue="200") int pageSize) {
        traceAction("search cartItems, keyword: " + keyword);
        return cartService.searchCartItems(keyword, page, pageSize);
    }

    @RequestMapping(method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public CartItem createCartItem(@RequestBody @Valid CartItem cartItem) {
        traceAction("create cartItem, name: " + cartItem.getName());
        cartItem.setId(null);
        return cartService.saveCartItem(cartItem);
    }
    
    @RequestMapping(value = "/{id}", method = PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CartItem updateCartItem(@PathVariable String id, @RequestBody @Valid CartItem cartItem) {
        traceAction("update cartItem, id: " + id);
        cartItem.setId(id);
        return cartService.saveCartItem(cartItem);
    }
    
    @RequestMapping(method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCartItems(@RequestParam String[] ids) {
        traceAction("delete cartItems, ids: '" + Arrays.toString(ids));
        cartService.deleteCartItems(ids);
    }
}
