package si.fri.ocenjevalnik;


import java.util.Comparator;

/**
 *
 * @author tomaz
 */
public class Dataset {
  String name;     // the name of this dataset
  String args;     // arguments passed to the program
  String stdInput; // the value of the stdin that is redirected to the program

  public Dataset(String name) {
    this.name = name;
    args = "";
    stdInput = "";
  }

  
  
  @Override
  public String toString() {
    return name;
  }
  
  
  public void getJSON() {
    
  }
}
