package jp.hashiwa.accountbook;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ABPayerRepository extends JpaRepository<ABPayer, Long> {
  public ABPayer findByName(String name);
}
