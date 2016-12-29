package jp.hashiwa.accountbook.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.hashiwa.accountbook.ABItem;
import jp.hashiwa.accountbook.ABPayer;
import jp.hashiwa.accountbook.ABType;

public class ABStatistics {
  private List<ABItem> items;
  private List<ABPayer> payers;
  private List<ABType> types;
  private List<String> header = new ArrayList<>();
  private Map<String, Map<String, Long>> map = new LinkedHashMap<>();

  public ABStatistics(
      List<ABItem> items,
      List<ABPayer> payers,
      List<ABType> types)
  {
    this.items = items;
    this.payers = payers;
    this.types = types;
    initMap();
  }

  private void initMap() {
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
      String payerName = item.getName();
      long amount = item.getAmount();
      String type = item.getType();
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
  }

  public List<String> getHeader() { return header; }
  public Map<String, Map<String, Long>> getMap() { return map; }
}

