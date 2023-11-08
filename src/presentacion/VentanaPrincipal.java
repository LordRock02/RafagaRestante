package presentacion;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class VentanaPrincipal extends ApplicationFrame {

    private Modelo modelo;
    private ControladorVentanaPrincipal control;

    ChartPanel chartPanel;

    private JPanel panelCola;
    private JPanel panelTabla;
    private JPanel diagramaGantt;
    private JTextField txtCantClientes;
    private JTextField txtCantAtender;
    private JButton btnAgregarClientes;
    private JButton btnAtender;
    private JButton btnIniciarDetener;
    DefaultTableModel modelTablaTiempos,modelTablaBloqueado;
    JTable tablaTiempos;
    JTable tablaBloqueados;

    Font fontButton;
    Font fontLabel;

    JScrollPane scrollPaneTiempos,scrollPaneBloqueados;
    JLabel labelCajero;
    JLabel labelAccion;

    private JLabel labelContadorCiclo;
    public VentanaPrincipal(Modelo modelo) {
        super("Sistema Cola Cajero");
        this.modelo=modelo;
        // Establecer el título de la ventana
        this.setLayout(null);
        inicializarComponentes();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        capturarEventos();
    }

    public Modelo getModelo() {
        return modelo;
    }

    public ControladorVentanaPrincipal getControl() {
        if(control == null){
            control = new ControladorVentanaPrincipal(this);
        }
        return control;
    }

    public void inicializarComponentes(){

        JFreeChart chart = ChartFactory.createGanttChart(
                "Diagrama de Gantt", // chart title
                "Procesos", // domain axis label
                "Tiempo", // range axis label
                null, // data
                false, // include legend
                true, // tooltips
                false // urls
        );

        chartPanel = new ChartPanel(chart);
        this.chartPanel.setBounds(0, 520, 1000, 500);

        this.fontButton = new Font("Roboto", Font.BOLD, 15);
        this.fontLabel = new Font("Roboto", Font.BOLD, 25);

        //Paneles

        this.panelCola = new JPanel();
        this.panelCola.setBounds(0,0,1000,720);
        this.getPanelCola().setLayout(null);

        this.panelTabla = new JPanel();
        this.panelTabla.setBounds(1000,0,1000,1080);
        this.getPanelTabla().setLayout(null);

        /*this.diagramaGantt = new JPanel();
        this.diagramaGantt.setBounds(0, 720, 2000, 360);
        this.getDiagramaGantt().setLayout(null);*/


        //Botones

        this.btnAtender = new JButton("ATENDER");
        this.btnAtender.setBounds(440,750,120,40);
        this.btnAtender.setFont(fontButton);

        this.btnAgregarClientes = new JButton("AGREGAR");
        this.btnAgregarClientes.setBounds(440,800,120,40);
        this.btnAgregarClientes.setFont(fontButton);

        this.btnIniciarDetener = new JButton("INICIAR");
        this.btnIniciarDetener.setBounds(440,850,120,40);
        this.btnIniciarDetener.setFont(fontButton);

        //TextFields

        this.txtCantAtender = new JTextField();
        this.txtCantAtender.setBounds(370,750,50,40);
        this.txtCantAtender.setHorizontalAlignment(JTextField.CENTER);
        this.txtCantAtender.setText("1");
        this.txtCantAtender.setFont(this.fontButton);

        this.txtCantClientes = new JTextField();
        this.txtCantClientes.setBounds(370,800,50,40);
        this.txtCantClientes.setHorizontalAlignment(JTextField.CENTER);
        this.txtCantClientes.setText("1");
        this.txtCantClientes.setFont(this.fontButton);

        //Label
        this.labelCajero = new JLabel();
        this.labelCajero.setBounds(60,350,140,150);
        this.labelCajero.setOpaque(true);
        this.labelCajero.setIcon(new ImageIcon(getClass().getResource("/imagenes/atm.png")));

        this.labelAccion = new JLabel();
        this.labelAccion.setBounds(460,900,120,40);
        this.labelAccion.setFont(this.fontLabel);
        this.labelAccion.setHorizontalTextPosition(SwingConstants.CENTER);
        this.labelAccion.setText("Tiempo");

        this.labelContadorCiclo = new JLabel();
        this.labelContadorCiclo.setBounds(490, 940, 120, 40);
        this.getLabelContadorCiclo().setFont(this.fontLabel);
        this.labelContadorCiclo.setHorizontalTextPosition(SwingConstants.CENTER);


        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setFont(new Font("Arial", Font.PLAIN, 16));
        //Tabla cola principal

        modelTablaTiempos = new DefaultTableModel();

        modelTablaTiempos.addColumn("Proceso");
        modelTablaTiempos.addColumn("T.Llegada");
        modelTablaTiempos.addColumn("Rafaga");
        modelTablaTiempos.addColumn("T.Comienzo");
        modelTablaTiempos.addColumn("T.Final");
        modelTablaTiempos.addColumn("T.Retorno");
        modelTablaTiempos.addColumn("T.Espera");
        modelTablaTiempos.addColumn("Estado");

        tablaTiempos = new JTable(modelTablaTiempos);
        tablaTiempos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tablaTiempos.setBackground(Color.white);

        TableColumnModel columnModelTiempos = tablaTiempos.getColumnModel();

        for (int i = 0; i < tablaTiempos.getColumnCount(); i++) {
            columnModelTiempos.getColumn(i).setPreferredWidth(80);
            tablaTiempos.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        scrollPaneTiempos = new JScrollPane(tablaTiempos);
        scrollPaneTiempos.setBounds(1050,40,705,400);

        //Tabla bloqueados

        modelTablaBloqueado = new DefaultTableModel();

        modelTablaBloqueado.addColumn("Proceso");
        modelTablaBloqueado.addColumn("T.Bloqueo");
        modelTablaBloqueado.addColumn("Rafaga");

        tablaBloqueados = new JTable(modelTablaBloqueado);
        tablaBloqueados.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tablaBloqueados.setBackground(Color.white);

        TableColumnModel columnModelBloqueados = tablaTiempos.getColumnModel();

        for (int i = 0; i < tablaBloqueados.getColumnCount(); i++) {
            columnModelBloqueados.getColumn(i).setPreferredWidth(100);
            tablaBloqueados.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        scrollPaneBloqueados = new JScrollPane(tablaBloqueados);
        scrollPaneBloqueados.setBounds(386,500,228,200);


        // Agregar elementos a los paneles
        this.add(chartPanel);
        this.add(this.scrollPaneTiempos);
        this.panelTabla.add(this.scrollPaneBloqueados);
        //this.panelTabla.add(this.btnAtender);
        this.panelTabla.add(this.btnAgregarClientes);
        this.panelTabla.add(this.btnIniciarDetener);
        //this.panelTabla.add(this.txtCantClientes);
       // this.panelTabla.add(this.txtCantAtender);

        this.panelCola.add(this.labelCajero);
        this.panelTabla.add(this.labelAccion);
        this.panelTabla.add(this.labelContadorCiclo);

        this.add(panelCola);
        this.add(panelTabla);
        //this.add(diagramaGantt);
    }

    public DefaultTableModel getModelTablaTiempos() {
        return modelTablaTiempos;
    }

    public DefaultTableModel getModelTablaBloqueado() {
        return modelTablaBloqueado;
    }

    public JPanel getPanelCola() {
        return panelCola;
    }

    public JPanel getPanelTabla() {
        return panelTabla;
    }

    public JPanel getDiagramaGantt() { return diagramaGantt; }

    public JTextField getTxtCantClientes() {
        return txtCantClientes;
    }

    public JTextField getTxtCantAtender() {
        return txtCantAtender;
    }

    public JLabel getLabelAccion() {
        return labelAccion;
    }

    public JButton getBtnIniciarDetener() {
        return btnIniciarDetener;
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    private void capturarEventos() {
        this.btnAtender.addActionListener(getControl());
        this.btnAgregarClientes.addActionListener(getControl());
        this.btnIniciarDetener.addActionListener(getControl());
    }

    public JLabel getLabelContadorCiclo() {
        return labelContadorCiclo;
    }

    public JTable getTablaTiempos() {
        return tablaTiempos;
    }

    public JTable getTablaBloqueados() {
        return tablaBloqueados;
    }
}
