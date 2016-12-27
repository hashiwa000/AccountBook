import java.sql.*;

public class DB {
  public static void main(String[] args) throws Exception {
    if (args.length != 1 || "".equals(args[0].trim())) {
      System.err.println("Error: <SQL>");
      System.exit(1);
    }
    String sql = args[0];
    Connection conn = null;
    try {
      String url = "jdbc:derby:/home/hashiwa/java/AccountBook/db_production;create=true;user=test;password=test";
      conn = DriverManager.getConnection(url);
      Statement stat = conn.createStatement();
      ResultSet result = stat.executeQuery(sql);
      //ResultSet result = stat.executeQuery("select t.tablename, t.tabletype, s.schemaname from sys.systables as t join sys.sysschemas as s on t.schemaid = s.schemaid");
      while (result.next()) {
        System.out.println(createRowString(result));
      }
    } finally {
      if (conn != null) conn.close();
    }
  }

  private static String createRowString(ResultSet result) {
    StringBuilder buf = new StringBuilder();
    for (int i=1 ;; i++) {
      try {
        String s = result.getString(i);
        buf.append(s);
        buf.append('\t');
      } catch(SQLException e) {
        break;
      }
    }
    return buf.toString();
  }
}
