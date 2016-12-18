package jp.hashiwa.accountbook;

import java.util.List;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/accountbook")
public class ABController {

  private static final List<DateFormat> DATETIME_FORMATS = Arrays.<DateFormat>asList(
      new SimpleDateFormat("yyyy-MM-dd"),
      new SimpleDateFormat("yyyy/MM/dd")
    );

  @Autowired
  ABService service;

  @RequestMapping(value="/show", method=RequestMethod.GET)
  public String showAccountBook(
      //@RequestParam(defaultValue = "") String all,
      Model model)
  {
    //boolean isAll = "true".equals(all.toLowerCase());
    List<ABItem> items = service.selectAll();
    //if (!isAll) {
    //  items = items.stream()
    //    .filter(i -> i.done() == false)
    //    .collect(Collectors.toList());
    //}
    model.addAttribute("items", items);
    return "show_accountbook";
  }

  @RequestMapping(value="/show", method=RequestMethod.POST)
  public String showAccountBook(
      @RequestParam(defaultValue = "-1") int idToDelete,
      Model model)
  {
    if (0 <= idToDelete) {
      service.delete(idToDelete);
    }
    return showAccountBook(model);
  }

  @RequestMapping(value="/create", method=RequestMethod.GET)
  public String createAccountBook(Model model) {
    List<ABPayer> payers = service.selectAllPayer();
    model.addAttribute("payers", payers);
    return "create_accountbook";
  }

  @RequestMapping(value="/create", method=RequestMethod.POST)
  public String createAccountBook(
      @RequestParam("date") String date,
      @RequestParam("amount") long amount,
      @RequestParam("name") String name,
      @RequestParam("type") String type,
      @RequestParam("desc") String desc,
      @RequestParam("remarks") String remarks,
      Model model) throws Exception
  {
    Date d = parseDateTimeStr(date);
    ABItem item = new ABItem(d, amount, name, type, desc, remarks);
    service.saveAndFlush(item);
    List<ABItem> items = Arrays.<ABItem>asList(item);
    model.addAttribute("created", items);
    return "created_accountbook";
  }

  private Date parseDateTimeStr(String s) throws Exception {
    Exception lastException = null;
    for (DateFormat format: DATETIME_FORMATS) {
      try {
        return format.parse(s);
      } catch(Exception e) {
        lastException = e;
      }
    }
    throw lastException;
  }
}
