package jp.hashiwa.accountbook;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ABTypeRepository extends JpaRepository<ABType, Long> {
  public ABType findByName(String type);
}
