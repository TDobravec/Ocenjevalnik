package si.fri.ocenjevalnik;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author tomaz
 */
public class Datasets {

  ArrayList<Dataset> datasets;

  public Datasets() {
    datasets = new ArrayList<Dataset>();
  }
  
  public ArrayList<Dataset> getData() {
    return datasets;
  }
  
  public void remove(Dataset ds) {
    datasets.remove(ds);
  }
  
  public void add(Dataset ds) {
    datasets.add(ds);
  }
  
  public String writeToFile(File file) {
    JSONArray jarray = new JSONArray();
    if (datasets != null) for(Dataset ds: datasets) {
      jarray.put(ds.getJSON());
    }
    
    PrintWriter pw;
    try {
      pw = new PrintWriter(file);
      pw.print(jarray.toString());
      pw.close();
      
      return "OK";
    } catch (FileNotFoundException ex) {
      return "Napaka pri pisanju: " + ex.toString();
    }
  } 
  
  public void readFromFile(File f) {
    datasets = new ArrayList<Dataset>();
  
    String cont = "";
    try {
      Scanner sc = new Scanner(f);
      while (sc.hasNextLine())
	cont += sc.nextLine();
      sc.close();
      
      JSONArray jArray = new JSONArray(cont);
      for(int i=0; i<jArray.length(); i++) {
         JSONObject obj = jArray.getJSONObject(i);
	 Dataset ds = new Dataset(obj);
	 if (ds != null)
	   datasets.add(ds);
      }
      
    } catch (FileNotFoundException ex) {
      cont = "";
    }
    
    
  }
}
