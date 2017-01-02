package jp.hashiwa.accountbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

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

import jp.hashiwa.accountbook.statistics.ABStatistics;

@Controller
@RequestMapping("/")
public class ABController {

  private static final DateFormat MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");
  private static final List<DateFormat> DATE_FORMATS = Arrays.<DateFormat>asList(
      new SimpleDateFormat("yyyy-MM-dd"),
      new SimpleDateFormat("yyyy/MM/dd")
    );
  private static final List<DateFormat> MONTH_FORMATS = Arrays.<DateFormat>asList(
      MONTH_FORMAT,
      new SimpleDateFormat("yyyy/MM")
    );

  @Autowired
  ABService service;

  @RequestMapping(value="/login", method=RequestMethod.GET)
  public String login() {
    return "login";
  }
 
  @RequestMapping(value="/plan/show", method=RequestMethod.GET)
  public String showPlan(
      @RequestParam(defaultValue = "") String month,
      Model model) throws Exception
  {
    Date start;
    if (month == null || "".equals(month.trim())) {
      start = getThisMonth();
    } else {
      start = parseMonthStr(month);
    }
    Date end = calcMonthEnd(start);
    List<ABPlan> plans = service.selectAllPlans(start, end);
    model.addAttribute("plans", plans);

    List<String> months = getShowMonths();
    model.addAttribute("months", months);
    model.addAttribute("thisMonth", MONTH_FORMAT.format(start));
    return "show_plan_accountbook";
  }

  @RequestMapping(value="/plan/create", method=RequestMethod.GET)
  public String createPlan(
      @RequestParam(defaultValue = "") String month,
      Model model) throws Exception
  {
    Date start;
    if (month == null || "".equals(month.trim())) {
      start = getThisMonth();
    } else {
      start = parseMonthStr(month);
    }
    Date end = calcMonthEnd(start);
    List<ABPlan> plans = service.selectAllPlans(start, end);
    List<ABType> types = service.selectAllTypes();
    if (plans.size() < types.size()) {
      for (ABType type: types) {
        boolean exist = plans.stream()
          .map(plan -> plan.getType())
          .anyMatch(t -> t.getId() == type.getId());
        if (!exist) {
          plans.add(new ABPlan(start, type, 0L));
        }
      }
    }
    model.addAttribute("plans", plans);
    model.addAttribute("thisMonth", MONTH_FORMAT.format(start));
    return "create_plan_accountbook";
  }

  @RequestMapping(value="/plan/create", method=RequestMethod.POST)
  public String createPlan(
      HttpServletRequest request,
      Model model) throws Exception
  {
    Map map = request.getParameterMap();
    String month = ((String[])map.get("month"))[0];
    Date monthDate = parseMonthStr(month);
    List<ABType> types = service.selectAllTypes();
    for (ABType type: types) {
      long id = type.getId();
      String key = "type_" + id;
      String value = ((String[])map.get(key))[0];
      long amount = Long.parseLong(value);
      ABPlan plan = service.selectOnePlan(monthDate, type);
      if (plan == null) {
        plan = new ABPlan(monthDate, type, amount);
        service.saveAndFlush(plan);
      } else if (plan.getAmount() != amount) {
        plan.setAmount(amount);
        service.saveAndFlush(plan);
      }
    }
    return showPlan(month, model);
  }

  @RequestMapping(value="/accountbook/show", method=RequestMethod.GET)
  public String showAccountBook(
      @RequestParam(defaultValue = "") String month,
      Model model) throws Exception
  {
    Date start;
    if (month == null || "".equals(month.trim())) {
      start = getThisMonth();
    } else {
      start = parseMonthStr(month);
    }
    Date end = calcMonthEnd(start);
    List<ABItem> items = service.selectAll(start, end);
    model.addAttribute("items", items);

    List<String> months = getShowMonths();
    model.addAttribute("months", months);
    model.addAttribute("thisMonth", MONTH_FORMAT.format(start));
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
    //TODO: update oldest/newest item
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
    ABPayer payer = service.selectOnePayer(name);
    if (payer == null) {
      throw new Exception("Payer is not found: " + name); // TODO: exception
    }
    ABType t = service.selectOneType(type);
    if (t == null) {
      throw new Exception("Type is not found: " + type); // TODO: exception
    }
    ABItem item = new ABItem(d, amount, payer, t, desc, remarks);
    service.saveAndFlush(item);

    List<ABItem> items = Arrays.<ABItem>asList(item);
    model.addAttribute("created", items);
    return "created_accountbook";
  }

  @RequestMapping(value="/accountbook/stats", method=RequestMethod.GET)
  public String statsAccountBook(
      @RequestParam(defaultValue = "") String month,
      Model model) throws Exception
  {
    Date start;
    if (month == null || "".equals(month)) {
      start = getThisMonth();
    } else {
      start = parseMonthStr(month);
    }
    Date end = calcMonthEnd(start);

    List<ABItem> items = service.selectAll(start, end);
    List<ABPayer> payers = service.selectAllPayers();
    List<ABType> types = service.selectAllTypes();
    List<ABPlan> plans = service.selectAllPlans(start, end);
    ABStatistics stats = new ABStatistics(items, payers, types, plans);

    model.addAttribute("header", stats.getHeader());
    model.addAttribute("map", stats.getMap());
    model.addAttribute("checkout", stats.getCheckout());

    List<String> months = getShowMonths();
    model.addAttribute("months", months);
    model.addAttribute("thisMonth", MONTH_FORMAT.format(start));
    return "stats_accountbook";
  }

  private Date getThisMonth() {
    Calendar c = Calendar.getInstance();
    setStartOfMonth(c);
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

  private Date calcMonthEnd(Date start) {
    Calendar endCalendar = Calendar.getInstance();
    endCalendar.setTime(start);
    setStartOfMonth(endCalendar);
    endCalendar.add(Calendar.MONTH, 1);
    endCalendar.add(Calendar.DAY_OF_MONTH, -1);
    return endCalendar.getTime();
  }

  private List<String> getShowMonths() {
    Date oldestDate = getOldestDate();
    Date newestDate = getNewestDate();

    Calendar newest = Calendar.getInstance();
    newest.setTime(newestDate);

    Calendar c = Calendar.getInstance();
    c.setTime(oldestDate);
    setStartOfMonth(c);

    List<String> months = new ArrayList<>();
    while (c.compareTo(newest) <= 0) {
      String month = MONTH_FORMAT.format(c.getTime());
      months.add(month);
      c.add(Calendar.MONTH, 1);
    }
    return months;
  }

  private Date getOldestDate() {
    Date oldestDate = null;
    // decide oldest date from database record
    ABItem item = service.selectOldestItem();
    ABPlan plan = service.selectOldestPlan();
    if (item != null && plan != null) {
      Date itemDate = item.getDate();
      Date planDate = plan.getMonth();
      oldestDate = itemDate.before(planDate)
        ? itemDate
        : planDate;
    } else if (item != null) {
      oldestDate = item.getDate();
    } else if (plan != null) {
      oldestDate = plan.getMonth();
    }
    if (oldestDate == null) {
      // if no record found, oldest date is this month
      oldestDate = getThisMonth();
    }
    return oldestDate;
  }

  private Date getNewestDate() {
    Date newestDate = null;
    // decide newest date from database record
    ABItem item = service.selectNewestItem();
    ABPlan plan = service.selectNewestPlan();
    if (item != null && plan != null) {
      Date itemDate = item.getDate();
      Date planDate = plan.getMonth();
      newestDate = itemDate.after(planDate)
        ? itemDate
        : planDate;
    } else if (item != null) {
      newestDate = item.getDate();
    } else if (plan != null) {
      newestDate = plan.getMonth();
    }
    if (newestDate == null) {
      // if no record found, newest date is this month
      newestDate = getThisMonth();
    }
    return newestDate;
  }

  private void setStartOfMonth(Calendar c) {
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    c.clear();
    c.set(
        year,
        month,
        1, //dateOfMonth
        0, //hourOfDate
        0, //minute
        0  //second
      );
  }

}
