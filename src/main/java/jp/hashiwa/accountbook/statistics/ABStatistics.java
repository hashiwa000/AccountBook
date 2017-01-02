package jp.hashiwa.accountbook.statistics;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jp.hashiwa.accountbook.ABItem;
import jp.hashiwa.accountbook.ABPayer;
import jp.hashiwa.accountbook.ABType;

public class ABStatistics {

  private List<ABItem> items;
  private List<ABPayer> payers;
  private List<ABType> types;
  private List<String> header;
  private Map<String, Map<String, Long>> map;
  private Map<String, Long> checkout;

  public ABStatistics(
      List<ABItem> items,
      List<ABPayer> payers,
      List<ABType> types)
  {
    this.items = items;
    this.payers = payers;
    this.types = types;
    this.header = initHeader();
    this.map = initMap();
    this.checkout = initCheckout();
  }

  private List<String> initHeader() {
    return types.stream()
      .map(type -> type.getName())
      .collect(Collectors.toList());
  }

  private Map<String, Map<String, Long>> initMap() {
    Map<String, Map<String, Long>> map = new LinkedHashMap<>();

    // list up rows
    for (ABPayer payer: payers) {
      String payerName = payer.getName();
      Map<String, Long> rows = new LinkedHashMap<>();
      // list up headers
      for (ABType type: types) {
        String typeName = type.getName();
        rows.put(typeName, 0L);
      }
      rows.put("Total", 0L);
      map.put(payerName, rows);
    }

    // aggregate items
    for (ABItem item: items) {
      String payerName = item.getPayer().getName();
      long amount = item.getAmount();
      String type = item.getType().getName();
      Map<String, Long> columns = map.get(payerName);
      columns.put(type, columns.get(type) + amount);
    }

    // calcurate total amount for each header
    Map<String, Long> totals = new LinkedHashMap<>();
    for (ABType type: types) {
      String typeName = type.getName();
      long sum = map.entrySet().stream()
        .mapToLong(e -> e.getValue().get(typeName))
        .sum();
      totals.put(typeName, sum);
    }
    map.put("Total", totals);

    // calcurate total amount for each row
    for (String payer: map.keySet()) {
      Map<String, Long> row = map.get(payer);
      long sum = row.values().stream()
        .mapToLong(v -> (long)v)
        .sum();
      row.put("Total", sum);
    }

    return map;
  }

  private Map<String, Long> initCheckout() {
    //TODO: use stream
    Map<String, Long> checkout = new LinkedHashMap<>();
    int payerCount = payers.size();

    Map<String, Long> payed = new LinkedHashMap<>();
    for (ABPayer payer: payers) {
      String payerName = payer.getName();
      long sum = items.stream()
        .filter(item -> item.getType().isLivingExpense())
        .filter(item -> item.getPayer().getName().equals(payerName))
        .mapToLong(item -> item.getAmount())
        .sum();
      payed.put(payerName, sum);
    }

    long total = payed.entrySet().stream()
      .mapToLong(e -> e.getValue())
      .sum();
    long divided = total/payerCount;

    for (String payerName: payed.keySet()) {
      long v = payed.get(payerName);
      checkout.put(payerName, divided - v);
    }

    return checkout;
  }

  public List<String> getHeader() { return header; }
  public Map<String, Map<String, Long>> getMap() { return map; }
  public Map<String, Long> getCheckout() { return checkout; }
}

