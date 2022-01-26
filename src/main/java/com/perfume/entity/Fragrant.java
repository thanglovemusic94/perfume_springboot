package com.perfume.entity;

import com.nmhung.anotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@TableName
public class Fragrant extends BaseEntity {
    public String name;
    public String description;

    @OneToMany(mappedBy = "fragrant")
    public List<Product> products;

    public Fragrant() {
    }

    public Fragrant(String name, String description, List<Product> products) {
        this.name = name;
        this.description = description;
        this.products = products;
    }
}
