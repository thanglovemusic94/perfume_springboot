package com.perfume.repository.custom.impl;

import com.perfume.entity.Checkout;
import com.perfume.repository.custom.CheckoutRepositoryCustom;
import org.springframework.stereotype.Repository;

@Repository
public class CheckoutRepositoryCustomImpl extends BaseRepositoryCustom<Checkout> implements CheckoutRepositoryCustom {
    public CheckoutRepositoryCustomImpl() {
        super("CO");
    }
}
