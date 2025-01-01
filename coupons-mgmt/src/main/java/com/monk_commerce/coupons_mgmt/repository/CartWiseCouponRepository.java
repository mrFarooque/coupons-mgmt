package com.monk_commerce.coupons_mgmt.repository;

import com.monk_commerce.coupons_mgmt.entity.CartWiseCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartWiseCouponRepository extends JpaRepository<CartWiseCouponEntity, Long> {
    List<CartWiseCouponEntity> findByIsDeletedFalse();
}
