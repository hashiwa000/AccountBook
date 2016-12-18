package jp.hashiwa.accountbook;

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

  public List<ABItem> selectAll() {
    return itemRepo.findAll(SORT);
  }

  public ABItem select(long id) {
    return itemRepo.findById(id);
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

  public void saveAndFlush(ABPayer payer) {
    payerRepo.saveAndFlush(payer);
  }

  public List<ABType> selectAllTypes() {
    return typeRepo.findAll();
  }

  public void saveAndFlush(ABType type) {
    typeRepo.saveAndFlush(type);
  }

}
