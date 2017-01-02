package jp.hashiwa.accountbook;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ABService {
  private static final Sort SORT = new Sort(Sort.Direction.ASC, "date", "id");

  @Autowired
  ABItemRepository itemRepo;

  @Autowired
  ABPayerRepository payerRepo;

  @Autowired
  ABTypeRepository typeRepo;

  @Autowired
  ABPlanRepository planRepo;

  public List<ABItem> selectAll() {
    return itemRepo.findAll(SORT);
  }

  public List<ABItem> selectAll(Date start, Date end) {
    return itemRepo.findByDateBetween(start, end, SORT);
  }

  public ABItem select(long id) {
    return itemRepo.findById(id);
  }

  public ABItem selectOldestItem() {
    return itemRepo.findFirstByOrderByDateAsc();
  }

  public ABItem selectNewestItem() {
    return itemRepo.findFirstByOrderByDateDesc();
  }

  public void saveAndFlush(ABItem item) {
    itemRepo.saveAndFlush(item);
  }

  public void delete(long id) {
    itemRepo.delete(id);
  }

  public List<ABPayer> selectAllPayers() {
    return payerRepo.findAll();
  }

  public ABPayer selectOnePayer(String name) {
    return payerRepo.findByName(name);
  }

  public void saveAndFlush(ABPayer payer) {
    payerRepo.saveAndFlush(payer);
  }

  public List<ABType> selectAllTypes() {
    return typeRepo.findAll();
  }

  public ABType selectOneType(String type) {
    return typeRepo.findByName(type);
  }

  public void saveAndFlush(ABType type) {
    typeRepo.saveAndFlush(type);
  }

  public ABPlan selectOnePlan(Date month, ABType type) {
    return planRepo.findFirstByMonthAndType(month, type);
  }

  public void saveAndFlush(ABPlan plan) {
    planRepo.saveAndFlush(plan);
  }

  public List<ABPlan> selectAllPlans(Date start, Date end) {
    return planRepo.findAllByMonthBetween(start, end);
  }

  public List<ABPlan> selectAllPlans(Date month) {
    return planRepo.findAllByMonth(month);
  }

  public List<ABPlan> selectAllPlans() {
    return planRepo.findAll();
  }

  public ABPlan selectOldestPlan() {
    return planRepo.findFirstByOrderByMonthAsc();
  }

  public ABPlan selectNewestPlan() {
    return planRepo.findFirstByOrderByMonthDesc();
  }

}
