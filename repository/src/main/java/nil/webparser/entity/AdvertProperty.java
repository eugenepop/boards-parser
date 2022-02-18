package nil.webparser.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "advert_property")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvertProperty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private AdvertProperty.Properties name;

    @Column(name = "value")
    private String value;

    public enum Properties {
        CATEGORY("Категория"),
        TYPE("Тип объявления"),
        HOUSE_TYPE("Тип дома"),
        TENANCY("Срок аренды"),
        KITCHEN_SQUARE("Площадь кухни"),
        LIVING_SQUARE("Жилая площадь"),
        PLEDGE("Залог"),
        COMMISSION("Размер комиссии"),
        OWNERSHIP("Право собственности"),
        ROOM_TYPE("roomType"),
        ROOM_COUNT("roomCount"),
        WHOLE_SQUARE("wholeSquare"),
        FLOOR("floor"),
        OF_FLOOR("ofFloor");

        private String name;

        Properties(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public String toString() {
        return "AdvertProperty{" +
                "id=" + id +
                ", name=" + name +
                ", value='" + value + '\'' +
                '}';
    }

    public AdvertProperty(String value) {
        this.value = value;
    }
}
