package com.perfume.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class CheckoutDTO extends BaseDTO {
    private UserDTO user;

    private CouponDTO coupon;

    private List<CheckoutItemDTO> checkoutItems;

    private String firstname;

    private String lastname;


    private Integer paymentMethod;

    private String address;

    private String email;

    private String phone;

    private Integer provinceId;

    private Integer districtId;

    private Integer wardId;
    private String note;
}
