/*  This file is part of AssinaFacil.

    AssinaFacil is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AssinaFacil is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AssinaFacil.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
 * AssinaFacilEscolheArquivoEntrada.java
 *
 * Created on 14/08/2010, 15:36:25
 */

package net.sf.assinafacil;

import java.awt.Dialog.ModalExclusionType;
import javax.swing.JFrame;

/**
 *
 * @author Mario
 */
public class AssinaFacilEscolheArquivoEntrada extends javax.swing.JDialog {

    JFrame mainWindow;
    /** Creates new form AssinaFacilEscolheArquivoEntrada */
    public AssinaFacilEscolheArquivoEntrada(JFrame mainWindow) {
        super(mainWindow);
        this.mainWindow = mainWindow;
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

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(net.sf.assinafacil.AssinaFacilApp.class).getContext().getResourceMap(AssinaFacilEscolheArquivoEntrada.class);
        setTitle(resourceMap.getString("FormFileChooser.title")); // NOI18N
        setAlwaysOnTop(true);
        setModal(true);
        setName("FormFileChooser"); // NOI18N

        jFileChooser1.setApproveButtonText(resourceMap.getString("jFileChooser1.approveButtonText")); // NOI18N
        jFileChooser1.setCurrentDirectory(null);
        jFileChooser1.setDialogTitle(resourceMap.getString("jFileChooser1.dialogTitle")); // NOI18N
        jFileChooser1.setDialogType(javax.swing.JFileChooser.CUSTOM_DIALOG);
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
            AssinaFacilApp.getApplication().setSelectedInputFile(jFileChooser1.getSelectedFile().getAbsolutePath());
        this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jFileChooser1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser jFileChooser1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
