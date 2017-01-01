package jp.hashiwa.accountbook;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="type_info")
public class ABType {

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private long id;

  @Column(name="name")
  private String name;

  public ABType() {}
  public ABType(String name) {
    this.name = name;
  }

  public String toString() {
    return getName();
  }
}
