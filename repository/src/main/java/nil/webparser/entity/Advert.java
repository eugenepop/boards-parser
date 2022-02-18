package nil.webparser.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nil.webparser.dto.ImageUnit;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "advert")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Advert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "link")
    private String link;

    @Column(name = "title")
    private String title;

    @Column(name = "price")
    private long price;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "district")
    private String district;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "sellerName")
    private String sellerName;

    @Column(name = "sellerPhone")
    private String sellerPhone;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.JOIN)
    @JoinColumn(name = "advert_id")
    private List<AdvertProperty> properties = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "advert_id")
    private List<AdvertImage> images = new ArrayList<>();

    @PostLoad
    public void setProperties() {
        for (AdvertProperty property : this.properties) {
            switch (property.getName()) {
                case CATEGORY:
                    this.category = property.getValue();
                    break;
                case TYPE:
                    this.type = property.getValue();
                    break;
                case HOUSE_TYPE:
                    this.houseType = property.getValue();
                    break;
                case TENANCY:
                    this.tenancy = property.getValue();
                    break;
                case KITCHEN_SQUARE:
                    this.kitchenSquare = property.getValue();
                    break;
                case LIVING_SQUARE:
                    this.livingSquare = property.getValue();
                    break;
                case WHOLE_SQUARE:
                    this.wholeSquare = property.getValue();
                    break;
                case PLEDGE:
                    this.pledge = property.getValue();
                    break;
                case COMMISSION:
                    this.commission = property.getValue();
                    break;
                case OWNERSHIP:
                    this.ownership = property.getValue();
                    break;
                case ROOM_TYPE:
                    this.roomCount = property.getValue();
                    break;
                case ROOM_COUNT:
                    this.roomCount = property.getValue();
                    break;
                case FLOOR:
                    this.floor = property.getValue();
                    break;
                case OF_FLOOR:
                    this.ofFloor = property.getValue();
                    break;
                default:
                    break;
            }
        }
    }

    @Transient
    private String category;

    @Transient
    private String type;

    @Transient
    private String houseType;

    @Transient
    private String tenancy;

    @Transient
    private String kitchenSquare;

    @Transient
    private String livingSquare;

    @Transient
    private String wholeSquare;

    @Transient
    private String pledge;

    @Transient
    private String commission;

    @Transient
    private String ownership;

    @Transient
    private String roomType;

    @Transient
    private String roomCount;

    @Transient
    private String floor;

    @Transient
    private String ofFloor;

    @Transient
    private List<ImageUnit> transformedImages;

    @Override
    public String toString() {
        return "Advert{" +
                "link=" + link +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", address='" + address + '\'' +
                ", district='" + district + '\'' +
                ", sellerName='" + sellerName + '\'' +
                ", sellerPhone='" + sellerPhone + '\'' +
                ", properties=" + properties +
                ", images=" + images +
                '}';
    }

    public Advert(long id, String title, String value) {
        this.id = id;
        this.title = title;
        this.properties = new ArrayList<>();
        this.getProperties().add(new AdvertProperty(value));
    }
}
