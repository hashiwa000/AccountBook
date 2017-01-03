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
  private static final Sort ITEM_SORT = new Sort(Sort.Direction.ASC, "date", "id");
  private static final Sort PLAN_SORT = new Sort(Sort.Direction.ASC, "id");

  @Autowired
  ABItemRepository itemRepo;

  @Autowired
  ABPayerRepository payerRepo;

  @Autowired
  ABTypeRepository typeRepo;

  @Autowired
  ABPlanRepository planRepo;

  public List<ABItem> selectAll() {
    return itemRepo.findAll(ITEM_SORT);
  }

  public List<ABItem> selectAll(Date start, Date end) {
    return itemRepo.findByDateBetween(start, end, ITEM_SORT);
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
    return planRepo.findAllByMonthBetween(start, end, PLAN_SORT);
  }

  public List<ABPlan> selectAllPlans(Date month) {
    return planRepo.findAllByMonth(month, PLAN_SORT);
  }

  public List<ABPlan> selectAllPlans() {
    return planRepo.findAll(PLAN_SORT);
  }

  public ABPlan selectOldestPlan() {
    return planRepo.findFirstByOrderByMonthAsc();
  }

  public ABPlan selectNewestPlan() {
    return planRepo.findFirstByOrderByMonthDesc();
  }

}
