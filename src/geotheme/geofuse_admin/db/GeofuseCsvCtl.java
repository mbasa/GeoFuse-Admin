/**
 * パッケージ名：geotheme.geofuse_admin.db
 * ファイル名  ：GeofuseCsvCtl.java
 * 
 * @author mbasa
 * @since May 19, 2017
 */
package geotheme.geofuse_admin.db;

import geotheme.geofuse_admin.utils.ParseCsv;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 説明：
 *
 */
public class GeofuseCsvCtl {

    /**
     * コンストラクタ
     *
     */
    public GeofuseCsvCtl() {
    }

    public static int deleteMarkerLayer(int layerId, String tableName ) {
        int retval = -1;
    
        String sql = new String();
        
        sql = "drop table "+tableName;
        
        retval = update(sql);
        
        sql = "delete from geofuse.markerlayer where id = ?";
        
        retval = update( sql,layerId );
        
        return retval;
    }
    
    public static String processMarkerCsv( String inStr ) {
        
        String tableName = null;        
        String pStr[][]  = ParseCsv.parse(inStr);
        
        if(pStr == null ) {
            return ("Error: Parsing CSV");
        }
        
        String cols[]  = pStr[0];
        boolean hasLon = false;
        boolean hasLat = false;

        int i;
        
        for(i=0;i<cols.length;i++) {
            if(cols[i].equalsIgnoreCase("lon"))
                hasLon = true;
            if(cols[i].equalsIgnoreCase("lat"))
                hasLat = true;
        }
        
        if( !hasLon || !hasLat ) {
            return("Error: No Lon/Lat columns");
        }
                
        Random rand = new Random();
        
        tableName = "markerlayers." + 
                "mb_"+(new Date()).getTime()+ "_" + 
                rand.nextInt(10000);
        
        StringBuffer sb = new StringBuffer();
        sb.append("create table ").append( tableName ).append(" (");
        
        for(i=0;i<cols.length-1;i++) {
            sb.append(cols[i]).append(" text,");
        }        
        sb.append(cols[i]).append(" text)");
        
        update( sb.toString() );
        
        sb = new StringBuffer();
        sb.append("insert into ").append(tableName).append(" values(");
        
        for(i=0;i<cols.length-1;i++) {
            sb.append("?,");
        }
        sb.append("?)");
        
        String arr[][] = Arrays.copyOfRange(pStr, 1, pStr.length);
        update(sb.toString(),arr);
        
        return tableName;
    }

    public static int[] update( String sql, Object[][] params) {
        Logger LOGGER = LogManager.getLogger();
        LOGGER.debug("In update:{}",sql);

        Connection con = null;
        int[] retval   = null;
        
        try {
            con = ConnectionPoolHolder.getConnection();
            con.setAutoCommit(false);
            
            QueryRunner runner = new QueryRunner();
            retval = runner.batch(con,sql,params);
            con.commit();
        }
        catch( Exception e ) {
            LOGGER.error( e );
        }
        finally {
            if( con != null ) {
                ConnectionPoolHolder.returnConnection(con);
            }
        }
        return retval;

    }
    
    public static int update( String sql, Object... params ) {
        Logger LOGGER = LogManager.getLogger();
        LOGGER.debug("In update:{}",sql);
        
        Connection con = null;
        int retval     = -1;
        
        try {
            con = ConnectionPoolHolder.getConnection();
            con.setAutoCommit(false);
            
            QueryRunner runner = new QueryRunner();
            retval = runner.update(con,sql,params);
            con.commit();
        }
        catch( Exception e ) {
            LOGGER.error( e );
        }
        finally {
            if( con != null ) {
                ConnectionPoolHolder.returnConnection(con);
            }
        }
        return retval;
    }

}
