package jp.hashiwa.accountbook;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ABRepository extends JpaRepository<ABItem, Long> {
  public ABItem findById(long id);
}
