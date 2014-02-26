/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tomaz
 */
public class RunResults extends javax.swing.JDialog {

  /**
   * Creates new form RunResults
   */
  public RunResults(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
    
    setSize(500,500);
    setTitle("Result window");
    setLocation(200,200);
  }
  
  public void showDialog() {
    setVisible(true);
  }

  public void hideDialog() {
    setVisible(false);
  }
  
  public void focus() {
    jButton1.requestFocus();
  }
  
  public void clear(){
    resultTA.setText("");
  }
  
  public void title(String title) {
    setTitle("Result window - " + title);
  }
  
  public void addTextNL(String text) {
    addText(text + "\n");
  }
  
  
  public void addText(String text) {
    resultTA.append(text);
    resultTA.revalidate();
    resultTA.repaint();
    focus();
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jButton1 = new javax.swing.JButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    resultTA = new javax.swing.JTextArea();

    jButton1.setText("OK");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });
    getContentPane().add(jButton1, java.awt.BorderLayout.PAGE_END);

    resultTA.setBackground(new java.awt.Color(51, 51, 51));
    resultTA.setColumns(20);
    resultTA.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
    resultTA.setForeground(new java.awt.Color(51, 255, 0));
    resultTA.setRows(5);
    jScrollPane1.setViewportView(resultTA);

    getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    hideDialog();
  }//GEN-LAST:event_jButton1ActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /* Set the Nimbus look and feel */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
     * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
	if ("Nimbus".equals(info.getName())) {
	  javax.swing.UIManager.setLookAndFeel(info.getClassName());
	  break;
	}
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(RunResults.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(RunResults.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(RunResults.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(RunResults.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /* Create and display the dialog */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
	RunResults dialog = new RunResults(new javax.swing.JFrame(), true);
	dialog.addWindowListener(new java.awt.event.WindowAdapter() {
	  @Override
	  public void windowClosing(java.awt.event.WindowEvent e) {
	    System.exit(0);
	  }
	});
	dialog.setVisible(true);
      }
    });
  }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton jButton1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTextArea resultTA;
  // End of variables declaration//GEN-END:variables
}