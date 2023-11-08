import logica.ListaOrdenada;
import logica.Proceso;
import presentacion.Modelo;

class Main {
  private Modelo miApp;
  public Main() {
    miApp = new Modelo();
    miApp.iniciar();

  }

  public static void main(String[] args) {
    new Main();
  }
}