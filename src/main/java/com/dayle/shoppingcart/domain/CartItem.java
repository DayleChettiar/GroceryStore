package com.dayle.shoppingcart.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "CARTITEM")
public class CartItem {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "NAME")
    @NotEmpty(message = "{validation.not-empty.message}")
    private String name;

    @Column(name = "DESCRIPTION")
    @NotEmpty(message = "{validation.not-empty.message}")
    private String description;

    @Column(name = "COST")
    private String cost;

    @Column(name = "OPTNOTES")
    private String optNotes;

//    @Column(name = "MOBILE")
//    @Pattern(regexp = "\\d{3,4} \\d{3} \\d{4}", message = "{validation.mobile.message}")
//    private String mobile;
//
//    @Column(name = "SKYPE_ID")
//    @Pattern(regexp = "[A-Za-z0-9_,\\-\\.]{6,32}", message = "{validation.skypeid.message}")
//    private String skypeId;
    
    public CartItem() {
    }
    
    public CartItem(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getOptNotes() {
		return optNotes;
	}

	public void setOptNotes(String optNotes) {
		this.optNotes = optNotes;
	}

	@Override
    public boolean equals(Object obj) {
        if (id != null && obj instanceof CartItem) {
            return id.equals(((CartItem) obj).name);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (id == null) ? 0 : id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s|%s|%s|%s|%s|%s|%s", id, name, description, cost, optNotes);
    }
    
    public static CartItem parseCartItem(String cartItem) {
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
        
        return cItem;
    }
}
