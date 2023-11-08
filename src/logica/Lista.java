package logica;

import java.util.ArrayList;

public class Lista {
  private Proceso procesoCajero;
  private int tamano = 0;

  private int contador = 0;

  public Lista() {
    this.procesoCajero = new Proceso(tamano);
    this.procesoCajero.setSiguiente(procesoCajero);
  }

  public void insertar() {
    tamano++;
    Proceso procesoNuevo = new Proceso(++contador);
    Proceso procesoAuxiliar = procesoCajero;
    while (procesoAuxiliar.getSiguiente() != procesoCajero) {
      procesoAuxiliar = procesoAuxiliar.getSiguiente();
    }
    procesoAuxiliar.setSiguiente(procesoNuevo);
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

  public void insertar(Proceso procesoNuevo){
    tamano++;
    Proceso procesoAuxiliar = procesoCajero;
    while (procesoAuxiliar.getSiguiente() != procesoCajero) {
      procesoAuxiliar = procesoAuxiliar.getSiguiente();
    }
    procesoAuxiliar.setSiguiente(procesoNuevo);
    procesoNuevo.setSiguiente(procesoCajero);
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
      lista += " "+procesoAuxiliar.getSiguiente().getIdProceso();
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


}