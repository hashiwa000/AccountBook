import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.sql.*;

public class DB {
  public static void main(String[] args) throws Exception {
    if (args.length != 1 || "".equals(args[0].trim())) {
      System.err.println("Error: <SQL>");
      System.exit(1);
    }
    String sql = args[0].trim();
    String url = readDatabaseUrl();
    Connection conn = null;
    try {
      conn = DriverManager.getConnection(url);
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

  private static String readDatabaseUrl() {
    try {
      URL classUrl = DB.class.getResource("DB.class");
      File rootDir = new File(classUrl.toURI())
        .getParentFile()
        .getParentFile();
      File confFile = new File(rootDir, "src/main/resources/config/application.yml");
      if (! confFile.isFile()) {
        throw new Exception("config file is not found: " + confFile);
      }

      String url=null, user=null, password=null;
      try (BufferedReader reader = new BufferedReader(new FileReader(confFile)))
      {
        String line;
        while ((line=reader.readLine()) != null) {
          line = line.trim();
          if (line.startsWith("url:")) {
            url = line.substring("url:".length()).trim();
          }
          if (line.startsWith("username:")) {
            user = line.substring("username:".length()).trim();
          }
          if (line.startsWith("password:")) {
            password = line.substring("password:".length()).trim();
          }
        }
      }
      if (url == null) {
        throw new Exception("url is not defined.");
      }
      if (user != null) {
        url += ";user=" + user;
      }
      if (password != null) {
        url += ";password=" + password;
      }
      return url;
    } catch(Exception e) {
      System.err.println("Database url is not found: " + e);
      System.exit(2);
    }
    // not reach here
    return null;
  }
}
