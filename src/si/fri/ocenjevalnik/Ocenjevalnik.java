package si.fri.ocenjevalnik;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import jsyntaxpane.DefaultSyntaxKit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author tomaz
 */
public class Ocenjevalnik extends javax.swing.JFrame {

  static final String tmpDirSuffix = "tmp";
  static final String resDir = "viri";
  static final String datasetFilename = "_datasets";
  private final static String stars = "************************************************\n";
  Datasets datasets; // datasets for this project
  static final String oceneFileName = "_ocene";
  static final String locilo = "\t";
  static final String newline = "<br>";
  static final String glavaCSV = "Vpisna" + locilo + "Ocena" + locilo + "Komentar";
  File currentDir;
  String trenutnaDatoteka = null;
  String vpisnaStevilka = null;
  HashMap<String, Student> studenti;
  RunResults rr;
  
  String vpisnaStevilkaRegex="[_=],2";  

  public Ocenjevalnik() {
    initComponents();

    datasets = new Datasets();

    studenti = new HashMap<String, Student>();

    jSplitPane2.setDividerLocation(600);

    DefaultSyntaxKit.initKit();
    kodaTP.setContentType("text/java");
    
    vpisnaStevilkaRegex = Preferences.userNodeForPackage(this.getClass()).get("Ocenjevalnik.regex", vpisnaStevilkaRegex);

    iskaniNizTF.getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
	poisciTekstInPostaviKurzor();
      }

      public void removeUpdate(DocumentEvent e) {
	poisciTekstInPostaviKurzor();
      }

      public void changedUpdate(DocumentEvent e) {
	poisciTekstInPostaviKurzor();
      }
    });

    rr = new RunResults(this, false);
  }

  void naloziDatoteke() {
    ArrayList<String> naloge = new ArrayList();
  
    String datoteke[] = currentDir.list();
    for (String ime : datoteke) {
      if (!(ime.startsWith(".") || ime.startsWith("_"))) {
	naloge.add(ime);
      }
    }
    Collections.sort(naloge, new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {        
        return o1.compareToIgnoreCase(o2);
      }      
    }
    );
    
    JList tmpList = new JList(new Vector(naloge));
    nalogeList.setModel(tmpList.getModel());
  }

  void preberiOcene() {
    File oceneFile = new File(currentDir, oceneFileName);
    studenti = new HashMap<String, Student>();
    int prebrani = 0;
    if (oceneFile.exists()) {
      try {
	setStatus("Berem datoteko z ocenami.");
	Scanner sc = new Scanner(oceneFile, "UTF-8");
	while (sc.hasNextLine()) {
	  String vrstica = sc.nextLine();
	  if (vrstica.equals(glavaCSV)) {
	    continue;
	  }

	  String polja[] = vrstica.split(locilo);
	  if (polja.length < 2) {
	    addStatus("Napaka: " + vrstica);
	  } else {
	    prebrani++;
	    Student s = new Student(polja[0]);
	    s.ocena = polja[1];
            String komentar = "";
            for (int i=2; i<polja.length; i++)
              komentar+=polja[i] + " "; 
	    s.komentar = komentar;
	    studenti.put(polja[0], s);
	  }
	}
	sc.close();
	addStatus(String.format("Prebranih: %d ocen", prebrani));
      } catch (Exception e) {
	setStatus("Napak pri branju datoteke z ocenami: " + e.toString());
      }
    } else {
      ustvariNovoTabeloZOcenami();
      shraniOcene();
    }
  }

  void preberiDatasets() {
    File f = new File(currentDir, datasetFilename);
    datasets.readFromFile(f);

    fillCombo();
  }

  void shraniDatasets() {
    File f = new File(currentDir, datasetFilename);
    datasets.writeToFile(f);
  }

  private void fillCombo() {
    DefaultComboBoxModel dcm = new DefaultComboBoxModel(datasets.getData().toArray());
    jComboBox1.setModel(dcm);

    if (dcm.getSize() > 0) {
      jComboBox1.setSelectedIndex(0);
    }
    prenesiPodatkeIzComba();
  }

  void prenesiPodatkeIzComba() {
    Dataset ds = (Dataset) jComboBox1.getSelectedItem();
    if (ds != null) {
      agrsTF.setText(ds.args);
      stdinTA.setText(ds.stdInput);
    }
  }

  void ustvariNovoTabeloZOcenami() {
    studenti = new HashMap<String, Student>();

    setStatus("Ustvarjam tabelo z ocenami.");

    int stNum = 0;
    String imenaDatotek[] = currentDir.list();
    for (String imeDatoteke : imenaDatotek) {
      if (imeDatoteke.startsWith(".") || imeDatoteke.startsWith("_")) {
	continue;
      }

      String vpisna = getVpisnaStevilka(imeDatoteke);

      if (vpisna != null) {
	stNum++;
	Student s = new Student(vpisna);
	studenti.put(vpisna, s);
      } else {
	addStatus("Napaka: " + imeDatoteke);
      }
    }
    addStatus(String.format("Dodanih je bilo %d studentov.", stNum));
  }

  static String brezTab(String besedilo) {
    return besedilo.replaceAll("\t", " ");
  }
  
  void shraniOcene() {
    File oceneFile = new File(currentDir, oceneFileName);
    try {
      PrintWriter pw = new PrintWriter(oceneFile, "UTF-8");
      pw.println(glavaCSV);

      for (Student s : studenti.values()) {
	pw.printf("%s%s%s%s%s\n", s.vpisna, locilo, s.ocena, locilo, brezTab(s.komentar));
      }
      pw.close();
    } catch (Exception e) {
      addStatus("Napaka pri shranjevanju: " + e.toString());
    }
  }

  void shraniRootDir(String fileName) {
    // direktorij, kjer se je nahajal zadnji projekt shrani, da ga bo drugic ponudil kot default
    int i = fileName.lastIndexOf(File.separator);
    if (i != -1) {
      try {
	Preferences pref = Preferences.userRoot();
	pref.put("Path", fileName.substring(0, i));
      } catch (Exception e) {
      }
    }
  }

  String getLastRootDir() {
    try {
      Preferences pref = Preferences.userRoot();
      String dir = pref.get("Path", "");
      return dir;
    } catch (Exception e) {
      return "";
    }
  }

  void odpriProjekt() {
    JFileChooser jfc = new JFileChooser();
    jfc.setCurrentDirectory(new File(getLastRootDir()));

    jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    int rez = jfc.showOpenDialog(this);
    if (rez == JFileChooser.APPROVE_OPTION) {
      currentDir = jfc.getSelectedFile();

      shraniRootDir(currentDir.getPath());

      naloziDatoteke();
      preberiOcene();
      preberiDatasets();
    }
  }

  void shraniStudenta() {
    if (vpisnaStevilka != null) {
      Student s = studenti.get(vpisnaStevilka);
      if (s != null) {
	s.ocena = ocenaTF.getText();
	s.komentar = komentarTA.getText().replaceAll("\n", newline);
      }
    }
    shraniOcene();
  }

  void prikaziOcenoTrenutnegaStudenta() {
    Student s = studenti.get(vpisnaStevilka);

    if (s != null) {
      vpisnaTF.setText(vpisnaStevilka);
      komentarTA.setText(s.komentar.replaceAll(newline, "\n"));
      ocenaTF.setText(s.ocena);
    }
  }

  void spremembaIzbire() {
    if (trenutnaDatoteka != null) {
      shraniStudenta();
    }

    sprazniVsaPolja();

    trenutnaDatoteka = (String) nalogeList.getSelectedValue();
    vpisnaStevilka = getVpisnaStevilka(trenutnaDatoteka);

    preberiVsebnoDatoteke();

    if (vpisnaStevilka == null) {
      setStatus("Ni vpisne �tevilke");
      return;
    } else {
      prikaziOcenoTrenutnegaStudenta();
    }
  }

  String getVpisnaStevilka(String fName) {
    String[] vpisnaRegexPart = vpisnaStevilkaRegex.split(",");
    if (vpisnaRegexPart.length != 2) return fName;
    String regex = vpisnaRegexPart[0];
    int polje = 0;
    try {
      polje = Integer.parseInt(vpisnaRegexPart[1]);
    }  catch (Exception e) {}
    
    String polja[] = fName.split(regex);
    if (polja.length >= polje) {
      // System.out.println(polja[1]);
      return polja[polje];
    } else {
      return fName;
    }

  }

  void setStatus(String msg) {
    statusTA.setText(msg);
  }

  void addStatus(String msg) {
    statusTA.setText(statusTA.getText() + "\n" + msg);
  }

  void sprazniVsaPolja() {
    vpisnaTF.setText("");
    kodaTP.setText("");
    ocenaTF.setText("");
    komentarTA.setText("");

    setStatus("");
  }

  void poisciTekstInPostaviKurzor() {
    String test = "";
    int pos = kodaTP.getCaretPosition();
    int length = kodaTP.getDocument().getLength();
    try {
      test = kodaTP.getDocument().getText(0, length);
    } catch (BadLocationException ex) {
    }
    int kje = test.indexOf(iskaniNizTF.getText(), pos + 1);
    if (pos > 0 && kje == -1) {
      kje = test.indexOf(iskaniNizTF.getText());
    }
    if (kje != -1) {
      kodaTP.setCaretPosition(kje);
      kodaTP.setSelectionStart(kje);
      kodaTP.setSelectionEnd(kje + iskaniNizTF.getText().length());
    }
  }

  void preberiVsebnoDatoteke() {
    File f = new File(currentDir, trenutnaDatoteka);
    try {
      kodaTP.read(new FileReader(f), null);
      kodaTP.setCaretPosition(0);

      if (autoSearch.isSelected()) {
	poisciTekstInPostaviKurzor();
	kodaTP.requestFocus();
      }
    } catch (Exception e) {
      setStatus("Napaka pri branju datoteke: " + e.toString());
    }
  }

  void vsemNastaviOceno() {
    if (currentDir == null) {
      setStatus("Najprej odpri projekt!");
    } else {
      String ocena;
      ocena = JOptionPane.showInputDialog("Vsem �tudentom nastavi oceno na: ");
      if (ocena != null) {
	for (Student s : studenti.values()) {
	  s.ocena = ocena;
	}
	prikaziOcenoTrenutnegaStudenta();
      }
    }
  }

  void vsemDodajVKomentar() {
    if (currentDir == null) {
      setStatus("Najprej odpri projekt!");
    } else {
      String komentar = statusTA.getText();
      int ok = JOptionPane.showConfirmDialog(this, "Vsem �tudentom v komentar dodam spodnje besedilo (vsebina status polja)? \n" + komentar);
      if (ok == JOptionPane.OK_OPTION) {
	for (Student s : studenti.values()) {
	  s.komentar = komentar.replaceAll("\n", newline) + s.komentar;
	}
	prikaziOcenoTrenutnegaStudenta();
      }
    }
  }
  
  void pogojnoNastaviOceno() {
    if (currentDir == null) {
      setStatus("Najprej odpri projekt!");
    } else {
      PogojnoNastaviDialog pnd = new PogojnoNastaviDialog(this, true);
      pnd.setVisible(true);
      if (pnd.buttonPressed == 1) return; // Cancel 
      
      OdgovorPND odgovor = pnd.getResult();
      for (Student s : studenti.values()) {
         String kje = odgovor.kdo.equals("ocena") ? s.ocena : s.komentar;
         String kaj = odgovor.kaj;
         boolean vsebuje = false;
         if (odgovor.operator.equals("vsebuje")) {
           vsebuje = kje.contains(kaj);
         } else
           vsebuje = kje.equals(kaj);
         if (odgovor.negacija)
           vsebuje = !vsebuje;
         
         if (vsebuje) {
           String novo = "";
           if (!odgovor.operacija.equals("nastavi"))
             novo = odgovor.komu.equals("oceno") ? s.ocena : s.komentar;
           
           novo = novo + odgovor.vKaj;
           if (odgovor.komu.equals("oceno"))
	      s.ocena = novo;
           else
             s.komentar = novo;
         }
      }
      prikaziOcenoTrenutnegaStudenta();
    }
  }
  

  void naslednjaNaloga() {
    int s = nalogeList.getSelectedIndex();

    if (s != -1 && s < nalogeList.getModel().getSize() - 1) {
      nalogeList.setSelectedIndex(s + 1);
    }
  }

  /**
   *
   * @param sourcePath path to source files
   * @param sources a list of sources (with .java extension)
   * @param destPath a path for compiled files
   * @param classpaths a list of paths to bo included in classpath
   * @param msg message substring to be displayed in log file
   * @return
   */
  public static String compile(String srcPath, String[] sources, String destPath,
	  String[] classpaths, String msg) {

    // build a classpath
    StringBuilder sb = new StringBuilder();
    URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
    for (URL url : urlClassLoader.getURLs()) {
      sb.append(url.getFile()).append(File.pathSeparator);
    }
    for (String cp : classpaths) {
      sb.append(cp).append(File.pathSeparator);
    }

    ArrayList<File> srcFiles = new ArrayList();
    int i = 0;
    for (String src : sources) {
      srcFiles.add(new File(srcPath + File.separator + src));
    }

    List<String> options = new ArrayList<String>();
    options.add("-classpath");  // -classpath <path>      Specify where to find user class files
    options.add(sb.toString());
    options.add("-d");          // -d <directory>         Specify where to place generated class files
    options.add(destPath);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    Writer wos = new OutputStreamWriter(os);

    JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager fileManager = javac.getStandardFileManager(null /* diagnosticlistener */, null, null);
    JavaCompiler.CompilationTask task = javac.getTask(wos, fileManager, null /* diagnosticlistener */, options, null, fileManager.getJavaFileObjectsFromFiles(srcFiles));

    Boolean compileOK = task.call();

    if (!compileOK) {
      String error = os.toString();
      error = error.replaceAll(srcPath + File.separator, "");

      return error;
    }
    return "";
  }

  private void runCurrentFile(boolean runDatasets) {
    rr.show();
    rr.clear();
    
    String tmpDir = currentDir + File.separator + tmpDirSuffix;

    String source = kodaTP.getText();
    String regex = "public\\sclass\\s(\\S*)\\s";
    Matcher matcher = Pattern.compile(regex).matcher(source);
    String className;
    if (matcher.find()) {
      className = matcher.group(1);
    } else {
      className = "";
    }


    rr.title(className);

    // remove package declaration
    source = source.replaceAll("package\\s+([^;]*);", "");

    // remove System.exit()
    source = source.replaceAll("System.exit\\s*\\(\\s*[0-9]+\\s*\\);", "");

    if (className.isEmpty()) {
      rr.addTextNL("Ne najdem imena razreda.");
      return;
    } else {
      // check for tmp folder, delete ...
      File tmp = new File(tmpDir);
      if (tmp.exists()) {
	try {
	  FileUtils.deleteDirectory(tmp);
	} catch (Exception e) {

	  return;
	}
      }
      // ... and create new
      tmp.mkdir();

      // copy resources from res folder to tmp/res folder
      File resSrc = new File(currentDir, resDir);
      File resDest = new File(tmpDir, resDir);
      try {
	FileUtils.copyDirectory(resSrc, resDest);
      } catch (IOException ex) {
	rr.addTextNL("Can not copy resuorce folder to tmp folder");
      }

      // write source into java file in tmp folder
      File src = new File(tmp, className + ".java");
      try {
	PrintWriter pw = new PrintWriter(src);
	pw.print(source);
	pw.close();
      } catch (FileNotFoundException ex) {
	rr.addText("Ne morem pisati v datoteko " + src.getAbsolutePath());
	return;
      }

      // compile
      rr.addText(stars + "Compile: javac " + className + ".java\n" + stars);
      String errorCompile = compile(tmpDir, new String[]{src.getName()}, tmpDir, new String[]{tmpDir}, "");

      if (!errorCompile.isEmpty()) {
	rr.addText("\n\n" + errorCompile);
	return;
      }

      rr.addTextNL("OK");

      try {
	int numberOfRuns = 1;
	if (runDatasets) {
	  numberOfRuns = datasets.getData().size();
	}
        
	String mainArgs[];
	InputStream mojIN;
	for (int iRun = 0; iRun < numberOfRuns; iRun++) {

	  String argsString;
	  String stdinString;
	  if (runDatasets) {
	    argsString  = datasets.getData().get(iRun).args;
	    stdinString = datasets.getData().get(iRun).stdInput;
	  } else {
	    argsString  = agrsTF.getText();
	    stdinString = stdinTA.getText();
	  }
	  
	  mainArgs = argsString.split(" ");
	  mojIN    = IOUtils.toInputStream(stdinString);
	  System.setIn(mojIN);

	  rr.addTextNL("\n\n" + stars + "Running: java " + className + " " + argsString + "\n");

	  URLClassLoader parentclassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
	  URL[] parentURLs = parentclassLoader.getURLs();

	  URL[] urls = new URL[parentURLs.length + 1];
	  for (int i = 0; i < urls.length - 1; i++) {
	    urls[i] = parentURLs[i];
	  }
	  urls[urls.length - 1] = new File(tmpDir).toURI().toURL();

	  URLClassLoader classLoader = URLClassLoader.newInstance(urls);

	  Class c = Class.forName(className, true, classLoader);
	  Class[] argTypes = new Class[]{String[].class};

	  Method main = c.getDeclaredMethod("main", argTypes);

	  ByteArrayOutputStream bos = new ByteArrayOutputStream();
	  System.setOut(new PrintStream(bos));
	  main.invoke(null, (Object) mainArgs);

	  rr.addTextNL("\n" + bos.toString());

	}

      } catch (Exception ex) {
	rr.addText("Execution error: " + ex.getCause().toString());
        ex.printStackTrace();
      }
    }
  }

  private void skociNaIsci() {
    iskaniNizTF.requestFocus();
    iskaniNizTF.setSelectionStart(0);
    iskaniNizTF.setSelectionEnd(iskaniNizTF.getText().length());
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    jSplitPane1 = new javax.swing.JSplitPane();
    jScrollPane1 = new javax.swing.JScrollPane();
    nalogeList = new javax.swing.JList();
    jPanel1 = new javax.swing.JPanel();
    jSplitPane2 = new javax.swing.JSplitPane();
    zPanel = new javax.swing.JPanel();
    jLabel5 = new javax.swing.JLabel();
    vpisnaTF = new javax.swing.JTextField();
    jLabel4 = new javax.swing.JLabel();
    jScrollPane2 = new javax.swing.JScrollPane();
    kodaTP = new javax.swing.JEditorPane();
    jLabel1 = new javax.swing.JLabel();
    ocenaTF = new javax.swing.JTextField();
    jPanel2 = new javax.swing.JPanel();
    iskaniNizTF = new javax.swing.JTextField();
    jLabel3 = new javax.swing.JLabel();
    autoSearch = new javax.swing.JCheckBox();
    jButton1 = new javax.swing.JButton();
    runPanel = new javax.swing.JPanel();
    jLabel6 = new javax.swing.JLabel();
    agrsTF = new javax.swing.JTextField();
    jButton2 = new javax.swing.JButton();
    jLabel7 = new javax.swing.JLabel();
    jButton3 = new javax.swing.JButton();
    jComboBox1 = new javax.swing.JComboBox();
    jLabel8 = new javax.swing.JLabel();
    jLabel9 = new javax.swing.JLabel();
    jScrollPane5 = new javax.swing.JScrollPane();
    stdinTA = new javax.swing.JTextArea();
    jButton4 = new javax.swing.JButton();
    sPanel = new javax.swing.JPanel();
    jLabel2 = new javax.swing.JLabel();
    jScrollPane3 = new javax.swing.JScrollPane();
    komentarTA = new javax.swing.JTextArea();
    statusPanel = new javax.swing.JPanel();
    jScrollPane4 = new javax.swing.JScrollPane();
    statusTA = new javax.swing.JTextArea();
    jMenuBar1 = new javax.swing.JMenuBar();
    jMenu1 = new javax.swing.JMenu();
    jMenuItem2 = new javax.swing.JMenuItem();
    jMenuItem1 = new javax.swing.JMenuItem();
    jMenu2 = new javax.swing.JMenu();
    jMenuItem3 = new javax.swing.JMenuItem();
    jMenuItem6 = new javax.swing.JMenuItem();
    jMenuItem4 = new javax.swing.JMenuItem();
    jMenuItem5 = new javax.swing.JMenuItem();
    jMenuItem7 = new javax.swing.JMenuItem();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("Ocenjevalnik");
    getContentPane().setLayout(new java.awt.GridBagLayout());

    nalogeList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
        nalogeListValueChanged(evt);
      }
    });
    nalogeList.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        nalogeListKeyPressed(evt);
      }
    });
    jScrollPane1.setViewportView(nalogeList);

    jSplitPane1.setLeftComponent(jScrollPane1);

    jPanel1.setLayout(new java.awt.BorderLayout());

    jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

    zPanel.setLayout(new java.awt.GridBagLayout());

    jLabel5.setText("ID");
    jLabel5.setMinimumSize(new java.awt.Dimension(70, 20));
    jLabel5.setPreferredSize(new java.awt.Dimension(70, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    zPanel.add(jLabel5, gridBagConstraints);

    vpisnaTF.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        vpisnaTFActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
    zPanel.add(vpisnaTF, gridBagConstraints);

    jLabel4.setText("Koda");
    jLabel4.setMinimumSize(new java.awt.Dimension(70, 20));
    jLabel4.setPreferredSize(new java.awt.Dimension(70, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    zPanel.add(jLabel4, gridBagConstraints);

    kodaTP.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(java.awt.event.KeyEvent evt) {
        kodaTPKeyTyped(evt);
      }
      public void keyPressed(java.awt.event.KeyEvent evt) {
        kodaTPKeyPressed(evt);
      }
    });
    jScrollPane2.setViewportView(kodaTP);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
    zPanel.add(jScrollPane2, gridBagConstraints);

    jLabel1.setText("Ocena");
    jLabel1.setMinimumSize(new java.awt.Dimension(70, 20));
    jLabel1.setPreferredSize(new java.awt.Dimension(70, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    zPanel.add(jLabel1, gridBagConstraints);

    ocenaTF.setColumns(20);
    ocenaTF.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        ocenaTFKeyPressed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    zPanel.add(ocenaTF, gridBagConstraints);

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Iskanje besedila"));
    jPanel2.setLayout(new java.awt.GridBagLayout());

    iskaniNizTF.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        iskaniNizTFActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    jPanel2.add(iskaniNizTF, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
    jPanel2.add(jLabel3, gridBagConstraints);

    autoSearch.setText("Avtomatsko iskanje");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
    jPanel2.add(autoSearch, gridBagConstraints);

    jButton1.setText("I��i");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
    jPanel2.add(jButton1, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    zPanel.add(jPanel2, gridBagConstraints);

    runPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Run class"));
    runPanel.setLayout(new java.awt.GridBagLayout());

    jLabel6.setText("Arguments ");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    runPanel.add(jLabel6, gridBagConstraints);

    agrsTF.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        agrsTFActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    runPanel.add(agrsTF, gridBagConstraints);

    jButton2.setText("Run");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton2ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
    runPanel.add(jButton2, gridBagConstraints);

    jLabel7.setText("Stdin");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    runPanel.add(jLabel7, gridBagConstraints);

    jButton3.setText("Uredi");
    jButton3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton3ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
    runPanel.add(jButton3, gridBagConstraints);

    jComboBox1.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jComboBox1ItemStateChanged(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    runPanel.add(jComboBox1, gridBagConstraints);

    jLabel8.setText("jLabel8");
    runPanel.add(jLabel8, new java.awt.GridBagConstraints());

    jLabel9.setText("Datasets");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    runPanel.add(jLabel9, gridBagConstraints);

    jScrollPane5.setMinimumSize(new java.awt.Dimension(23, 75));

    stdinTA.setColumns(20);
    stdinTA.setRows(2);
    jScrollPane5.setViewportView(stdinTA);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
    runPanel.add(jScrollPane5, gridBagConstraints);

    jButton4.setText("RunAll");
    jButton4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton4ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    runPanel.add(jButton4, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
    gridBagConstraints.weightx = 1.0;
    zPanel.add(runPanel, gridBagConstraints);

    jSplitPane2.setLeftComponent(zPanel);

    sPanel.setLayout(new java.awt.GridBagLayout());

    jLabel2.setText("Komentar");
    jLabel2.setMinimumSize(new java.awt.Dimension(70, 20));
    jLabel2.setPreferredSize(new java.awt.Dimension(70, 20));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
    sPanel.add(jLabel2, gridBagConstraints);

    jScrollPane3.setMinimumSize(new java.awt.Dimension(23, 100));

    komentarTA.setColumns(20);
    komentarTA.setRows(5);
    komentarTA.setMinimumSize(new java.awt.Dimension(240, 200));
    komentarTA.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        komentarTAKeyPressed(evt);
      }
    });
    jScrollPane3.setViewportView(komentarTA);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    sPanel.add(jScrollPane3, gridBagConstraints);

    jSplitPane2.setRightComponent(sPanel);

    jPanel1.add(jSplitPane2, java.awt.BorderLayout.CENTER);

    jSplitPane1.setRightComponent(jPanel1);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    getContentPane().add(jSplitPane1, gridBagConstraints);

    statusPanel.setPreferredSize(new java.awt.Dimension(569, 80));
    statusPanel.setLayout(new java.awt.GridBagLayout());

    statusTA.setColumns(20);
    statusTA.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
    statusTA.setForeground(new java.awt.Color(255, 0, 51));
    statusTA.setRows(2);
    jScrollPane4.setViewportView(statusTA);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    statusPanel.add(jScrollPane4, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    getContentPane().add(statusPanel, gridBagConstraints);

    jMenu1.setText("Datoteka");

    jMenuItem2.setText("Odpri projekt ...");
    jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItem2ActionPerformed(evt);
      }
    });
    jMenu1.add(jMenuItem2);

    jMenuItem1.setText("Izhod");
    jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItem1ActionPerformed(evt);
      }
    });
    jMenu1.add(jMenuItem1);

    jMenuBar1.add(jMenu1);

    jMenu2.setText("Mo�nosti");

    jMenuItem3.setText("Nastavi oceno");
    jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItem3ActionPerformed(evt);
      }
    });
    jMenu2.add(jMenuItem3);

    jMenuItem6.setText("Pogojno nastavi...");
    jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItem6ActionPerformed(evt);
      }
    });
    jMenu2.add(jMenuItem6);

    jMenuItem4.setText("Dodaj besedilo v komentar");
    jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItem4ActionPerformed(evt);
      }
    });
    jMenu2.add(jMenuItem4);

    jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
    jMenuItem5.setText("Run");
    jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItem5ActionPerformed(evt);
      }
    });
    jMenu2.add(jMenuItem5);

    jMenuItem7.setText("Dolo�i regex za vpisno �tevilko...");
    jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItem7ActionPerformed(evt);
      }
    });
    jMenu2.add(jMenuItem7);

    jMenuBar1.add(jMenu2);

    setJMenuBar(jMenuBar1);

    pack();
  }// </editor-fold>//GEN-END:initComponents

	 private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
	   System.exit(0);
	 }//GEN-LAST:event_jMenuItem1ActionPerformed

	 private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
	   odpriProjekt();
	 }//GEN-LAST:event_jMenuItem2ActionPerformed

	 private void nalogeListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_nalogeListValueChanged
	   spremembaIzbire();
	 }//GEN-LAST:event_nalogeListValueChanged

	 private void vpisnaTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vpisnaTFActionPerformed
	   // TODO add your handling code here:
	 }//GEN-LAST:event_vpisnaTFActionPerformed

	 private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
	   vsemNastaviOceno();
	 }//GEN-LAST:event_jMenuItem3ActionPerformed

     private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
       vsemDodajVKomentar();
     }//GEN-LAST:event_jMenuItem4ActionPerformed

  private void kodaTPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kodaTPKeyPressed
    if (evt.getKeyCode() == 10 && evt.getModifiers() != 0) {
      naslednjaNaloga();
      nalogeList.requestFocus();
      evt.setKeyCode(0);
    }

    if (evt.getKeyCode() == KeyEvent.VK_F3) {
      poisciTekstInPostaviKurzor();
    }

    if (evt.getKeyChar() == 'F' && evt.getModifiers() != 0) {
      skociNaIsci();
    }
  }//GEN-LAST:event_kodaTPKeyPressed

  private void nalogeListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nalogeListKeyPressed
    if (evt.getKeyCode() == 10) {
      naslednjaNaloga();
      evt.setKeyCode(0);
    }
  }//GEN-LAST:event_nalogeListKeyPressed

  private void ocenaTFKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ocenaTFKeyPressed
    if (evt.getKeyCode() == 10) {
      naslednjaNaloga();
      evt.setKeyCode(0);
      ocenaTF.requestFocus();

      ocenaTF.setSelectionStart(0);
      ocenaTF.setSelectionEnd(ocenaTF.getText().length());
    }
  }//GEN-LAST:event_ocenaTFKeyPressed

  private void kodaTPKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kodaTPKeyTyped
    char znak = Character.toUpperCase(evt.getKeyChar());
    if (evt.getKeyCode() == KeyEvent.VK_F3) {
      // if (znak=='N') {
      poisciTekstInPostaviKurzor();
      evt.setKeyChar('\0');
    }
  }//GEN-LAST:event_kodaTPKeyTyped

  private void komentarTAKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_komentarTAKeyPressed
    if (evt.getKeyCode() == 10 && evt.getModifiers() != 0) {
      naslednjaNaloga();
      evt.setKeyCode(0);
    }
  }//GEN-LAST:event_komentarTAKeyPressed

  private void iskaniNizTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iskaniNizTFActionPerformed
    poisciTekstInPostaviKurzor();
  }//GEN-LAST:event_iskaniNizTFActionPerformed

  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    poisciTekstInPostaviKurzor();
  }//GEN-LAST:event_jButton1ActionPerformed

  private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    runCurrentFile(false);
  }//GEN-LAST:event_jButton2ActionPerformed

  private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
    runCurrentFile(false);
  }//GEN-LAST:event_jMenuItem5ActionPerformed

  private void agrsTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agrsTFActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_agrsTFActionPerformed

  private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    DatasetEditor dse = new DatasetEditor(this, true);
    dse.setDataSet(datasets);
    dse.setVisible(true);
    if (!dse.canceled) {
      datasets = dse.getDatasets();
      shraniDatasets();

      fillCombo();
    }
  }//GEN-LAST:event_jButton3ActionPerformed

  private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
    prenesiPodatkeIzComba();
  }//GEN-LAST:event_jComboBox1ItemStateChanged

  private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
    runCurrentFile(true);
  }//GEN-LAST:event_jButton4ActionPerformed

  private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
     pogojnoNastaviOceno();
  }//GEN-LAST:event_jMenuItem6ActionPerformed

  private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
    String curRegex = Preferences.userNodeForPackage(this.getClass()).get("Ocenjevalnik.regex", vpisnaStevilkaRegex);
    
    String question = "Vpi�i regex za split in �tevilko polja vpisne �tevilke v imenu datoteke (format: regex,stevilka_polja).\nOpomba: regex lahko nastavis le novemu projektu (t.j. projektu, za katerega datoteka _ocene �e ne obstaja).\nRegex nastavi PRED odpiranjem projekta; morebitno datoteko _ocene pred spremembo regexa POBRISI!";
    
    curRegex = JOptionPane.showInputDialog(question, curRegex);
    if (curRegex!=null && !curRegex.equals(vpisnaStevilkaRegex)) {
      String[] regexPolja = curRegex.split(",");
      if (regexPolja.length != 2) {
        JOptionPane.showMessageDialog(rootPane, "Napa�en format vpisa (pravilen format: regex,stevilka_polja)");
        return;
      }
      vpisnaStevilkaRegex = curRegex;
      Preferences.userNodeForPackage(this.getClass()).put("Ocenjevalnik.regex", vpisnaStevilkaRegex);
    }
  }//GEN-LAST:event_jMenuItem7ActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {

    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
	Ocenjevalnik oc = new Ocenjevalnik();

	//System.out.println(oc.getVpisnaStevilka("abc_23456789_def"));
	//System.exit(0);

	Toolkit tk = Toolkit.getDefaultToolkit();
	oc.setSize(tk.getScreenSize());
	oc.setLocation(0, 0);

	oc.setVisible(true);
      }
    });
  }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JTextField agrsTF;
  private javax.swing.JCheckBox autoSearch;
  private javax.swing.JTextField iskaniNizTF;
  private javax.swing.JButton jButton1;
  private javax.swing.JButton jButton2;
  private javax.swing.JButton jButton3;
  private javax.swing.JButton jButton4;
  private javax.swing.JComboBox jComboBox1;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JLabel jLabel9;
  private javax.swing.JMenu jMenu1;
  private javax.swing.JMenu jMenu2;
  private javax.swing.JMenuBar jMenuBar1;
  private javax.swing.JMenuItem jMenuItem1;
  private javax.swing.JMenuItem jMenuItem2;
  private javax.swing.JMenuItem jMenuItem3;
  private javax.swing.JMenuItem jMenuItem4;
  private javax.swing.JMenuItem jMenuItem5;
  private javax.swing.JMenuItem jMenuItem6;
  private javax.swing.JMenuItem jMenuItem7;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JScrollPane jScrollPane3;
  private javax.swing.JScrollPane jScrollPane4;
  private javax.swing.JScrollPane jScrollPane5;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JSplitPane jSplitPane2;
  private javax.swing.JEditorPane kodaTP;
  private javax.swing.JTextArea komentarTA;
  private javax.swing.JList nalogeList;
  private javax.swing.JTextField ocenaTF;
  private javax.swing.JPanel runPanel;
  private javax.swing.JPanel sPanel;
  private javax.swing.JPanel statusPanel;
  private javax.swing.JTextArea statusTA;
  private javax.swing.JTextArea stdinTA;
  private javax.swing.JTextField vpisnaTF;
  private javax.swing.JPanel zPanel;
  // End of variables declaration//GEN-END:variables

  public static File createTempDir(String baseDir) {
    String baseName = "tmp" + System.currentTimeMillis();

    File tempDir = new File(baseDir, baseName);
    if (tempDir.mkdir()) {
      return tempDir;
    }
    return null;
  }
}
