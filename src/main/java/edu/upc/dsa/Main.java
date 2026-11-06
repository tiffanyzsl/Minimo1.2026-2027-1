package edu.upc.dsa;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class Main {
    public static final String BASE_URI = "http://localhost:8080/myapp/";

    public static HttpServer startServer() {
        io.swagger.jaxrs.config.BeanConfig beanConfig = new io.swagger.jaxrs.config.BeanConfig();
        beanConfig.setVersion("1.0.0");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/myapp");
        beanConfig.setResourcePackage("edu.upc.dsa.services");
        beanConfig.setScan(true);

        final ResourceConfig rc = new ResourceConfig().packages("edu.upc.dsa.services");
        rc.register(io.swagger.jaxrs.listing.ApiListingResource.class);
        rc.register(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();

        File carpetaPublic = new File("public");

        if (!carpetaPublic.exists()) {
            carpetaPublic = new File("Minimo1.2026-2027-1/public");
        }

        StaticHttpHandler staticHttpHandler = new StaticHttpHandler(carpetaPublic.getAbsolutePath());
        server.getServerConfiguration().addHttpHandler(staticHttpHandler, "/");

        System.out.println("Servidor encendido en: " + BASE_URI);
        System.out.println("Abre Swagger en tu navegador: http://localhost:8080/swagger/");
        System.out.println("Presiona ENTER para apagar el servidor...");
        System.in.read();
        server.shutdownNow();
    }
}
