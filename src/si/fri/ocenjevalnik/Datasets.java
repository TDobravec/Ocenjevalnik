package si.fri.ocenjevalnik;


import java.io.File;
import java.util.ArrayList;

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
  
  public void writeToFile(File file) {
    
  } 
}
