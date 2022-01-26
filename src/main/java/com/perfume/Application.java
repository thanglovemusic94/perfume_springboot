package com.perfume;

import com.perfume.constant.RoleEnum;
import com.perfume.constant.StatusEnum;
import com.perfume.constant.TargetEnum;
import com.perfume.dto.mapper.UserMapper;
import com.perfume.entity.Checkout;
import com.perfume.entity.Role;
import com.perfume.entity.Target;
import com.perfume.entity.User;
import com.perfume.repository.ProductRepository;
import com.perfume.repository.RoleRepository;
import com.perfume.repository.TargetRepository;
import com.perfume.repository.UserRepository;
import com.perfume.util.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;


@SpringBootApplication(scanBasePackages = "com.perfume")
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    TargetRepository targetRepository;
    @Autowired
    UserMapper userMapper;

    @Autowired
    ProductRepository productRepository;


    @Autowired
    MailUtils mailUtils;


    @Override
    public void run(String... args) throws Exception {


//        this.sendMail();

        // Khi chương trình chạy
//         Insert vào csdl một user.
        User user = new User();
        user.setUsername("admin");
        user.setPassword(new BCryptPasswordEncoder().encode("123456"));
        user.setAddress("Thái Nguyên");
        user.setEmail("nmhung190398@gmail.com");
        user.setFirstname("hung");
        user.setFirstname("nguyen");
        user.setStatus(StatusEnum.ACTIVE.getValue());

        List<Target> targets = Arrays.asList(

                new Target(TargetEnum.MALE.getValue(), StatusEnum.ACTIVE.getValue()),
                new Target(TargetEnum.FEMALE.getValue(), StatusEnum.ACTIVE.getValue()),
                new Target(TargetEnum.GAY.getValue(), StatusEnum.ACTIVE.getValue()),
                new Target(TargetEnum.LES.getValue(), StatusEnum.ACTIVE.getValue()),
                new Target(TargetEnum.CAR.getValue(), StatusEnum.ACTIVE.getValue())
        );

        for (Target target :
                targets) {
            if (targetRepository.findByName(target.getName()) == null) {
                targetRepository.save(target);
            }
        }

        List<Role> roles = Arrays.asList(
                new Role(RoleEnum.ROLE_ADMIN.toString(), Arrays.asList(user)),
                new Role(RoleEnum.ROLE_EMPLOYEE.toString(), Arrays.asList(user)),
                new Role(RoleEnum.ROLE_MEMBER.toString(), Arrays.asList(user))
        );
        for (int i = 0; i < roles.size(); ++i) {
            if (roleRepository.findByName(roles.get(i).getName()) == null) {
                roleRepository.save(roles.get(i));
            }
        }

        if (userRepository.findByUsername(user.getUsername()) == null) {
            user.setRoles(roleRepository.findAll());
            userRepository.save(user);
        }

        testMapper();

    }

    public void sendMail() {
        Checkout checkout = new Checkout();
        checkout.setEmail("manhhung19031998@gmail.com");
        checkout.setAddress("hdz");
        checkout.setFirstname("HDZ");
        checkout.setLastname("ahihi");

        mailUtils.send(checkout);

    }

    public void testMapper() {
//        Product product = Product.builder().categoryId(1L).build();
//        List<Product> list = productRepository.find(product);
//        User user = User.builder().username("admin").password(new BCryptPasswordEncoder().encode("123456"))
//                .address("Thái Nguyên").email("nmhung190398@gmail.com").firstname("hung").lastname("nguyen")
//                .build();
//        user.setId(1L);
        MultiValueMap<String, Object> queryParams = new LinkedMultiValueMap<String, Object>();
////        queryParams.add("id",1L);
//        List<User> users = null;
//        users = userRepository.find(user);
////        List<User> users = userRepository.filter(queryParams,null);
//        UserDTO userDTO = userMapper.toDTO(users.get(0));
//        System.out.println(users.get(0).getUsername());ull
        queryParams.add("id", "1");

        List<Target> targets = targetRepository.filter(new LinkedMultiValueMap<>());
//        Long cout = targetRepository.count(new LinkedMultiValueMap<>());
//        Page<Target> targetPage = targetRepository.filterPage(new LinkedMultiValueMap<>(),PageRequest.of(1,2));

        System.out.println("");
    }
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedMethods("GET", "POST", "PUT", "DELETE")
//                        .allowedOrigins("*").maxAge(3600).allowedHeaders("*");
//            }
//        };
//    }

}
