package jp.hashiwa.accountbook;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="plan_info")
public class ABPlan {

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private long id;

  @Column(name="month")
  private Date month;

  @OneToOne
  @JoinColumn(name="type_info_id")
  private ABType type;

  @Column(name="amount")
  private Long amount;

  public ABPlan() {}
  public ABPlan(Date month, ABType type, Long amount) {
    this.month = month;
    this.type = type;
    this.amount = amount;
  }
}
