package edu.upc.dsa.manager;

import edu.upc.dsa.models.Dron;
import edu.upc.dsa.models.Pilot;
import edu.upc.dsa.models.PlanVuelo;
import edu.upc.dsa.models.Position;

import java.util.List;

public interface FlotaManager {

    public void addDron(String id, String name, String fabricante, String modelo);
    public void addDron(Dron dron);
    public void addPilot(String id, String nombre, String apellido);
    public void addPilot(Pilot pilot);

    public List<Dron> getDronesOrdenadosPorHoras();
    public List<Pilot> getPilotosOrdenadosPorHoras();

    public Dron guardarDronAlmacen(String idD);

    public Dron repararDron();

    public PlanVuelo crearPlanVuelo(String id, String idD, String idP, String fecha, double duracionHoras, List<Position> positions);

    public List<PlanVuelo> getPlanesVueloPiloto(String idP);
    public List<PlanVuelo> getPlanesVueloDron(String idD);

    public Dron getDron(String idD);
    public Pilot getPilot(String idP);
    public int numDrones();
    public int numPilotos();
    public int numDronesAlmacen();
    public void clear();
}