package si.fri.ocenjevalnik;


import java.util.Comparator;
import org.json.JSONObject;

/**
 *
 * @author tomaz
 */
public class Dataset {
  private final String ID_name  = "Name";
  private final String ID_args  = "Args";
  private final String ID_stdin = "Stdin";
  
  
  String name;     // the name of this dataset
  String args;     // arguments passed to the program
  String stdInput; // the value of the stdin that is redirected to the program

  public Dataset() {
    this.name = "";
    this.args = "";
    this.stdInput = "";
  }
  
  public Dataset(String name) {
    this.name = name;
    args = "";
    stdInput = "";
  }

  public Dataset(JSONObject json) {
    this();
    
    if (json == null) return;
      
    this.name     = json.optString(ID_name);
    this.args     = json.optString(ID_args);
    this.stdInput = json.optString(ID_stdin);
  }
  
  
  @Override
  public String toString() {
    return name;
  }
  
  public JSONObject getJSON() {
    JSONObject json = new JSONObject();
    json.putOpt(ID_name,  name);
    json.putOpt(ID_args,  args);
    json.putOpt(ID_stdin, stdInput);
    
    return json;
  }
}
