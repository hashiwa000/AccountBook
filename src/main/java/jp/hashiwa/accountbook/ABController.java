package jp.hashiwa.accountbook;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.util.List;
import java.util.stream.Collectors;
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
@RequestMapping("/")
public class ABController {

  private static final List<DateFormat> DATE_FORMATS = Arrays.<DateFormat>asList(
      new SimpleDateFormat("yyyy-MM-dd"),
      new SimpleDateFormat("yyyy/MM/dd")
    );
  private static final List<DateFormat> MONTH_FORMATS = Arrays.<DateFormat>asList(
      new SimpleDateFormat("yyyy-MM"),
      new SimpleDateFormat("yyyy/MM")
    );

  @Autowired
  ABService service;

  @RequestMapping(value="/login", method=RequestMethod.GET)
  public String login() {
    return "login";
  }
 
  @RequestMapping(value="/accountbook/show", method=RequestMethod.GET)
  public String showAccountBook(
      @RequestParam(defaultValue = "") String month,
      Model model) throws Exception
  {
    Date start;
    if (month == null || "".equals(month)) {
      start = getThisMonth();
    } else {
      start = parseMonthStr(month);
    }
    Date end = calcNextMonth(start);

    List<ABItem> items = service.selectAll(start, end);
    model.addAttribute("items", items);
    return "show_accountbook";
  }

  @RequestMapping(value="/accountbook/show", method=RequestMethod.POST)
  public String showAccountBook(
      @RequestParam(defaultValue = "") String month,
      @RequestParam(defaultValue = "-1") int idToDelete,
      Model model) throws Exception
  {
    if (0 <= idToDelete) {
      service.delete(idToDelete);
    }
    return showAccountBook(month, model);
  }

  @RequestMapping(value="/accountbook/create", method=RequestMethod.GET)
  public String createAccountBook(Model model) {
    List<ABPayer> payers = service.selectAllPayers();
    List<ABType> types = service.selectAllTypes();
    model.addAttribute("payers", payers);
    model.addAttribute("types", types);
    return "create_accountbook";
  }

  @RequestMapping(value="/accountbook/create", method=RequestMethod.POST)
  public String createAccountBook(
      @RequestParam("date") String date,
      @RequestParam("amount") long amount,
      @RequestParam("name") String name,
      @RequestParam("type") String type,
      @RequestParam("desc") String desc,
      @RequestParam("remarks") String remarks,
      Model model) throws Exception
  {
    Date d = parseDateStr(date);
    ABItem item = new ABItem(d, amount, name, type, desc, remarks);
    service.saveAndFlush(item);
    List<ABItem> items = Arrays.<ABItem>asList(item);
    model.addAttribute("created", items);
    return "created_accountbook";
  }

  private Date getThisMonth() {
    Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    c.set(year, month, 0, 0, 0, 0);
    return c.getTime();
  }

  private Date parseDateStr(String s) throws Exception {
    Exception lastException = null;
    for (DateFormat format: DATE_FORMATS) {
      try {
        return format.parse(s);
      } catch(Exception e) {
        lastException = e;
      }
    }
    throw lastException;
  }

  private Date parseMonthStr(String s) throws Exception {
    Exception lastException = null;
    for (DateFormat format: MONTH_FORMATS) {
      try {
        return format.parse(s);
      } catch(Exception e) {
        lastException = e;
      }
    }
    throw lastException;
  }

  private Date calcNextMonth(Date start) {
    Calendar endCalendar = Calendar.getInstance();
    endCalendar.setTime(start);
    endCalendar.add(Calendar.MONTH, 1);
    return endCalendar.getTime();
  }
}
