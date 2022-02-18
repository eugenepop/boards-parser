package nil.webparser.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "advert_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvertImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "source_link")
    private String sourceLink;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "processed", columnDefinition = "bool default false")
    private boolean processed;

    @Column(name = "success", columnDefinition = "bool")
    private Boolean success;

    @CreationTimestamp
    @Column(name = "create_date", columnDefinition = "timestamp default CURRENT_TIMESTAMP not null")
    private Instant createDate;

    @UpdateTimestamp
    @Column(name = "modify_date", columnDefinition = "timestamp default CURRENT_TIMESTAMP not null")
    private Instant modifyDate;

    public AdvertImage(String sourceLink) {
        this.sourceLink = sourceLink;
    }

    @Override
    public String toString() {
        return "AdvertImage{" +
                "id=" + id +
                ", sourceLink='" + sourceLink +
                '}';
    }
}
