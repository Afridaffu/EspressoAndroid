package com.coyni.android.model.giftcard;

import java.util.List;

public class GiftCard {
    private String catalogName;
    private List<Brand> brands;

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }
}
