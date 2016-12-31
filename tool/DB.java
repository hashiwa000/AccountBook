import java.sql.*;

public class DB {
  private static final  String URL = "jdbc:derby:/home/hashiwa/java/AccountBook/db_production;create=true;user=test;password=test";
  public static void main(String[] args) throws Exception {
    if (args.length != 1 || "".equals(args[0].trim())) {
      System.err.println("Error: <SQL>");
      System.exit(1);
    }
    String sql = args[0].trim();
    Connection conn = null;
    try {
      conn = DriverManager.getConnection(URL);
      Statement stat = conn.createStatement();
      if (isSelectStatement(sql)) {
        doSelect(stat, sql);
      } else {
        doUpdate(stat, sql);
      }
    } finally {
      if (conn != null) conn.close();
    }
  }

  private static boolean isSelectStatement(String sql) {
    String type = sql.split(" ")[0].toLowerCase();
    return "select".equals(type);
  }

  private static void doSelect(Statement stat, String sql) throws Exception {
    ResultSet result = stat.executeQuery(sql);
    //ResultSet result = stat.executeQuery("select t.tablename, t.tabletype, s.schemaname from sys.systables as t join sys.sysschemas as s on t.schemaid = s.schemaid");
    while (result.next()) {
      System.out.println(createRowString(result));
    }
  }

  private static void doUpdate(Statement stat, String sql) throws Exception {
    int rowCount = stat.executeUpdate(sql);
    System.out.println("rows = " + rowCount);
  }

  private static String createRowString(ResultSet result) throws SQLException {
    ResultSetMetaData metadata = result.getMetaData();
    int columnCount = metadata.getColumnCount();
    StringBuilder buf = new StringBuilder();
    for (int i=1 ; i<=columnCount ; i++) {
      String s = result.getString(i);
      buf.append(s);
      buf.append('\t');
    }
    return buf.toString().trim();
  }
}
