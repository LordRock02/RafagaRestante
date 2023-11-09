package presentacion;

import logica.Lista;
import logica.ListaOrdenada;
import logica.Proceso;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Modelo implements Cloneable{

    int contadorReloj =0;
    int cicloReloj = 1000;

    private ListaOrdenada listaOrdenadaTurnos = new ListaOrdenada();
    private Lista listaOrdenadaBloqueados = new Lista();

    private ArrayList<Proceso> procesosTabla = new ArrayList<>();
    private VentanaPrincipal ventanaPrincipal;
    private Proceso procesoActual;
    Thread hiloAtender;
    Thread hiloPintarTabla;
    Thread hiloPintarCola;
    Thread hiloPintarDiagramaGantt;
    Thread hiloAtencionBloqueados;
    boolean sistemaActivo = false;

    int turno = 0;

    private int[][] coordenadas = {
            {270,370},
            {440,370},
            {610,370},
            {780,370},

            {780,170},
            {610,170},
            {440,170},
            {270,170},
            {100,170},

            {100,10},
            {270,10},
            {440,10},
            {610,10},
            {780,10}
    };

    public TaskSeriesCollection model;

    public void iniciar(){
        getVistaPrincipal().setSize(1920, 1080);
        getVistaPrincipal().setVisible(true);
        getVistaPrincipal().setLocationRelativeTo(null);

       // getVistaPrincipal().getPanelCola().setBackground(Color.red);
        //getVistaPrincipal().getPanelTabla().setBackground(Color.blue);
        this.pintarDiagramaGantt();
        this.insertarClientesIniciales();
    }
    public void agregarCliente(){
        int cantidadAgregar = Integer.parseInt(this.getVistaPrincipal().getTxtCantClientes().getText());
        for(int i=0;i<cantidadAgregar;i++){
            this.listaOrdenadaTurnos.insertar();
            listaOrdenadaTurnos.getUltimoAgregado().setTiempoLlegada(this.contadorReloj);
            insertarTabla(listaOrdenadaTurnos.getUltimoAgregado());
        }
        pintarTabla();
        pintarCola();
    }


    public void insertarClientesIniciales(){
        //int cantClientes = (int)(Math.random()*10+1);
        int cantClientes = 3;
        for(int i=0;i<cantClientes;i++){
            this.listaOrdenadaTurnos.insertar();
            listaOrdenadaTurnos.getUltimoAgregado().setTiempoLlegada(0);
            listaOrdenadaTurnos.getUltimoAgregado().setEstado("Listo");
            insertarTabla(listaOrdenadaTurnos.getUltimoAgregado());
        }
        this.pintarTabla();
        this.pintarCola();
    }

    public void iniciarAtencion(){
        hiloAtender = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isSistemaActivo()){
                    //Si la cola está vacía sólo aumenta el contador de ciclo
                    while(listaOrdenadaTurnos.getProcesoCajero().getSiguiente() == listaOrdenadaTurnos.getProcesoCajero()){
                        try {
                            Thread.sleep(cicloReloj);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        contadorReloj++;
                        ventanaPrincipal.getLabelContadorCiclo().setText(""+contadorReloj);
                    }

                    //Proceso de atención
                    procesoActual = listaOrdenadaTurnos.getProcesoCajero().getSiguiente();
                    procesoActual.setEstado("En ejecución");
                    procesoActual.setTiempoComienzo(contadorReloj);
                    ventanaPrincipal.semaforo.setBackground(Color.RED);
                    ventanaPrincipal.semaforo.setText("Seccion Critica Ocupada");
                    actualizarTabla(procesoActual);
                    System.out.println("\nLista a atender: ");
                    listaOrdenadaTurnos.imprimirLista();
                    System.out.println("\n");



                    System.out.println("\nProceso actual :"+procesoActual.getNombreProceso() + " T comienzo: "+procesoActual.getTiempoComienzo() + " Rafaga: "+procesoActual.getRafagaRestante() + " Estado: "+procesoActual.getEstado());

                    procesoActual.setRafagaEjecutadaParcial(0);

                    procesoActual.setRafagaRestante(procesoActual.getRafaga() - procesoActual.getRafagaEjecutadaTotal());
                    int rafagaAEjecutar = procesoActual.getRafagaRestante();

                    //System.out.println("Rafaga ejecutar: "+rafagaAEjecutar);

                    for (int i=0;i<rafagaAEjecutar;i++){

                        //Simular un ciclo de reloj
                        try {
                            Thread.sleep(cicloReloj);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ventanaPrincipal.getLabelContadorCiclo().setText(""+contadorReloj);
                        System.out.println("Ciclo: "+(i+1));

                        procesoActual.setRafagaEjecutadaTotal(procesoActual.getRafagaEjecutadaTotal()+1);
                        procesoActual.setRafagaEjecutadaParcial(procesoActual.getRafagaEjecutadaParcial()+1);
                        procesoActual.setRafagaRestante(procesoActual.getRafaga() - procesoActual.getRafagaEjecutadaTotal());
                        contadorReloj++;


                        int randomBloqueo = (int)(Math.random()*100+1);
                        //System.out.println("For ciclo: "+(i+1));
                        if(randomBloqueo % 7  == 0 && procesoActual.getRafagaRestante()>0){
                            int tiempoBloqueo = (int)(Math.random()*8+4);
                            procesoActual.setTiempoBloqueo(tiempoBloqueo);
                            procesoActual.setEstado("Bloqueado");
                            System.out.println("Bloqueado");
                            break;
                        }

                        if(listaOrdenadaTurnos.getProcesoCajero().getSiguiente() != procesoActual){
                            procesoActual.setEstado("Expulsado");
                            break;
                        }
                    }

                    //Calculos de tiempos
                    procesoActual.setTiempoFinal(procesoActual.getRafagaEjecutadaParcial() + procesoActual.getTiempoComienzo());
                    procesoActual.setTiempoRetorno(procesoActual.getTiempoFinal() - procesoActual.getTiempoLlegada());
                    procesoActual.setTiempoEspera(procesoActual.getTiempoRetorno() - procesoActual.getRafagaEjecutadaTotal());

                    listaOrdenadaTurnos.atender(procesoActual);


                    if(procesoActual.getEstado() == "Bloqueado"){
                        listaOrdenadaBloqueados.insertar(procesoActual);
                    }
                    else if(procesoActual.getEstado() == "Expulsado"){
                        insertarProcesoExpulsado(procesoActual);
                    }
                    else{
                        procesoActual.setEstado("Terminado");
                    }

                    actualizarTabla(procesoActual);
                    ventanaPrincipal.semaforo.setBackground(Color.GREEN);
                    ventanaPrincipal.semaforo.setText("Seccion Critica Libre");
                }
            }
        });
        hiloAtender.start();
    }

    public void insertarProcesoExpulsado(Proceso procesoInsertar){


        Proceso clon = (Proceso) procesoInsertar.clone();
        clon.setEstado("Listo");
        //System.out.println("ID proceso clone: "+clon.getIdProceso());
        clon.setTiempoComienzo(0);
        clon.setTiempoFinal(0);
        clon.setTiempoRetorno(0);
        clon.setTiempoEspera(0);
        clon.setNombreProceso(procesoInsertar.getNombreProceso()+"*");

        listaOrdenadaTurnos.insertar(clon);
        insertarTabla(clon);
    }

    public void insertarTabla(Proceso procesoInsertar){
        Proceso procesoCopia = (Proceso) procesoInsertar.clone();
        this.procesosTabla.add(procesoCopia);
    }

    public void actualizarTabla(Proceso procesoActualizar){

        for(int i=0;i<procesosTabla.size();i++){
            Proceso proceso = procesosTabla.get(i);

            if(proceso.getNombreProceso() == procesoActualizar.getNombreProceso()){
                proceso.setTiempoComienzo(procesoActualizar.getTiempoComienzo());
                proceso.setTiempoFinal(procesoActualizar.getTiempoFinal());
                proceso.setTiempoRetorno(procesoActualizar.getTiempoRetorno());
                proceso.setTiempoEspera(procesoActualizar.getTiempoEspera());
                proceso.setEstado(procesoActualizar.getEstado());
                return;
            }
        }
    }

    public void iniciarAtencionBloqueados(){
        hiloAtencionBloqueados = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isSistemaActivo()){
                    try {
                        Thread.sleep(cicloReloj);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ArrayList<Proceso> procesosBloqueados = listaOrdenadaBloqueados.listarNodos();
                    for(int i = 0; i< procesosBloqueados.size(); i++){
                        Proceso proceso = procesosBloqueados.get(i);
                        if(proceso.getTiempoBloqueo()>1){
                            proceso.setTiempoBloqueo(proceso.getTiempoBloqueo()-1);
                        }
                        else{
                            Proceso clon = (Proceso) proceso.clone();
                            clon.setEstado("Listo");
                            //System.out.println("ID proceso clone: "+clon.getIdProceso());
                            clon.setTiempoComienzo(0);
                            clon.setTiempoFinal(0);
                            clon.setTiempoRetorno(0);
                            clon.setTiempoEspera(0);
                            clon.setNombreProceso(proceso.getNombreProceso()+"'");

                            listaOrdenadaBloqueados.atender(proceso);

                            listaOrdenadaTurnos.insertar(clon);
                            insertarTabla(clon);
                        }
                    }
                }
            }
        });
        hiloAtencionBloqueados.start();
    }

    public void iniciarPintarCola(){
        hiloPintarCola = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isSistemaActivo()){
                    try {
                        Thread.sleep(cicloReloj); // Agregar un retraso de 200 ms entre cada elemento

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pintarCola();
                }
            }
        });
        hiloPintarCola.start();
    }

    public void iniciarPintarTabla(){
        hiloPintarTabla = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isSistemaActivo() ){
                    try {

                        Thread.sleep(cicloReloj); // Agregar un retraso de 200 ms entre cada elemento

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pintarTabla();
                }
            }
        });
        hiloPintarTabla.start();
    }
    public void iniciarDiagramaGantt(){
        hiloPintarDiagramaGantt = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isSistemaActivo() ){
                    try {

                        Thread.sleep(cicloReloj); // Agregar un retraso de 200 ms entre cada elemento

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pintarDiagramaGantt();
                }
            }
        });
        hiloPintarDiagramaGantt.start();
    }

    public void atenderCliente(){
        int cantidadAtender = Integer.parseInt(this.getVistaPrincipal().getTxtCantAtender().getText());
        for(int i=0;i<cantidadAtender;i++){
            this.listaOrdenadaTurnos.atender();
        }
        this.pintarCola();
        this.pintarTabla();

    }

    public void pintarTabla(){

        this.getVistaPrincipal().getModelTablaTiempos().setRowCount(0);
        for(int i = 0; i< procesosTabla.size(); i++){
            this.getVistaPrincipal().getModelTablaTiempos().addRow(new Object[]{
                    procesosTabla.get(i).getNombreProceso(),
                    procesosTabla.get(i).getTiempoLlegada(),
                    procesosTabla.get(i).getRafagaRestante(),
                    procesosTabla.get(i).getTiempoComienzo(),
                    procesosTabla.get(i).getTiempoFinal(),
                    procesosTabla.get(i).getTiempoRetorno(),
                    procesosTabla.get(i).getTiempoEspera(),
                    procesosTabla.get(i).getEstado()
            });
        }

        ArrayList<Proceso> procesosBloqueados = listaOrdenadaBloqueados.listarNodos();
        this.getVistaPrincipal().getModelTablaBloqueado().setRowCount(0);

        for(int i = 0; i< procesosBloqueados.size(); i++){
            Proceso proceso = procesosBloqueados.get(i);
            this.getVistaPrincipal().getModelTablaBloqueado().addRow(new Object[]{
                    proceso.getNombreProceso(),
                    proceso.getTiempoBloqueo(),
                    proceso.getRafagaRestante()
            });
        }
    }

    public void pintarCola(){
        getVistaPrincipal().getPanelCola().removeAll();

        JLabel labelCajero = new JLabel();
        labelCajero.setBounds(60,350,140,150);
        labelCajero.setOpaque(true);
        //labelCajero.setBackground(Color.blue);
        labelCajero.setIcon(new ImageIcon(getClass().getResource("/imagenes/atm.png")));

        getVistaPrincipal().getPanelCola().add(labelCajero);


        ArrayList<Proceso> procesos = listaOrdenadaTurnos.listarNodos();
        int cantClientes = procesos.size();

        if(cantClientes>14){
            cantClientes=14;
        }

        for (int i = 0; i < cantClientes; i++) {
            Proceso proceso = procesos.get(i);
            String labelText = "" +
                    "<html>" +
                    "<div style='width:80px;height:85px;display:flex;flex-direction:column;align-items:space-between;justify-content:space-between;background-color:#D9D9D9;padding:5px;border-radius:5px;'>"+
                        "<p style='text-align:center;margin:0;font-size:9px;'> Raf. Rest: "+ proceso.getRafagaRestante()+""+"</p>"+
                        "<img style='margin-left:40px;' src="+ "'"+getClass().getResource("/imagenes/persona.png")+"'>" + "</img>"+
                        "<p style='text-align:center;margin:0;font-size:9px;'>"+ proceso.getNombreProceso()+""+"</p>" +
                        "<p style='text-align:center;margin:0;font-size:9px;'>"+ proceso.getRafaga()+" Rafaga"+"</p>" +
                    "</div>"+
                    "</html>";

            JLabel label = new JLabel(labelText);
            label.setBounds(coordenadas[i][0],coordenadas[i][1],100,110);
            label.setOpaque(true);
            label.setBackground(Color.lightGray);
            this.getVistaPrincipal().getPanelCola().add(label);
        }
        getVistaPrincipal().getPanelCola().repaint();
        getVistaPrincipal().repaint();

    }
    public void pintarDiagramaGantt(){
        final IntervalCategoryDataset dataset = createSampleDataset();
        // create the chart...
        final JFreeChart chart = ChartFactory.createGanttChart(
                "Diagrama de Gantt", // chart title
                "procesos", // domain axis label
                "tiempo", // range axis label
                dataset, // data
                false, // include legend
                true, // tooltips
                false // urls
        );
        final CategoryPlot plot = (CategoryPlot) chart.getPlot();
        DateAxis range = (DateAxis) plot.getRangeAxis();
        range.setDateFormatOverride(new SimpleDateFormat("S"));
        range.setMaximumDate(new Date(100 + (contadorReloj/100)*100));

        // insertando diagrama de gantt al panel
        getVistaPrincipal().getChartPanel().setChart(chart);

        //GanttRenderer personnalisÃ©..
        MyRenderer renderer = new MyRenderer(model);
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);
        getVistaPrincipal().getChartPanel().repaint();
    }
    private IntervalCategoryDataset createSampleDataset() {
        model = new TaskSeriesCollection();
        final TaskSeries s = new TaskSeries("");
        //System.out.println("tamano :"+s.getTasks().size());
        for(int i=0; i<procesosTabla.size(); i++){
            //System.out.println("proceso :" + procesosIngresados.get(i)[0] + " estado: " + procesosIngresados.get(i)[7]);
        }
        if(procesoActual!=null){
            for(int i=0; i<procesosTabla.size(); i++){
                System.out.println(procesosTabla.get(i).getNombreProceso() + " " + procesosTabla.get(i).getEstado());
                if(procesosTabla.get(i).getEstado()=="Listo"){
                    final Task t = new Task(""+procesosTabla.get(i).getNombreProceso(), new SimpleTimePeriod(procesosTabla.get(i).getTiempoLlegada(), contadorReloj));
                    final Task t1 = new Task("esperando", new SimpleTimePeriod(procesosTabla.get(i).getTiempoLlegada(), contadorReloj));
                    final Task t2 = new Task("ejecutando", new SimpleTimePeriod(0, 0));
                    t.addSubtask(t1);
                    t.addSubtask(t2);
                    s.add(t);
                }
                if(procesosTabla.get(i).getEstado()=="En ejecución"){
                    //System.out.println("Pintando proceso en ejecucion");
                    final Task t = new Task(""+procesosTabla.get(i).getNombreProceso(), new SimpleTimePeriod(procesosTabla.get(i).getTiempoLlegada(), contadorReloj));
                    final Task t1 = new Task("esperando", new SimpleTimePeriod(procesosTabla.get(i).getTiempoLlegada(), procesosTabla.get(i).getTiempoComienzo()));
                    final Task t2 = new Task("ejecutando", new SimpleTimePeriod(procesosTabla.get(i).getTiempoComienzo(), contadorReloj));
                    t.addSubtask(t1);
                    t.addSubtask(t2);
                    s.add(t);
                }
                if(procesosTabla.get(i).getEstado()=="Bloqueado"){
                    //System.out.println("Pintando proceso en ejecucion");
                    final Task t = new Task(""+procesosTabla.get(i).getNombreProceso(), new SimpleTimePeriod(procesosTabla.get(i).getTiempoLlegada(), procesosTabla.get(i).getTiempoFinal()));
                    final Task t1 = new Task("esperando", new SimpleTimePeriod(procesosTabla.get(i).getTiempoLlegada(), procesosTabla.get(i).getTiempoComienzo()));
                    final Task t2 = new Task("ejecutando", new SimpleTimePeriod(procesosTabla.get(i).getTiempoComienzo(), procesosTabla.get(i).getTiempoFinal()));
                    System.out.println("rafaga ejecutada parcial del proceso Bloqueado: "+procesosTabla.get(i).getTiempoComienzo() + procesosTabla.get(i).getRafagaEjecutadaParcial());
                    t.addSubtask(t1);
                    t.addSubtask(t2);
                    s.add(t);
                }
               if(procesosTabla.get(i).getEstado()=="Terminado" || procesosTabla.get(i).getEstado()=="Expulsado"){
                    final Task t = new Task(""+procesosTabla.get(i).getNombreProceso(), new SimpleTimePeriod(procesosTabla.get(i).getTiempoLlegada(), contadorReloj));
                    final Task t1 = new Task("esperando", new SimpleTimePeriod(procesosTabla.get(i).getTiempoLlegada(),procesosTabla.get(i).getTiempoComienzo()));
                    final Task t2 = new Task("ejecutando", new SimpleTimePeriod(procesosTabla.get(i).getTiempoComienzo(), procesosTabla.get(i).getTiempoFinal()));
                    t.addSubtask(t1);
                    t.addSubtask(t2);
                    s.add(t);
                }
            }

            model.add(s);
        }
        //model.removeAll();
        return model;
    }

    public boolean isSistemaActivo() {
        return sistemaActivo;
    }

    public void setSistemaActivo(boolean sistemaActivo) {
        this.sistemaActivo = sistemaActivo;
    }


    public void resetHilos(){
        this.hiloPintarTabla=null;
        this.hiloPintarCola=null;
        this.hiloAtender=null;
        this.hiloAtencionBloqueados=null;


        this.hiloPintarDiagramaGantt=null;
    }

    public VentanaPrincipal getVistaPrincipal() {
        if(ventanaPrincipal == null){
            ventanaPrincipal = new VentanaPrincipal(this);
        }
        return ventanaPrincipal;
    }

}
