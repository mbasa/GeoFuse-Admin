/**
 * パッケージ名：geotheme.geofuse_admin.utils
 * ファイル名  ：ParseCsv.java
 * 
 * @author mbasa
 * @since May 19, 2017
 */
package geotheme.geofuse_admin.utils;

/**
 * 説明：
 *
 */
import java.io.StringReader;

import org.apache.commons.csv.*;

public class ParseCsv {

    public static String[][] parse(String instr) {
        
        String outstr[][] = null;
        
        try {

            CSVStrategy tab   = new CSVStrategy('\t','"','#');
            
            StringReader sr = new StringReader(instr);
            CSVParser cvsp  = new CSVParser(sr,tab);
            outstr          = cvsp.getAllValues();
            
            if( outstr[0].length < 2 ){
                outstr = CSVUtils.parse(instr);
            }           
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        
        if( outstr != null && outstr.length < 2 || outstr[0].length < 2 ){
            return null;
        }
        
        return outstr;
    }
}
