/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import dataBase.ConexionBD;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import model.Atributo;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author William Duarte
 */
public class GUIReporte extends javax.swing.JFrame {

    private final static String SELECCIONE_ATRIBUTO = "Seleccione atributo";
    
    private ConexionBD conn;
    private String nomTabla;
    private ArrayList<Atributo> elements;
    private ArrayList<Atributo> obtenidos;
    private ArrayList<Atributo> noSelect;
    /**
     * Creates new form GUIReporte
     */
    public GUIReporte() {
        initComponents();
        nomTabla = "CICLISTA";
        conn = new ConexionBD();
        llenarList();
        generarComboBox();
    }
    
    //metodo que llena la lista con los atributos
    public void llenarList()
    {
        try {
            DefaultListModel<String> model = new DefaultListModel<>();
            elements = new ArrayList<>();
            String cad = "SELECT column_name, data_type, data_length FROM user_tab_cols WHERE table_name = '"+nomTabla+"'";
            ResultSet rs = conn.executeQueryStatement(cad);
            while(rs.next())
            {
                Atributo x = new Atributo(rs.getString(1), rs.getString(2), rs.getInt(3));
                elements.add(x);
                model.addElement(x.getNombre());
                listAtributos.setModel(model);
                
            }            
            
        } catch (SQLException ex) {
            Logger.getLogger(GUIReporte.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void obtenerAtributosSeleccionados()
    {
        obtenidos = new ArrayList<>();
        int[] indices = listAtributos.getSelectedIndices();
        
        
        for (int i = 0; i < elements.size(); i++) {
            for (int j = 0;  j < indices.length; j++) {
                if(i == indices[j])
                {
                    System.out.println(elements.get(i).getNombre()+ " "+elements.get(i).getTipoDato()+" "+elements.get(i).getLongitud());
                    obtenidos.add(elements.get(i));
                }
            }
        }
    }
    
    //metodo que obtiene los elementos no seleccionados por el usuario
    public void elementosNoSeleccionados()
    {

    }
    
    
    public void generarComboBox()
    {     
        
        this.cb1.addItem(SELECCIONE_ATRIBUTO);
        this.cb2.addItem(SELECCIONE_ATRIBUTO);
        
        for (int i = 0; i < elements.size(); i++) {
            this.cb1.addItem(elements.get(i).getNombre());
            this.cb2.addItem(elements.get(i).getNombre());
        }
    }
    
    
    //metodo que retorna los atributosSeleccionadosSQL en un jTextField
    public String atributosSeleccionadosSQL()
    {
        String obt = "";
            
        for (int i = 0; i < obtenidos.size(); i++) {
            obt += obtenidos.get(i).getNombre()+", "; 
        }
            
        obt = obt.substring(0,obt.length()-2);
        txtObtenidos.setText(obt);

        return obt;
    }

    
    //metodo que realiza una consulta sencilla sin utilizar el where
    public void condicionSinWhere()
    {
        String obt = atributosSeleccionadosSQL();                           

        String cad = "select "+ obt +" from " + nomTabla;
        System.out.println(cad);

        hacerResultSet(cad);
    }
    
    private String darCondicionWhere(String comp1, String nomAtr, String valorCondicion){
        String cad = "";
        if(comp1.equals("IGUAL"))
        {
            cad = nomAtr + " = '" + valorCondicion + "'";
        }
        else if(comp1.equals("MAYOR"))
        {
            cad = nomAtr+" > " + valorCondicion;
        }
        else if(comp1.equals("EMPIEZA POR"))
        {
            cad = nomAtr+" LIKE '"+valorCondicion+"%'";
        }
        else if(comp1.equals("MENOR"))
        {
            cad = nomAtr+" < "+ valorCondicion;
        }
        return cad;
    }
    
    public void condicionWhereUno()
    {          
        String nomAtr = cb1.getSelectedItem().toString();
        String comp1 = cbComp1.getSelectedItem().toString();
        String buscar = txtCampo1.getText();
        String obt = atributosSeleccionadosSQL(); 
        if(nomAtr.equals(SELECCIONE_ATRIBUTO)){
            JOptionPane.showMessageDialog(this, "Seleccione un atributo para la consulta");
            return;
        }
        String condicionWhere = darCondicionWhere(comp1, nomAtr, buscar);
        if(condicionWhere.equals("")){
            JOptionPane.showMessageDialog(this, "Debe seleccionar un tipo de condicion");
        }else{
            String cad = "select "+ obt +" from " + nomTabla +" where " + condicionWhere;
            System.out.println(cad);
            hacerResultSet(cad);
        }
    }
    
    public void condicionWhereDos()
    {          
        String nomAtr1 = cb1.getSelectedItem().toString();
        String comp1 = cbComp1.getSelectedItem().toString();
        String buscar1 = txtCampo1.getText();
        String compIO = cbIO.getSelectedItem().toString();
        String nomAtr2 = cb2.getSelectedItem().toString();
        String comp2 = cbComp2.getSelectedItem().toString();
        String buscar2 = txtCampo2.getText();
        
        if(nomAtr1.equals(SELECCIONE_ATRIBUTO) || nomAtr2.equals(SELECCIONE_ATRIBUTO)){
            JOptionPane.showMessageDialog(this, "Seleccione un atributo para la consulta");
            return;
        }
        
        String obt = atributosSeleccionadosSQL(); 
        String condicion1 = darCondicionWhere(comp1, nomAtr1, buscar1);
        String condicion2 = darCondicionWhere(comp2, nomAtr2, buscar2);
        if(condicion1.equals("") || condicion2.equals("")){
            JOptionPane.showMessageDialog(this, "Debe seleccionar un tipo de condicion");
        }else{
            String cad = "";
            if(compIO.equals("AND"))
            {
                cad = "select "+ obt +" from " + nomTabla +" where " + condicion1 + " AND " + condicion2;
            }
            else if(compIO.equals("OR"))
            {
                cad = "select "+ obt +" from " + nomTabla +" where " + condicion1 + " OR " + condicion2;
            }
            if(cad.equals("")){
                JOptionPane.showMessageDialog(this, "Seleccione el tipo de compuerta entre las dos condiciones");
            }else{
                System.out.println(cad);
                hacerResultSet(cad);
            }
        }
    }
    
    public void hacerResultSet(String cad)
    {
        try {
            DefaultListModel<String> model = new DefaultListModel<>();
            ResultSet rs = conn.executeQueryStatement(cad);
            ResultSetMetaData rsm = rs.getMetaData();
            
            System.out.println("rms : "+rsm.getColumnCount());
            String x = "";
            boolean existeResultado = false;
            while(rs.next())
            {
                existeResultado = true;
                for (int i = 0; i < rsm.getColumnCount(); i++) {
                    x += rs.getString(i+1)+" ";
                }
                model.addElement(x);
                listConsulta.setModel(model);
                x = "";
            }
            if(!existeResultado)
            {
                JOptionPane.showMessageDialog(this, "La Consulta no tiene resultados");
                listConsulta.setModel(new DefaultListModel());
            }
        } catch (SQLException ex) {
            Logger.getLogger(GUIReporte.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void dispose() {
        conn.closeConecction();
        super.dispose(); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listAtributos = new javax.swing.JList<>();
        butConsultaBasica = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listConsulta = new javax.swing.JList<>();
        txtObtenidos = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cb1 = new javax.swing.JComboBox<>();
        cbComp1 = new javax.swing.JComboBox<>();
        txtCampo1 = new javax.swing.JTextField();
        cb2 = new javax.swing.JComboBox<>();
        cbComp2 = new javax.swing.JComboBox<>();
        txtCampo2 = new javax.swing.JTextField();
        cbIO = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        butConsulta1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        butJasper = new javax.swing.JButton();
        butReporte2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Consulta Ciclista");

        jScrollPane1.setViewportView(listAtributos);

        butConsultaBasica.setText("Consulta Básica");
        butConsultaBasica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butConsultaBasicaActionPerformed(evt);
            }
        });

        jLabel2.setText("Seleccione los atributos para realizar la consulta");

        jScrollPane2.setViewportView(listConsulta);

        txtObtenidos.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Los atributos que ha seleccionado son:");

        cb1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb1ActionPerformed(evt);
            }
        });

        cbComp1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione", "IGUAL", "MENOR", "MAYOR", "EMPIEZA POR" }));
        cbComp1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbComp1ActionPerformed(evt);
            }
        });

        txtCampo1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtCampo1.setPreferredSize(new java.awt.Dimension(10, 21));

        cbComp2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione", "IGUAL", "MENOR", "MAYOR", "EMPIEZA POR" }));

        txtCampo2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtCampo2.setPreferredSize(new java.awt.Dimension(10, 21));

        cbIO.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione", "AND", "OR" }));
        cbIO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbIOActionPerformed(evt);
            }
        });

        jButton2.setText("Consultar 1 y 2");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        butConsulta1.setText("Consultar 1");
        butConsulta1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butConsulta1ActionPerformed(evt);
            }
        });

        jLabel4.setText("Si los párametros de consulta son los que necesita");

        jLabel5.setText("Desea mas añadir mas a su consulta");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Resultado de la consulta");

        jLabel7.setText("Si solo desea realizar una consulta con los párametros elegidos:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Consulta Avanzada");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("1.");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("2.");

        butJasper.setText("Reporte");
        butJasper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butJasperActionPerformed(evt);
            }
        });

        butReporte2.setText("Reporte Agrupamiento");
        butReporte2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butReporte2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbIO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cb1, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbComp1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCampo1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(73, 73, 73)
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(butConsulta1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cb2, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbComp2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCampo2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(butConsultaBasica, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 155, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane2)
                        .addComponent(jLabel6)
                        .addComponent(jLabel3)
                        .addComponent(txtObtenidos, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(butJasper)
                        .addGap(39, 39, 39)
                        .addComponent(butReporte2)))
                .addGap(33, 33, 33))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butConsultaBasica)
                            .addComponent(jLabel7))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cb1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbComp1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCampo1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(butConsulta1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtObtenidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cbIO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cb2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCampo2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(cbComp2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2))
                        .addGap(54, 54, 54))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butJasper)
                            .addComponent(butReporte2))
                        .addGap(47, 47, 47))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void butConsultaBasicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butConsultaBasicaActionPerformed
        try {
            obtenerAtributosSeleccionados();
            condicionSinWhere();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Debe dar valores para la consulta");
        }

    }//GEN-LAST:event_butConsultaBasicaActionPerformed

    private void cbIOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbIOActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbIOActionPerformed

    private void cb1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cb1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            listConsulta.clearSelection();
            obtenerAtributosSeleccionados();
            condicionWhereDos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ingresar valores validos para la consulta");
        }
        

    }//GEN-LAST:event_jButton2ActionPerformed

    private void cbComp1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbComp1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbComp1ActionPerformed

    private void butConsulta1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butConsulta1ActionPerformed
        try {
            listConsulta.clearSelection();
            obtenerAtributosSeleccionados();
            condicionWhereUno();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ingresar valores validos para la consulta");
        }
    }//GEN-LAST:event_butConsulta1ActionPerformed

    private void butJasperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butJasperActionPerformed
        JasperReport reporte;
        try { 
            reporte = (JasperReport) JRLoader.loadObjectFromFile("./src/reports/Reportasdf.jasper");
            JasperPrint print = JasperFillManager.fillReport(reporte, null, conn.getCon());
            JasperViewer ver = new JasperViewer (print,false);
            ver.setTitle("Prueba Reporte");
            ver.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            
            ver.setVisible(true);
            
            
        } catch (Exception ex) {
            Logger.getLogger(GUIReporte.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_butJasperActionPerformed

    private void butReporte2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butReporte2ActionPerformed
        JasperReport reporte;
        try {
            reporte = (JasperReport) JRLoader.loadObjectFromFile("./src/reports/Report2.jasper");
            JasperPrint print = JasperFillManager.fillReport(reporte, null, conn.getCon());
            JasperViewer ver = new JasperViewer (print,false);
            ver.setTitle("Prueba Reporte");
            ver.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            
            ver.setVisible(true);
            
            
        } catch (Exception ex) {
            Logger.getLogger(GUIReporte.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_butReporte2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butConsulta1;
    private javax.swing.JButton butConsultaBasica;
    private javax.swing.JButton butJasper;
    private javax.swing.JButton butReporte2;
    private javax.swing.JComboBox<String> cb1;
    private javax.swing.JComboBox<String> cb2;
    private javax.swing.JComboBox<String> cbComp1;
    private javax.swing.JComboBox<String> cbComp2;
    private javax.swing.JComboBox<String> cbIO;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<String> listAtributos;
    private javax.swing.JList<String> listConsulta;
    private javax.swing.JTextField txtCampo1;
    private javax.swing.JTextField txtCampo2;
    private javax.swing.JTextField txtObtenidos;
    // End of variables declaration//GEN-END:variables
}
