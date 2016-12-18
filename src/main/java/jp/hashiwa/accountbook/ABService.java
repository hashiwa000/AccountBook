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
  ABRepository repository;

  public List<ABItem> selectAll() {
    return repository.findAll(SORT);
  }

  public ABItem select(long id) {
    return repository.findById(id);
  }

  public void saveAndFlush(ABItem item) {
    repository.saveAndFlush(item);
  }

  public void delete(long id) {
    repository.delete(id);
  }
}
