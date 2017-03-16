package com.dayle.shoppingcart.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dayle.shoppingcart.domain.CartItem;

public interface CartRepository extends JpaRepository<CartItem, String> {
    @Query("select c from CartItem c where lower(c.id) like :keyword% "
            + "or lower(c.name) like :keyword% "
            + "or lower(c.description) like :keyword% "
            + "or lower(c.cost) like :keyword% "
            + "or lower(c.optNotes) like :keyword% "
            + "order by c.name")
    List<CartItem> searchCartItem(@Param("keyword") String keyword, Pageable pageable);
    
    @Modifying
    @Query("delete from CartItem where id in (:ids)")
    void deleteCartItems(@Param("ids") String... ids);
}