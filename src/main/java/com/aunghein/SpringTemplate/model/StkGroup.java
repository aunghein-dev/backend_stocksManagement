package com.aunghein.SpringTemplate.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "stk_group")
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

    public StkGroup() {
    }

    public StkGroup(Long groupId, String groupImage, String groupName, float groupUnitPrice, Date releasedDate, Business business, List<StkItem> items) {
        this.groupId = groupId;
        this.groupImage = groupImage;
        this.groupName = groupName;
        this.groupUnitPrice = groupUnitPrice;
        this.releasedDate = releasedDate;
        this.business = business;
        this.items = items;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public float getGroupUnitPrice() {
        return groupUnitPrice;
    }

    public void setGroupUnitPrice(float groupUnitPrice) {
        this.groupUnitPrice = groupUnitPrice;
    }

    public Date getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(Date releasedDate) {
        this.releasedDate = releasedDate;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public List<StkItem> getItems() {
        return items;
    }

    public void setItems(List<StkItem> items) {
        this.items = items;
    }

    public void addItem(StkItem item) {
        item.setStkGroup(this);
        this.items.add(item);
    }

    public void removeItem(StkItem item) {
        item.setStkGroup(null);
        this.items.remove(item);
    }

    @Override
    public String toString() {
        return "StkGroup{" +
                "groupId=" + groupId +
                ", groupImage='" + groupImage + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupUnitPrice=" + groupUnitPrice +
                ", releasedDate=" + releasedDate +
                ", items=" + items +
                '}';
    }
}
