/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AssinaFacilEscolheArquivo.java
 *
 * Created on 14/08/2010, 15:36:25
 */

package assinafacil;

import java.awt.Dialog.ModalExclusionType;

/**
 *
 * @author Mario
 */
public class AssinaFacilEscolheArquivo extends javax.swing.JFrame {

    /** Creates new form AssinaFacilEscolheArquivo */
    public AssinaFacilEscolheArquivo() {
        this.setModalExclusionType(ModalExclusionType.NO_EXCLUDE);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jFileChooser1 = new javax.swing.JFileChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(assinafacil.AssinaFacilApp.class).getContext().getResourceMap(AssinaFacilEscolheArquivo.class);
        jFileChooser1.setApproveButtonText(resourceMap.getString("jFileChooser1.approveButtonText")); // NOI18N
        jFileChooser1.setName("jFileChooser1"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jFileChooser1, org.jdesktop.beansbinding.ELProperty.create("${fileFilter}"), jFileChooser1, org.jdesktop.beansbinding.BeanProperty.create("fileFilter"));
        bindingGroup.addBinding(binding);

        jFileChooser1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jFileChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jFileChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jFileChooser1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser1ActionPerformed
        if (jFileChooser1.getSelectedFile()!=null)
            AssinaFacilApp.getApplication().setArquivoSelecionado(jFileChooser1.getSelectedFile().getAbsolutePath());
        this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jFileChooser1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser jFileChooser1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
