import edu.upc.dsa.manager.FlotaManager;
import edu.upc.dsa.manager.FlotaManagerImpl;
import edu.upc.dsa.models.Dron;
import edu.upc.dsa.models.PlanVuelo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FlotaManagerTest {

    private FlotaManager manager;

    @Before
    public void setUp() {

        manager = FlotaManagerImpl.getInstance(); //singleton
        manager.clear();

        manager.addDron("D1", "Mavic", "DJI", "Pro");
        manager.addDron("D2", "Phantom", "DJI", "4");
        manager.addPilot("P1", "Mario", "Bros");
    }

    @After
    public void tearDown() {
        manager.clear();
    }

    @Test
    public void testAñadirDronYPiloto() {
        Assert.assertEquals(2, manager.numDrones());

        manager.addDron("D3", "Inspire", "DJI", "2");
        Assert.assertEquals(3, manager.numDrones());

        Assert.assertEquals(1, manager.numPilotos());
    }

    @Test
    public void testAlmacenLIFO() {
        manager.guardarDronAlmacen("D1"); // Entra primero (Fondo)
        manager.guardarDronAlmacen("D2"); // Entra segundo (Puerta)

        Assert.assertEquals(2, manager.numDronesAlmacen());

        Dron reparado = manager.repararDron();
        Assert.assertEquals("D2", reparado.getId());
        Assert.assertEquals(1, manager.numDronesAlmacen());
    }

    @Test
    public void testCrearPlanVueloExito() {
        PlanVuelo plan = manager.crearPlanVuelo("V1", "D1", "P1", "2026-11-05 10:00", 2.5, null);

        Assert.assertNotNull(plan); // Debe haberse creado
        Assert.assertEquals(1, manager.getPlanesVueloPiloto("P1").size());
        Assert.assertEquals(1, manager.getPlanesVueloDron("D1").size());
        Assert.assertEquals(2.5, manager.getDron("D1").getHorasVuelo(), 0.1);
    }

    @Test
    public void testCrearPlanVueloErrorMantenimiento() {
        manager.guardarDronAlmacen("D1"); // Lo metemos a mantenimiento

        PlanVuelo plan = manager.crearPlanVuelo("V2", "D1", "P1", "2026-11-05 12:00", 1.0, null);

        Assert.assertNull(plan);
    }
}