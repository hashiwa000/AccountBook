package jp.hashiwa.accountbook;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import jp.hashiwa.accountbook.statistics.ABStatistics;

@Controller
@RequestMapping("/")
public class ABController {

  private static final DateFormat MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");
  private static final DateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
  private static final List<DateFormat> DATE_FORMATS = Arrays.<DateFormat>asList(
      new SimpleDateFormat("yyyy-MM-dd"),
      new SimpleDateFormat("yyyy/MM/dd")
    );
  private static final List<DateFormat> MONTH_FORMATS = Arrays.<DateFormat>asList(
      MONTH_FORMAT,
      new SimpleDateFormat("yyyy/MM")
    );
  private static final List<DateFormat> YEAR_FORMATS = Arrays.<DateFormat>asList(
      YEAR_FORMAT
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
      if (start == null) {
        //fall back to this month
        start = getThisMonth();
      }
    }
    Date end = calcMonthEnd(start);
    List<ABPlan> plans = service.selectAllPlans(start, end);
    if (plans == null) {
      plans = Arrays.<ABPlan>asList();
    }
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
      if (start == null) {
        //fall back to this month
        start = getThisMonth();
      }
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
    return "update_plan_accountbook";
  }

  @RequestMapping(value="/plan/create", method=RequestMethod.POST)
  public String createPlan(
      @RequestParam String month,
      @RequestParam(defaultValue="false") boolean all_months,
      HttpServletRequest request,
      Model model) throws Exception
  {
    Map map = request.getParameterMap();
    Date monthDate = parseMonthStr(month);
    if (monthDate == null) {
      throw new Exception("Illegal month: " + monthDate); // exception
    }

    // list one or all months
    List<Date> months;
    if (all_months) {
      Calendar c = Calendar.getInstance();
      c.setTime(monthDate);
      setStartOfYear(c);
      final Date baseMonth = c.getTime();
      months = IntStream
        .range(0, 12)
        .mapToObj(i -> {
          Calendar tmpc = Calendar.getInstance();
          tmpc.setTime(baseMonth);
          tmpc.add(Calendar.MONTH, i);
          return tmpc.getTime();
        })
        .collect(Collectors.toList());
    } else {
      months = Arrays.<Date>asList(monthDate);
    }

    // update plans
    List<ABType> types = service.selectAllTypes();
    for (Date targetMonth: months) {
      for (ABType type: types) {
        long id = type.getId();
        String key = "type_" + id;
        String value = ((String[])map.get(key))[0]; //XXX:
        long amount = Long.parseLong(value);
        ABPlan plan = service.selectOnePlan(targetMonth, type);
        if (plan == null) {
          plan = new ABPlan(targetMonth, type, amount);
          service.saveAndFlush(plan);
        } else if (plan.getAmount() != amount) {
          plan.setAmount(amount);
          service.saveAndFlush(plan);
        }
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
      if (start == null) {
        //fall back to this month
        start = getThisMonth();
      }
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
      @RequestParam(defaultValue = "-1") long idToUpdate,
      @RequestParam(defaultValue = "-1") long idToDelete,
      Model model) throws Exception
  {
    if (0 <= idToDelete) {
      service.delete(idToDelete);
    }
    if (0 <= idToUpdate) {
      return createAccountBook(idToUpdate, model);
    }
    return showAccountBook(month, model);
  }

  @RequestMapping(value="/accountbook/create", method=RequestMethod.GET)
  public String createAccountBook(
      @RequestParam(defaultValue = "-1") long idToUpdate,
      Model model)
  {
    List<ABPayer> payers = service.selectAllPayers();
    List<ABType> types = service.selectAllTypes();
    model.addAttribute("payers", payers);
    model.addAttribute("types", types);
    if (0 <= idToUpdate) {
      ABItem item = service.select(idToUpdate);
      if (item != null) {
        model.addAttribute("item", item);
      }
    }
    return "create_accountbook";
  }

  @RequestMapping(value="/accountbook/create", method=RequestMethod.POST)
  public String createAccountBook(
      @RequestParam(defaultValue = "-1") long id,
      @RequestParam("date") String date,
      @RequestParam("amount") long amount,
      @RequestParam("name") String name,
      @RequestParam("type") String type,
      @RequestParam("desc") String desc,
      @RequestParam("remarks") String remarks,
      Model model) throws Exception
  {
    Date d = parseDateStr(date);
    if (d == null) {
      throw new Exception("Date is not found: " + date); // TODO: exception
    }
    ABPayer payer = service.selectOnePayer(name);
    if (payer == null) {
      throw new Exception("Payer is not found: " + name); // TODO: exception
    }
    ABType t = service.selectOneType(type);
    if (t == null) {
      throw new Exception("Type is not found: " + type); // TODO: exception
    }
    ABItem item = null;
    if (0 <= id) {
      item = service.select(id);
    }
    if (item != null) {
      // update item
      item.setDate(d);
      item.setAmount(amount);
      item.setPayer(payer);
      item.setType(t);
      item.setDescription(desc);
      item.setRemarks(remarks);
    } else {
      // create new item
      item = new ABItem(d, amount, payer, t, desc, remarks);
    }
    service.saveAndFlush(item);

    List<ABItem> items = Arrays.<ABItem>asList(item);
    model.addAttribute("created", items);
    return "created_accountbook";
  }

  @RequestMapping(value="/accountbook/stats", method=RequestMethod.GET)
  public String statsAccountBook(
      @RequestParam(defaultValue="") String period,
      Model model) throws Exception
  {
    Object[] results;
    if ((results=handleAsMonth(period)) != null) {}
    else if ((results=handleAsYear(period)) != null) {}
    else if ((results=handleAsNoPeriod()) != null) {}
    else {
      //should not reach here
      throw new Error("Fatal Error");
    }

    //TODO: can improve
    Date start = (Date)results[0];
    Date end = (Date)results[1];
    String thisPeriod = (String)results[2];

    List<ABItem> items = service.selectAll(start, end);
    List<ABPlan> plans = service.selectAllPlans(start, end);
    List<ABPayer> payers = service.selectAllPayers();
    List<ABType> types = service.selectAllTypes();
    ABStatistics stats = new ABStatistics(items, payers, types, plans);

    model.addAttribute("header", stats.getHeader());
    model.addAttribute("map", stats.getMap());
    model.addAttribute("checkout", stats.getCheckout());

    List<String> periods = new ArrayList<>();
    periods.addAll(getShowYears());
    periods.addAll(getShowMonths());
    model.addAttribute("periods", periods);
    model.addAttribute("thisPeriod", thisPeriod);
    return "stats_accountbook";
  }

  public Object[] handleAsMonth(String period) {
    Date start = parseMonthStr(period);
    if (start == null) {
      // period is not month string.
      return null;
    }
    Date end = calcMonthEnd(start);
    String thisMonth = MONTH_FORMAT.format(start);
    return new Object[] {start, end, thisMonth};
  }

  public Object[] handleAsYear(String period) {
    Date start = parseYearStr(period);
    if (start == null) {
      // period is not year string.
      return null;
    }
    Date end = calcYearEnd(start);
    String thisYear = YEAR_FORMAT.format(start);
    return new Object[] {start, end, thisYear};
  }

  public Object[] handleAsNoPeriod() {
    Date start = getThisMonth();
    Date end = calcMonthEnd(start);
    String thisMonth = MONTH_FORMAT.format(start);
    return new Object[] {start, end, thisMonth};
  }

  private Date getThisMonth() {
    Calendar c = Calendar.getInstance();
    setStartOfMonth(c);
    return c.getTime();
  }

  private Date getThisYear() {
    Calendar c = Calendar.getInstance();
    setStartOfYear(c);
    return c.getTime();
  }

  private Date parseDateStr(String s) {
    return parseStr(s, DATE_FORMATS);
  }

  private Date parseMonthStr(String s) {
    return parseStr(s, MONTH_FORMATS);
  }

  private Date parseYearStr(String s) {
    return parseStr(s, YEAR_FORMATS);
  }

  private Date parseStr(String s, List<DateFormat> formats) {
    if (s == null) {
      return null;
    }
    for (DateFormat format: formats) {
      try {
        return format.parse(s);
      } catch(ParseException e) {}
    }
    return null;
  }

  private Date calcYearEnd(Date start) {
    Calendar endCalendar = Calendar.getInstance();
    endCalendar.setTime(start);
    setStartOfYear(endCalendar);
    endCalendar.add(Calendar.YEAR, 1);
    endCalendar.add(Calendar.DAY_OF_MONTH, -1);
    return endCalendar.getTime();
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

  private List<String> getShowYears() {
    Date oldestDate = getOldestDate();
    Date newestDate = getNewestDate();

    Calendar newest = Calendar.getInstance();
    newest.setTime(newestDate);

    Calendar c = Calendar.getInstance();
    c.setTime(oldestDate);
    setStartOfYear(c);

    List<String> years = new ArrayList<>();
    while (c.compareTo(newest) <= 0) {
      String year = YEAR_FORMAT.format(c.getTime());
      years.add(year);
      c.add(Calendar.YEAR, 1);
    }
    return years;
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

  private void setStartOfYear(Calendar c) {
    setStartOfMonth(c, 0);
  }

  private void setStartOfMonth(Calendar c) {
    int month = c.get(Calendar.MONTH);
    setStartOfMonth(c, month);
  }

  private void setStartOfMonth(Calendar c, int month) {
    int year = c.get(Calendar.YEAR);
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
