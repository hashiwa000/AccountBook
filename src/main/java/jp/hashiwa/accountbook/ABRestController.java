package jp.hashiwa.accountbook;

import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/rest/accountbook")
public class ABRestController {

  private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

  @Autowired
  ABService service;

  @RequestMapping(method=RequestMethod.GET)
  public List<ABItem> getAll() {
    return service.selectAll();
  }

  @RequestMapping(value="/{id}", method=RequestMethod.GET)
  public ABItem get(@PathVariable long id) {
    // TODO: found check
    return service.select(id);
  }

  @RequestMapping(method=RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public ABItem post(
      @RequestParam("date") String date,
      @RequestParam("amount") String amount,
      @RequestParam("name") String name,
      @RequestParam("type") String type,
      @RequestParam("desc") String desc,
      @RequestParam("remarks") String remarks) throws ParseException
  {
    Date d = format.parse(date);
    long a = Long.parseLong(amount);
    ABItem item = new ABItem(d, a, name, type, desc, remarks);
    service.saveAndFlush(item);
    return item;
  }

  @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
  public void delete(@PathVariable long id) {
    service.delete(id);
    // TODO: catch org.springframework.dao.EmptyResultDataAccessException
  }
}
