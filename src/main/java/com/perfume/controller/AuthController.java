package com.perfume.controller;

import com.perfume.constant.RoleEnum;
import com.perfume.constant.StatusEnum;
import com.perfume.dto.ResponseMsg;
import com.perfume.dto.UserDTO;
import com.perfume.dto.mapper.UserMapper;
import com.perfume.entity.JwtRequest;
import com.perfume.entity.JwtResponse;
import com.perfume.entity.Role;
import com.perfume.entity.User;
import com.perfume.repository.RoleRepository;
import com.perfume.repository.UserRepository;
import com.perfume.sercurity.JwtToken;
import com.perfume.sercurity.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Locale;

@RestController()
@RequestMapping("/api")
public class AuthController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private JwtToken jwtTokenUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(), authenticationRequest.getPassword()
                )
        );

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByUsername(authenticationRequest.getUsername());
        user.setPassword(null);
        return ResponseEntity.ok(new JwtResponse(token, user));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseMsg<UserDTO>> register(@RequestBody UserDTO userDTO){
        ResponseMsg<UserDTO> responseMsg = new ResponseMsg();
        if(userDTO.password.equals(userDTO.getConfirmPassword())){
            if(!userRepository.existsByUsername(userDTO.getUsername())){
                User user = userMapper.toEntity(userDTO);
                Role role = roleRepository.findByName(RoleEnum.ROLE_MEMBER.toString());
                user.setRoles(Arrays.asList(role));
                String passwordEncode = passwordEncoder.encode(user.getPassword());
                user.setPassword(passwordEncode);
                user.setStatus(StatusEnum.ACTIVE.getValue());
                user = userRepository.save(user);
                if(user != null){
                    responseMsg.setMsg("Tạo tài khoản thành công");
                    responseMsg.setStatus(200);
                    responseMsg.setData(userMapper.toDTO(user));
                }else{
                    responseMsg.setMsg("Lỗi hệ thống");
                }
            }else{
                responseMsg.setMsg("Tài khoản đã tồn tại");
            }
        }else{
            responseMsg.setMsg("Mật khâu không giống nhau");
        }


        return ResponseEntity.ok(responseMsg);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getRoles(){
        return ResponseEntity.ok(roleRepository.findAll());
    }

    @PutMapping("/change-password")
    public ResponseEntity update(Locale locale, @RequestBody UserDTO userDTO){
        ResponseMsg responseMsg = new ResponseMsg();
        User user = userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName());

        if (!userDTO.getConfirmPassword().equals(userDTO.getPassword())) {
            responseMsg.setMsg("Confirmed password does not match");
            return ResponseEntity.ok(responseMsg);
        }

        if (!passwordEncoder.matches(userDTO.getOldPassworld(), user.getPassword())) {
            responseMsg.setMsg("Wrong old password");
            return ResponseEntity.ok(responseMsg);
        }else{
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userRepository.save(user);
            responseMsg.setStatus(200);
        }
//        userService.changeUserPassword(user, password);
        return ResponseEntity.ok(responseMsg);
    }

    @PutMapping("/change-user-login")
    public ResponseEntity<ResponseMsg<UserDTO>> update(@RequestBody UserDTO user, HttpServletRequest request) {
        user.setUsername(null);
        ResponseMsg responseMsg = new ResponseMsg();
        User userLogin = jwtTokenUtil.getUserLogin(request);
        if (userLogin == null) {
            return ResponseEntity.ok(responseMsg);
        }


        if (user.getPassword() != null && user.getConfirmPassword() != null && user.getOldPassworld() != null) {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            user.getUsername(), user.getOldPassworld()
//                    )
//            );
            if (true) {
                if (user.getPassword().equals(user.getConfirmPassword())) {
                    User update = new User();
                    update.setId(userLogin.getId());
                    userLogin.setPassword(user.getPassword());
                    if (userRepository.update(update)) {
                        responseMsg.setMsg("Đổi mật khẩu thành công");
                        responseMsg.setStatus(200);
                    } else {
                        responseMsg.setMsg("Phát sinh lỗi");
                    }
                } else {
                    responseMsg.setMsg("Mật khẩu không giống nhau");
                }
            } else {
                responseMsg.setMsg("Nhập sai mật khẩu cũ");
            }

            return ResponseEntity.ok(responseMsg);
        }

        user.setPassword(null);
        user.setUsername(null);

        User update = userMapper.toEntity(user);
        update.setId(userLogin.getId());
        if (userRepository.update(update)) {
            responseMsg.setStatus(200);
            responseMsg.setMsg("Đổi thông tin Thành công");
        } else {
            responseMsg.setMsg("Phát sinh lỗi");
        }

        return ResponseEntity.ok(responseMsg);
    }

}
