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
@Table(name="account_book")
public class ABItem {

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private long id;

  @Column(name="date")
  private Date date;

  @Column(name="amount")
  private Long amount;

  @OneToOne
  @JoinColumn(name="payer_name_id")
  private ABPayer payer;

  @OneToOne
  @JoinColumn(name="type_name_id")
  private ABType type;

  @Column(name="description")
  private String description;

  @Column(name="remarks")
  private String remarks;

  public ABItem() {}
  public ABItem(Date date, Long amount, ABPayer payer, ABType type) {
    this(date, amount, payer, type, null, null);
  }
  public ABItem(Date date, Long amount, ABPayer payer, ABType type, String description) {
    this(date, amount, payer, type, description, null);
  }
  public ABItem(Date date, Long amount, ABPayer payer, ABType type, String description, String remarks) {
    this.date = date;
    this.amount = amount;
    this.payer = payer;
    this.type = type;
    this.description = description;
    this.remarks = remarks;
  }
}
