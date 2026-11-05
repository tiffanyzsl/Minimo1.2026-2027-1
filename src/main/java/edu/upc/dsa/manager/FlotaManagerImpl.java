package edu.upc.dsa.manager;

import edu.upc.dsa.models.Dron;
import edu.upc.dsa.models.Pilot;
import edu.upc.dsa.models.PlanVuelo;
import edu.upc.dsa.models.Position;

import org.apache.log4j.Logger;

import java.util.*;

public class FlotaManagerImpl implements FlotaManager {

    private static FlotaManagerImpl instance;
    final static Logger logger = Logger.getLogger(FlotaManagerImpl.class);

    private Map<String, Dron> drones;
    private Map<String, Pilot> pilotos;
    private Stack<Dron> almacen;
    private List<PlanVuelo> planesVuelo;

    // Constructor privado para Singleton
    private FlotaManagerImpl() {
        this.drones = new HashMap<>();
        this.pilotos = new HashMap<>();
        this.almacen = new Stack<>();
        this.planesVuelo = new LinkedList<>();
    }

    // Método getInstance Singleton
    public static FlotaManagerImpl getInstance() {
        if (instance == null) {
            instance = new FlotaManagerImpl();
        }
        return instance;
    }

    @Override
    public void addDron(String id, String name, String fabricante, String modelo) {
        this.addDron(new Dron(id, name, fabricante, modelo));
    }

    @Override
    public void addDron(Dron dron) {
        logger.info("Inicio addDron: " + dron.getId() + ", " + dron.getName());
        this.drones.put(dron.getId(), dron);
        logger.info("Fin addDron: Dron añadido correctamente");
    }

    @Override
    public void addPilot(String id, String nombre, String apellido) {
        this.addPilot(new Pilot(id, nombre, apellido));
    }

    @Override
    public void addPilot(Pilot pilot) {
        logger.info("Inicio addPilot: " + pilot.getId() + ", " + pilot.getNombre());
        this.pilotos.put(pilot.getId(), pilot);
        logger.info("Fin addPilot: Piloto añadido correctamente");
    }

    @Override
    public List<Dron> getDronesOrdenadosPorHoras() {
        logger.info("Inicio getDronesOrdenadosPorHoras()");
        List<Dron> res = new LinkedList<>(this.drones.values());

        res.sort(new Comparator<Dron>() {
            @Override
            public int compare(Dron d1, Dron d2) {
                return Double.compare(d2.getHorasVuelo(), d1.getHorasVuelo());
            }
        });

        logger.info("Fin getDronesOrdenadosPorHoras: Total drones = " + res.size());
        return res;
    }

    @Override
    public List<Pilot> getPilotosOrdenadosPorHoras() {
        logger.info("Inicio getPilotosOrdenadosPorHoras()");
        List<Pilot> res = new LinkedList<>(this.pilotos.values());

        res.sort(new Comparator<Pilot>() {
            @Override
            public int compare(Pilot p1, Pilot p2) {
                return Double.compare(p2.getHorasVuelo(), p1.getHorasVuelo());
            }
        });

        logger.info("Fin getPilotosOrdenadosPorHoras: Total pilotos = " + res.size());
        return res;
    }

    @Override
    public Dron guardarDronAlmacen(String idD) {
        logger.info("Inicio guardarDronAlmacen para idD: " + idD);
        Dron d = this.drones.get(idD);

        if (d == null) {
            logger.warn("guardarDronAlmacen: Dron no encontrado con idD " + idD);
            return null;
        }

        // Se guarda en el fondo del almacén y aproxima hacia la puerta (Stack LIFO)
        this.almacen.push(d);
        logger.info("Fin guardarDronAlmacen: Dron guardado. En almacén: " + this.almacen.size());
        return d;
    }

    @Override
    public Dron repararDron() {
        logger.info("Inicio repararDron()");

        if (this.almacen.isEmpty()) {
            logger.warn("repararDron: El almacén está vacío");
            return null;
        }

        // Extrae el dron más cercano a la puerta (el último que entró)
        Dron d = this.almacen.pop();
        logger.info("Fin repararDron: Dron reparado idD = " + d.getId());
        return d;
    }

    @Override
    public PlanVuelo crearPlanVuelo(String id, String idD, String idP, String fecha, double duracionHoras, List<Position> positions) {
        logger.info("Inicio crearPlanVuelo: id = " + id + ", idD = " + idD + ", idP = " + idP);

        Dron d = this.drones.get(idD);
        Pilot p = this.pilotos.get(idP);

        if (d == null || p == null) {
            logger.error("crearPlanVuelo: Dron o piloto no existen");
            return null;
        }

        // Comprobación 1: si el dron está en el almacén en mantenimiento
        if (this.almacen.contains(d)) {
            logger.error("crearPlanVuelo: El dron " + idD + " está en mantenimiento en el almacén");
            return null;
        }

        // Comprobación 2: solapamiento de horario para el dron o el piloto
        for (PlanVuelo pv : this.planesVuelo) {
            if (pv.getFecha().equals(fecha)) {
                if (pv.getIdD().equals(idD)) {
                    logger.error("crearPlanVuelo: El dron " + idD + " ya tiene un plan en esa fecha");
                    return null;
                }
                if (pv.getIdP().equals(idP)) {
                    logger.error("crearPlanVuelo: El piloto " + idP + " ya tiene un plan en esa fecha");
                    return null;
                }
            }
        }

        PlanVuelo nuevoPlan = new PlanVuelo(id, idD, idP, fecha, duracionHoras);
        if (positions != null) {
            nuevoPlan.setPositions(positions);
        }

        d.addHorasVuelo(duracionHoras);
        p.addHorasVuelo(duracionHoras);

        this.planesVuelo.add(nuevoPlan);
        logger.info("Fin crearPlanVuelo: Plan registrado con éxito");
        return nuevoPlan;
    }

    @Override
    public List<PlanVuelo> getPlanesVueloPiloto(String idP) {
        logger.info("Inicio getPlanesVueloPiloto para idP: " + idP);
        List<PlanVuelo> res = new LinkedList<>();

        for (PlanVuelo pv : this.planesVuelo) {
            if (pv.getIdP().equals(idP)) {
                res.add(pv);
            }
        }

        logger.info("Fin getPlanesVueloPiloto: Encontrados = " + res.size());
        return res;
    }

    @Override
    public List<PlanVuelo> getPlanesVueloDron(String idD) {
        logger.info("Inicio getPlanesVueloDron para idD: " + idD);
        List<PlanVuelo> res = new LinkedList<>();

        for (PlanVuelo pv : this.planesVuelo) {
            if (pv.getIdD().equals(idD)) {
                res.add(pv);
            }
        }

        logger.info("Fin getPlanesVueloDron: Encontrados = " + res.size());
        return res;
    }

    @Override
    public Dron getDron(String idD) {
        return this.drones.get(idD);
    }

    @Override
    public Pilot getPilot(String idP) {
        return this.pilotos.get(idP);
    }

    @Override
    public int numDrones() {
        return this.drones.size();
    }

    @Override
    public int numPilotos() {
        return this.pilotos.size();
    }

    @Override
    public int numDronesAlmacen() {
        return this.almacen.size();
    }

    @Override
    public void clear() {
        this.drones.clear();
        this.pilotos.clear();
        this.almacen.clear();
        this.planesVuelo.clear();
    }
}