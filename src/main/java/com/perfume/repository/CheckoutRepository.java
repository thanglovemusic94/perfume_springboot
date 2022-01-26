package com.perfume.repository;

import com.perfume.entity.Checkout;
import com.perfume.repository.custom.CheckoutRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout,Long>, CheckoutRepositoryCustom {
}
