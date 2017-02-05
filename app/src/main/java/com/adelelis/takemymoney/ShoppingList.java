package com.adelelis.takemymoney;

import java.util.HashMap;

public class ShoppingList {

    private String id;
    private String name;
    private HashMap<Integer, Product> products;

    public ShoppingList(String id, String name, HashMap<Integer, Product> products) {
        this.id = id;
        this.name = name;
        this.products = products;
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

    public HashMap<Integer, Product> getProducts() {
        return products;
    }

    public void setProducts(HashMap<Integer, Product> products) {
        this.products = products;
    }
}
