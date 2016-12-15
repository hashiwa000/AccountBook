package jp.hashiwa.accountbook;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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

  @Column(name="name")
  private String name;

  @Column(name="type")
  private String type;

  @Column(name="description")
  private String description;

  @Column(name="remarks")
  private String remarks;

  public ABItem() {}
  public ABItem(Date date, Long amount, String name, String type) {
    this(date, amount, name, type, null, null);
  }
  public ABItem(Date date, Long amount, String name, String type, String description) {
    this(date, amount, name, type, description, null);
  }
  public ABItem(Date date, Long amount, String name, String type, String description, String remarks) {
    this.date = date;
    this.amount = amount;
    this.name = name;
    this.type = type;
    this.description = description;
    this.remarks = remarks;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Date getDate() { return date; }
  public void setDate(Date date) { this.date = date; }
  public Long getAmount() { return amount; }
  public void setAmount(Long amount) { this.amount = amount; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getRemarks() { return remarks; }
  public void setRemarks(String remarks) { this.remarks = remarks; }
}
