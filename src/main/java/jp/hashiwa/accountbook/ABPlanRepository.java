package jp.hashiwa.accountbook;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ABPlanRepository extends JpaRepository<ABPlan, Long> {
  public ABPlan findById(long id);
  public ABPlan findFirstByMonthAndType(Date month, ABType type);
  public List<ABPlan> findAllByMonthBetween(Date start, Date end, Sort sort);
  public List<ABPlan> findAllByMonth(Date month, Sort sort);
  public List<ABPlan> findAll(Sort sort);
  public ABPlan findFirstByOrderByMonthAsc();
  public ABPlan findFirstByOrderByMonthDesc();
}
