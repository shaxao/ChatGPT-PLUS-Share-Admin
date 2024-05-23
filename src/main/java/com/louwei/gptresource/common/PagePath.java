package com.louwei.gptresource.common;


public enum PagePath {
    INDEX("index.html"),
    PRODUCT("product.html"),
    ORDER_QUERY("order-query.html"),
    PRODUCT_DETAIL("product-detail.html"),
    PANDORA("pandora");


    private final String path;

    PagePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
