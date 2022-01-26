package com.perfume.controller;

import com.perfume.constant.CheckoutStatus;
import com.perfume.constant.StatusEnum;
import com.perfume.dto.CheckoutDTO;
import com.perfume.dto.PagingDTO;
import com.perfume.dto.ResponseMsg;
import com.perfume.dto.ResponsePaging;
import com.perfume.dto.mapper.CheckoutMapper;
import com.perfume.entity.*;
import com.perfume.repository.*;
import com.perfume.sercurity.JwtToken;
import com.perfume.util.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    @Autowired
    CheckoutRepository checkoutRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CheckoutItemRepository checkoutItemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CartItemRepository cartItemRepository;


    @Autowired
    private JwtToken jwtToken;

    @Autowired
    private CheckoutMapper checkoutMapper;

    @Autowired
    MailUtils mailUtils;

    @Autowired
    CouponRepository couponRepository;


//    @PutMapping("/delivery/{id}")
//    public ResponseEntity<ResponseMsg<Object>> changeStatusDelivery(@PathVariable Long id) {
//        return changeStatus(id, CheckoutStatus.DELIVERY, null);
//    }
//
//    @PutMapping("/done/{id}")
//    public ResponseEntity<ResponseMsg<Object>> changeStatusDone(@PathVariable Long id) {
//        return changeStatus(id, CheckoutStatus.DONE, null);
//    }
//
//    @PutMapping("/deleted/{id}")
//    public ResponseEntity<ResponseMsg<Object>> changeStatusDeleted(@PathVariable Long id, @RequestParam String node) {
//        System.out.println(node);
//        return changeStatus(id, CheckoutStatus.DELETED, node);
//    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseMsg<Object>> update(@PathVariable Long id, @RequestBody Checkout body) {
        body.setId(id);
        boolean rs = false;
        if (body.getStatus() != null) {
            Checkout tmp = checkoutRepository.getOne(id);
            tmp.setStatus(body.getStatus());
            if (tmp.getStatus() == CheckoutStatus.DELETED.getValue()) {
                tmp.setNote(body.getNote());
            }

            tmp = checkoutRepository.save(tmp);
            if (tmp != null) {
                mailUtils.send(tmp);
            }
            rs = tmp != null;
        } else {
            rs = checkoutRepository.update(body);

        }
        int statusRs = rs ? 200 : 400;
        return ResponseEntity.ok(new ResponseMsg(null, statusRs, null));
    }


    private ResponseEntity<ResponseMsg<Object>> changeStatus(Long id, CheckoutStatus status, String node) {
        Checkout checkout = new Checkout();
        checkout.setStatus(status.getValue());
        checkout.setId(id);
        if (status == CheckoutStatus.DELETED) {
            checkout.setNote(node);
        }
        boolean rs = checkoutRepository.update(checkout);
        int statusRs = rs ? 200 : 400;
        return ResponseEntity.ok(new ResponseMsg(null, statusRs, null));
    }

    @PostMapping("")
    public ResponseEntity<ResponseMsg<Checkout>> checkout(@RequestBody Checkout checkout, HttpServletRequest request) {
        ResponseMsg<Checkout> responseMsg = new ResponseMsg<>(null, 200, "");

        if (checkout.getCoupon() != null) {
            String codeCoupon = checkout.getCoupon().getCode();
            if (codeCoupon == null) {
                checkout.setCoupon(null);
            } else {
                Coupon coupon = couponRepository.getByCodeValidate(codeCoupon);
                if (coupon == null) {
                    responseMsg.setStatus(400);
                    responseMsg.setMsg("Mã giảm giả không phù hợp");
                    return ResponseEntity.status(responseMsg.getStatus()).body(responseMsg);
                } else {
                    checkout.setCoupon(coupon);
                }
            }
        }
        User user = jwtToken.getUserLogin(request);


        List<CartItem> carts = user.getCartItems();
        if (carts.size() > 0) {

            checkout.setId(null);
            checkout.setStatus(CheckoutStatus.ACTIVE.getValue());


            List<CheckoutItem> checkoutItems = carts.stream().map(cartItem -> {
                CheckoutItem checkoutItem = new CheckoutItem(cartItem.getQuantity(), cartItem.getVersion(), checkout);
                return checkoutItem;
            }).collect(Collectors.toList());
            checkout.setCheckoutItems(checkoutItems);
            checkout.setUser(user);
            Checkout tmp = checkoutRepository.save(checkout);
            if (tmp != null) {
                mailUtils.send(tmp);

                cartItemRepository.deleteAll(carts);

                this.updateTotalSold(checkoutItems);
            } else {
                responseMsg.setStatus(400);
                responseMsg.setMsg("checkout thất bại");
            }
            System.out.println();

        } else {
            responseMsg.setStatus(300);
            responseMsg.setMsg("Giỏ hàng không có hàng");
        }
        return ResponseEntity.status(responseMsg.getStatus()).body(responseMsg);
    }


    @PostMapping("/filter/{page}/{limit}")
    public ResponseEntity<ResponsePaging<CheckoutDTO>> filterPage(@RequestBody Checkout body, @PathVariable int page, @PathVariable int limit) {
//        body.setStatus(StatusEnum.ACTIVE.getValue());
        Pageable paging = PageRequest.of(page - 1, limit);

        Page<Checkout> pagedResult = checkoutRepository.findPage(body, paging);

        List<CheckoutDTO> producers = new ArrayList<>();

        if (pagedResult.hasContent()) {
            producers = pagedResult.getContent().stream().map(checkoutMapper::toDTO).collect(Collectors.toList());
        }

        PagingDTO pagingDTO = new PagingDTO(pagedResult.getTotalElements(), page, limit, paging.getOffset());
        return ResponseEntity.ok(new ResponsePaging<>(producers, pagingDTO));
    }


    private void updateTotalSold(List<CheckoutItem> checkoutItems) {
        Map<Long, Long> data = new HashMap<>();

//        checkoutItems.forEach(item -> {
//            if(data.containsKey(item.get))
//        });

        List<Product> list = productRepository.findAllById(data.keySet());
        list.forEach(item -> {
            Long totalSold = item.getTotalSold();
            if (totalSold == null) {
                item.setTotalSold(0L);
            }
            totalSold += data.get(item.getId());
            item.setTotalSold(totalSold);
        });
        productRepository.saveAll(list);
    }

}
