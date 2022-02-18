package nil.webparser.comparator;

import com.univocity.parsers.annotations.EnumOptions;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;
import com.univocity.parsers.conversions.EnumSelector;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "points_out")
@Data
@NoArgsConstructor
public class PointsRow {

    public static final String DATE_FORMAT = "MMddyyyy";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    private Long vendorId;

    @Trim
    @Parsed(index = 0)
    @EnumOptions(selectors = EnumSelector.CUSTOM_METHOD, customElement = "getValue")
    private PointsOutRecordType recordType;

    @Trim
    @Parsed(index = 1)
    @Column(length = 20)
    private String customerId;

    @Trim
    @Parsed(index = 2)
    @Column(length = 14)
    private String loyaltyNumber;

    @Parsed(index = 3)
    private String ipCode;

    @Parsed(index = 4)
    @Column
    private Integer totalPoints;

    @Parsed(index = 5)
    @Column
    private Integer pointsToNextReward;

    @Parsed(index = 6)
    @Column
    private Integer availablePoints;

    @Parsed(index = 7)
    @Column
    private Integer pendingPoints;

    @Parsed(index = 8)
    private Integer availableBraCredits;

    @Parsed(index = 9)
    private Integer pendingBraCredits;

    @Parsed(index = 10)
    private Integer totalBraCredits;

    @Parsed(index = 11)
    private Integer availableJeanCredits;

    @Parsed(index = 12)
    private Integer pendingJeanCredits;

    @Parsed(index = 13)
    private Integer totalJeanCredits;

    @Parsed(index = 14)
    @Column
    private BigDecimal netSpendCurrentYear;

    @Parsed(index = 15)
    @Column
    private BigDecimal netSpendPreviousYear;

    @Parsed(index = 16)
    @Column
    private BigDecimal netSpendToCurrentTier;

    @Parsed(index = 17)
    @Column
    private BigDecimal netSpendToNextTier;

    @Parsed(index = 18)
    @Column
    private BigDecimal netSpendLifetime;

    @Parsed(index = 19)
    @Column
    private String pointsCreditExpireDate;

    @Trim
    @Parsed(index = 20)
    @Column(length = 30)
    private String memberRewardId;

    @Parsed(index = 21)
    private Integer pointTransactionType;

    @Parsed(index = 22, defaultNullRead = "0")
    private Long pointEventId;

    @Parsed(index = 23)
    @Column
    private Integer basePoints;

    @Parsed(index = 24)
    @Column
    private Integer bonusPoints;

    @Parsed(index = 25)
    private Integer braCredits;

    @Parsed(index = 26)
    private Integer jeanCredits;

    @Parsed(index = 27)
    @Column
    private String transactionDate;

    @Trim
    @Parsed(index = 28, defaultNullRead = "")
    @Column(length = 11)
    private String transactionNumber;

    @Trim
    @Parsed(index = 29, defaultNullRead = "")
    @Column(length = 20)
    private String orderNumber;

    @Trim
    @Parsed(index = 30)
    @Column(length = 10)
    private Integer storeNumber;

    @Trim
    @Parsed(index = 31)
    @Column(length = 10)
    private Integer registerNumber;

}
