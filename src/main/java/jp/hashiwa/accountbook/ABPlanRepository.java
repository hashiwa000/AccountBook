package jp.hashiwa.accountbook;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ABPlanRepository extends JpaRepository<ABPlan, Long> {
  public ABPlan findById(long id);
  public ABPlan findFirstByMonthAndType(Date month, ABType type);
  public List<ABPlan> findAllByMonthBetween(Date start, Date end);
  public List<ABPlan> findAllByMonth(Date month);
  public List<ABPlan> findAll();
  public ABPlan findFirstByOrderByMonthAsc();
  public ABPlan findFirstByOrderByMonthDesc();
}
