/**
 * パッケージ名：geotheme.geofuse_admin.db
 * ファイル名  ：ConnectionPoolHolder.java
 * 
 * @author mbasa
 * @since May 18, 2017
 */
package geotheme.geofuse_admin.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;

/**
 * 説明：
 *
 */
public class ConnectionPoolHolder {

    private static SimpleJDBCConnectionPool connectionPool = null;
    
    /**
     * コンストラクタ
     *
     */
    public ConnectionPoolHolder() {
    }

    public static SimpleJDBCConnectionPool getConnectionPool() {
        
        if( connectionPool == null ) {
            String hostname = "localhost";
            String dbname   = "geofuse";
            String user     = "postgres";
            String password = "";
            
            int init_connections = 2;
            int max_connections  = 5;
            
            try {
                ResourceBundle rdb = 
                        ResourceBundle.getBundle("properties.database");
                
                dbname    = rdb.getString("DB.NAME");
                hostname  = rdb.getString("DB.HOST");
                user      = rdb.getString("DB.USER");
                password  = rdb.getString("DB.PASSWORD");   
                
                init_connections = Integer.parseInt(
                        rdb.getString("DB.INITIAL_CONNECTIONS"));
                
                max_connections  = Integer.parseInt(
                        rdb.getString("DB.MAX_CONNECTIONS"));
                
                connectionPool = new SimpleJDBCConnectionPool(
                        "org.postgresql.Driver", 
                        "jdbc:postgresql://"+hostname+"/"+dbname, 
                        user,password,
                        init_connections,max_connections);
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return connectionPool;
    }
    
    public static Connection getConnection() {
        Connection conn = null;
        
        try {
            conn =  getConnectionPool().reserveConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return conn;
    }
    
    public static void returnConnection( Connection conn ) {
        if( conn != null ) {
            getConnectionPool().releaseConnection(conn);
        }
    }

}
