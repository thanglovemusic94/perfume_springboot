package com.perfume.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nmhung.anotation.QueryField;
import com.nmhung.anotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import java.util.List;

@Entity
@Data
@TableName
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    @Column(unique = true)
    @QueryField
    public String username;
    @QueryField
    public String firstname;
    @QueryField
    public String lastname;
    @QueryField
    public String email;
    @QueryField
    public String address;
    @QueryField
    public String phone;
    @JsonIgnore
    public String password;

    public String image;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role")
    public List<Role> roles;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    public List<CartItem> cartItems;

    public User() {
    }

    public User(String username, String firstname, String lastname, String email, String address, String phone, String password, List<Role> roles, List<CartItem> cartItems) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.password = password;
        this.roles = roles;
        this.cartItems = cartItems;
    }
}
