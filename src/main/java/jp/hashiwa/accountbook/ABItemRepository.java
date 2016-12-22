package jp.hashiwa.accountbook;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ABItemRepository extends JpaRepository<ABItem, Long> {
  public ABItem findById(long id);
  public List<ABItem> findByDateBetween(Date start, Date end, Sort sort);
}
