package jp.hashiwa.accountbook;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ABUserRepository extends JpaRepository<ABUser, Long> {
  public ABUser findByUsername(String username);
}
