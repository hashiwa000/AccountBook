package jp.hashiwa.accountbook.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.hashiwa.accountbook.ABItem;
import jp.hashiwa.accountbook.ABPayer;
import jp.hashiwa.accountbook.ABType;

public class ABStatistics {
  private static final List<String> IGNORE_TYPES = Arrays.asList("その他");

  private List<ABItem> items;
  private List<ABPayer> payers;
  private List<ABType> types;
  private List<String> header = new ArrayList<>();
  private Map<String, Map<String, Long>> map = new LinkedHashMap<>();
  private Map<String, Long> checkout = new LinkedHashMap<>();

  public ABStatistics(
      List<ABItem> items,
      List<ABPayer> payers,
      List<ABType> types)
  {
    this.items = items;
    this.payers = payers;
    this.types = types;
    this.map = initMap();
    this.checkout = initCheckout();
  }

  private Map<String, Map<String, Long>> initMap() {
    Map<String, Map<String, Long>> map = new LinkedHashMap<>();

    //TODO: use stream
    for (ABType type: types) {
      String typeName = type.getName();
      header.add(typeName);
    }

    for (ABPayer payer: payers) {
      String payerName = payer.getName();
      Map<String, Long> columns = new LinkedHashMap<>();
      for (ABType type: types) {
        String typeName = type.getName();
        columns.put(typeName, 0L);
      }
      columns.put("Total", 0L);
      map.put(payerName, columns);
    }

    for (ABItem item: items) {
      String payerName = item.getPayer().getName();
      long amount = item.getAmount();
      String type = item.getType().getName();
      Map<String, Long> columns = map.get(payerName);
      columns.put(type, columns.get(type) + amount);
    }

    Map<String, Long> totals = new LinkedHashMap<>();
    for (ABType type: types) {
      String typeName = type.getName();
      long sum = map.entrySet().stream()
        .mapToLong(e -> e.getValue().get(typeName))
        .sum();
      totals.put(typeName, sum);
    }
    map.put("Total", totals);

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
        .filter(item -> !IGNORE_TYPES.contains(item.getType().getName()))
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

