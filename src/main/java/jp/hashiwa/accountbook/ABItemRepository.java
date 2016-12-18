package jp.hashiwa.accountbook;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ABItemRepository extends JpaRepository<ABItem, Long> {
  public ABItem findById(long id);
}
