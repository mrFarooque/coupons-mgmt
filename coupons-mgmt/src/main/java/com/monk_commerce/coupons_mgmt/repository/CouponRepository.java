package com.monk_commerce.coupons_mgmt.repository;

import com.monk_commerce.coupons_mgmt.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
}
