package logica;

import java.util.ArrayList;

public class ListaOrdenada {
  private Proceso procesoCajero;
  private Proceso ultimoAgregado;
  private int tamano = 0;
  private int contador = 1;

  public ListaOrdenada() {
    this.procesoCajero = new Proceso(tamano);
    this.procesoCajero.setSiguiente(procesoCajero);
  }


  public void insertar() {

    Proceso procesoNuevo = new Proceso(this.contador);

    ultimoAgregado = procesoNuevo;
    this.contador++;
    int rafagaRestanteNuevoProceso = procesoNuevo.getRafagaRestante();
    Proceso procesoActual = procesoCajero.getSiguiente();
    Proceso procesoAnterior = procesoCajero;

    // Si la cola está vacía y sólo está el cajero.
    if (procesoCajero.getSiguiente() == procesoCajero) {
      procesoCajero.setSiguiente(procesoNuevo);
      procesoNuevo.setSiguiente(procesoCajero);
      return;
    }

    while (procesoActual != procesoCajero) {

      int rafagaRestanteProcesoActual = procesoActual.getRafagaRestante();

      if(rafagaRestanteNuevoProceso >= rafagaRestanteProcesoActual){
        procesoAnterior=procesoActual;
        procesoActual = procesoActual.getSiguiente();
      }
      else{
        procesoAnterior.setSiguiente(procesoNuevo);
        procesoNuevo.setSiguiente(procesoActual);
        return;
      }
    }
    procesoAnterior.setSiguiente(procesoNuevo);
    procesoNuevo.setSiguiente(procesoCajero);
  }

  public void insertar(Proceso procesoNuevo) {

    ultimoAgregado = procesoNuevo;
    this.contador++;
    int prioridadNuevoProceso = procesoNuevo.getRafagaRestante();
    Proceso procesoActual = procesoCajero.getSiguiente();
    Proceso procesoAnterior = procesoCajero;

    // Si la cola está vacía y sólo está el cajero.
    if (procesoCajero.getSiguiente() == procesoCajero) {
      procesoCajero.setSiguiente(procesoNuevo);
      procesoNuevo.setSiguiente(procesoCajero);
      return;
    }

    while (procesoActual != procesoCajero) {

      int prioridadProcesoActual = procesoActual.getRafagaRestante();

      if(prioridadNuevoProceso >= prioridadProcesoActual){
        procesoAnterior=procesoActual;
        procesoActual = procesoActual.getSiguiente();
      }
      else{
        procesoAnterior.setSiguiente(procesoNuevo);
        procesoNuevo.setSiguiente(procesoActual);
        return;
      }
    }
    procesoAnterior.setSiguiente(procesoNuevo);
    procesoNuevo.setSiguiente(procesoCajero);
  }

  public void atender(Proceso procesoRemover){
    Proceso procesoAuxiliar = procesoCajero;
    while (procesoAuxiliar.getSiguiente() != procesoCajero) {
      if(procesoAuxiliar.getSiguiente()==procesoRemover){
        procesoAuxiliar.setSiguiente(procesoRemover.getSiguiente());
        tamano--;
        return;
      }
      procesoAuxiliar = procesoAuxiliar.getSiguiente();
    }
  }

  public Proceso getUltimoEnLista(){
    Proceso proceso = this.procesoCajero.getSiguiente();
    if(proceso!=this.procesoCajero){
      while(proceso.getSiguiente()!=this.procesoCajero){
        proceso=proceso.getSiguiente();
      }
      return proceso;
    }
    return null;
  }



  public ArrayList<Proceso> listarNodos(){
    ArrayList<Proceso> procesos = new ArrayList<Proceso>();
    Proceso procesoAuxiliar = procesoCajero;
    while (procesoAuxiliar.getSiguiente() != procesoCajero) {
      procesos.add(procesoAuxiliar.getSiguiente());
      procesoAuxiliar = procesoAuxiliar.getSiguiente();
    }
    return procesos;
  }

  public void atender(){
    Proceso procesoAtendido = procesoCajero.getSiguiente();
    procesoCajero.setSiguiente(procesoAtendido.getSiguiente());
    this.tamano--;
  }

  public void imprimirLista(){
    String lista = "";
    Proceso procesoAuxiliar = procesoCajero;
    while (procesoAuxiliar.getSiguiente() != procesoCajero) {
      //lista += " "+procesoAuxiliar.getSiguiente().getPrioridad();
      lista += " ("+procesoAuxiliar.getSiguiente().getNombreProceso() +","+procesoAuxiliar.getSiguiente().getRafagaRestante()+ ")";
      procesoAuxiliar = procesoAuxiliar.getSiguiente();
    }

    System.out.println(lista);
  }
  public boolean isEmpty(){
    return (this.procesoCajero.getSiguiente()==this.procesoCajero);
  }

  public int getTamano() {
    return tamano;
  }

  public Proceso getProcesoCajero(){
    return this.procesoCajero;
  }

  public Proceso getUltimoAgregado() {
    return ultimoAgregado;
  }

}