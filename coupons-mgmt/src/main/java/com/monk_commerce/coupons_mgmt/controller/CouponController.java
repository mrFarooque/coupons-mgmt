package com.monk_commerce.coupons_mgmt.controller;

import com.monk_commerce.coupons_mgmt.model.*;
import com.monk_commerce.coupons_mgmt.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/coupons")
@Slf4j
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Coupon> createCoupon(@Valid @RequestBody Coupon coupon) {
        log.info("Create Coupon request received: {}", coupon);
        Coupon createdCoupon = couponService.createCoupon(coupon);
        log.info("Coupon created successfully: {}", createdCoupon);
        return new ResponseEntity<>(createdCoupon, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @Valid @RequestBody Coupon couponRequest) {
        log.info("Update Coupon request received for ID {}: {}", id, couponRequest);
        Coupon updatedCoupon = couponService.updateCoupon(id, couponRequest);
        log.info("Coupon updated successfully for ID {}: {}", id, updatedCoupon);
        return new ResponseEntity<>(updatedCoupon, HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Coupons> getCoupons() {
        return ResponseEntity.ok(couponService.getCoupons());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Coupon> getCoupon(@PathVariable Long id) {
        log.info("Get Coupon request received for ID {}", id);
        Coupon coupon = couponService.getCouponById(id);
        log.info("Retrieved coupon for ID {}: {}", id, coupon);
        return ResponseEntity.ok(coupon);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCouponById(@PathVariable Long id) {
        log.info("Delete Coupon request received for ID {}", id);
        couponService.deleteCouponById(id);
        log.info("Coupon deleted successfully for ID {}", id);
    }

    @PostMapping(value = "/applicable-coupons")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApplicableCoupons> applicableCoupons(@RequestBody Cart cart) {
        log.info("Applicable Coupons request received: {}", cart);
        ApplicableCoupons applicableCoupons = couponService.applicableCoupons(cart);
        log.info("Applicable coupons: {}", applicableCoupons);
        return ResponseEntity.ok(applicableCoupons);
    }

    @PostMapping(value = "/apply-coupon/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UpdatedCartDetails> applyCoupon(@PathVariable Long id, @RequestBody Cart cart) {
        log.info("Apply Coupon request received for coupon ID {}: {}", id, cart);
        UpdatedCartDetails updatedCartDetails = couponService.applyCoupon(id, cart);
        log.info("Coupon applied successfully for ID {}: {}", id, updatedCartDetails);
        return ResponseEntity.ok(updatedCartDetails);
    }
}
