package com.emusicstore.model;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private String cartId;
    private Map<Integer, CartItem> cartItems;
    private double grandTotal;

    public Cart(){
        cartItems = new HashMap<Integer, CartItem>();
        grandTotal = 0;
    }

    public Cart(String cartId) {
        this();
        this.cartId = cartId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public Map<Integer, CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(Map<Integer, CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public void addCartItem(CartItem item){
        int productID = item.getProduct().getProductId();

        if (cartItems.containsKey(productID)) {
            CartItem existingItem = cartItems.get(productID);
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
        } else {
            cartItems.put(productID, item);
        }

        updateGrandTotal();
    }

    public void removeCartItem(CartItem item){
        int productId = item.getProduct().getProductId();
        cartItems.remove(productId);
        updateGrandTotal();
    }

    private void updateGrandTotal() {
        grandTotal = 0;
        for (CartItem item : cartItems.values()) {
            grandTotal += item.getTotalPrice();
        }
    }
}
