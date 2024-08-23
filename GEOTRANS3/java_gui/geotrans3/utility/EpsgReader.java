// CLASSIFICATION: UNCLASSIFIED

/********************************************************************************
* Filename: EpsgReader.java
*
* Copyright BAE Systems Inc. 2020 ALL RIGHTS RESERVED
*
* MODIFICATION HISTORY
*
* DATE      NAME        DR#          DESCRIPTION
*
* 04/06/20  E. Poon     GTR-53       Reads a csv file containing EPSG codes
********************************************************************************/

package geotrans3.utility;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.String;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 *  Reads in a csv file that stores EPSG data. The EpsgReader parses the data so that 
 *  the EPSG code is stored as the key of the outer map and value is another map which 
 *  contains all the associated attributes (i.e datum, coordinate system, etc.)
 *
 */
public class EpsgReader {
    
    static public Map<String, Map<String, String>> readFile(final String path, final List<String> warnings) {
        final Map<String, Map<String, String>> codes = new HashMap<>();
        final Map<String, String> codeValuesTemp = new HashMap<>();
        
        BufferedReader csvReader = null;
        
        try {
            csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
        } catch (final FileNotFoundException e) {
            final String error = "ERROR reading EPSG file: " + path + " not found: " + e.getMessage();
            System.err.println(error);
            throw new RuntimeException(error, e);
        } catch (final UnsupportedEncodingException e) {
            final String error = "ERROR reading EPSG file " + path + ": Unsupported encoding: " + e.getMessage();
            System.err.println(error);
            throw new RuntimeException(error, e);
        }
        
        try {
            String line;
            int lineNum = 0;
            
            while ((line = csvReader.readLine()) != null) {
                lineNum++;
                
                // Skip blank lines
                if (line.isEmpty()) {
                    final String warning = "WARNING: Line " + lineNum + ": Skipping blank line";
                    //System.err.println(warning);
                    warnings.add(warning);
                    continue;
                }
                
                // Skip lines not containing delimiters
                if (line.contains(",") == false) {
                    final String warning = "WARNING: Line " + lineNum + ": No delimiter found. Line: '" + line + "'";
                    //System.err.println(warning);
                    warnings.add(warning);
                    continue;
                }
                
                final String lineVals[] = line.split(",");
                if (lineVals[0].equals("Authority")) 
                    continue; 
                
                if (!(lineVals[0].equals("EPSG"))) {
                    final String warning = "WARNING: Line " + lineNum + ": Invalid EPSG data row '" + line + "'";
                    //System.err.println(warning);
                    warnings.add(warning);
                    continue;
                }
                    
                final Map<String, String> codeValues = new HashMap<>();
                final String code = lineVals[1];
                
                
                for (int x = 2; x < lineVals.length; x += 2) {
                    codeValues.put(lineVals[x], lineVals[x + 1]);
                }
                
                codes.put(code, codeValues);
            }
        } catch (final IOException e) {
            final String error = "ERROR reading EPSG file " + path + ": IOException: " + e.getMessage();
            System.err.println(error);
            throw new RuntimeException(error, e);
        } finally {
            try {
                csvReader.close();
            } catch (final IOException e) {
                System.err.println("ERROR: Unable to properly close the EPSG CSV file '" + path + "': " + e.getMessage());
            }
        }
        
        return codes;
    }
}

// CLASSIFICATION: UNCLASSIFIED
