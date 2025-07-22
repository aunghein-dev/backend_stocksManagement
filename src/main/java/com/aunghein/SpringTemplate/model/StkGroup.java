package com.aunghein.SpringTemplate.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "stk_group")
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class StkGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    private String groupImage;

    private String groupName;
    private float groupUnitPrice;

    @Temporal(TemporalType.TIMESTAMP)
    private Date releasedDate;

    @ManyToOne
    @JoinColumn(name = "business_id", nullable = false)
    @JsonBackReference
    private Business business;

    @OneToMany(mappedBy = "stkGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<StkItem> items;

    public void addItem(StkItem item) {
        item.setStkGroup(this);
        this.items.add(item);
    }

    public void removeItem(StkItem item) {
        item.setStkGroup(null);
        this.items.remove(item);
    }

}
