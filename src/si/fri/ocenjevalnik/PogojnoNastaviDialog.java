/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package si.fri.ocenjevalnik;

class OdgovorPND {
  String kdo;
  String operator;
  boolean negacija;
  String kaj;
  String operacija;
  String vKaj;
  String komu;

  public OdgovorPND(String kdo, String operator, String kaj, String operacija, String vKaj, String komu, boolean negacija) {
    this.kdo = kdo;
    this.operator = operator;
    this.kaj = kaj;
    this.operacija = operacija;
    this.vKaj = vKaj;
    this.komu = komu;
    this.negacija = negacija;
  }
  
  
}

/**
 *
 * @author tomaz
 */
public class PogojnoNastaviDialog extends javax.swing.JDialog {

  int buttonPressed = 1;
  
  /**
   * Creates new form PogojnoNastaviDialog
   */
  public PogojnoNastaviDialog(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
  }
  
  public OdgovorPND getResult() {
    return new OdgovorPND((String)kdoCB.getSelectedItem(), (String)operatorCB.getSelectedItem(), 
            kajTF.getText(), (String)operacijaCB.getSelectedItem(), vkajTF.getText(), 
            (String)komuCB.getSelectedItem(), negacijaCB.isSelected());
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

    jLabel1 = new javax.swing.JLabel();
    kdoCB = new javax.swing.JComboBox();
    operatorCB = new javax.swing.JComboBox();
    kajTF = new javax.swing.JTextField();
    operacijaCB = new javax.swing.JComboBox();
    komuCB = new javax.swing.JComboBox();
    vkajTF = new javax.swing.JTextField();
    jLabel2 = new javax.swing.JLabel();
    jPanel1 = new javax.swing.JPanel();
    jPanel2 = new javax.swing.JPanel();
    jPanel3 = new javax.swing.JPanel();
    jButton1 = new javax.swing.JButton();
    jButton2 = new javax.swing.JButton();
    negacijaCB = new javax.swing.JCheckBox();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    getContentPane().setLayout(new java.awt.GridBagLayout());

    jLabel1.setText("�e");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    getContentPane().add(jLabel1, gridBagConstraints);

    kdoCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ocena", "komentar" }));
    kdoCB.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        kdoCBActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    getContentPane().add(kdoCB, gridBagConstraints);

    operatorCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "vsebuje", "==" }));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    getContentPane().add(operatorCB, gridBagConstraints);

    kajTF.setColumns(10);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    getContentPane().add(kajTF, gridBagConstraints);

    operacijaCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "nastavi", "dodaj" }));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    getContentPane().add(operacijaCB, gridBagConstraints);

    komuCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "oceno", "komenta" }));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    getContentPane().add(komuCB, gridBagConstraints);

    vkajTF.setColumns(10);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    getContentPane().add(vkajTF, gridBagConstraints);

    jLabel2.setText("v");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    getContentPane().add(jLabel2, gridBagConstraints);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.weighty = 1.0;
    getContentPane().add(jPanel1, gridBagConstraints);

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    getContentPane().add(jPanel2, gridBagConstraints);

    jButton1.setText("OK");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });
    jPanel3.add(jButton1);

    jButton2.setText("Cancel");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton2ActionPerformed(evt);
      }
    });
    jPanel3.add(jButton2);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    getContentPane().add(jPanel3, gridBagConstraints);

    negacijaCB.setText("!");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
    getContentPane().add(negacijaCB, gridBagConstraints);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void kdoCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kdoCBActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_kdoCBActionPerformed

  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    buttonPressed = 0;
    setVisible(false);
  }//GEN-LAST:event_jButton1ActionPerformed

  private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    buttonPressed = 1;
    setVisible(false);
  }//GEN-LAST:event_jButton2ActionPerformed

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
      java.util.logging.Logger.getLogger(PogojnoNastaviDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(PogojnoNastaviDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(PogojnoNastaviDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(PogojnoNastaviDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
        //</editor-fold>

    /* Create and display the dialog */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        PogojnoNastaviDialog dialog = new PogojnoNastaviDialog(new javax.swing.JFrame(), true);
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
  private javax.swing.JButton jButton2;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JTextField kajTF;
  private javax.swing.JComboBox kdoCB;
  private javax.swing.JComboBox komuCB;
  private javax.swing.JCheckBox negacijaCB;
  private javax.swing.JComboBox operacijaCB;
  private javax.swing.JComboBox operatorCB;
  private javax.swing.JTextField vkajTF;
  // End of variables declaration//GEN-END:variables
}
