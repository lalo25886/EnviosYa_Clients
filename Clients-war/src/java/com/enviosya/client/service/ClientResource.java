package com.enviosya.client.service;

import com.enviosya.client.domain.ClientBean;
import com.enviosya.client.exception.DatoErroneoException;
import com.enviosya.client.exception.EntidadNoExisteException;
import com.enviosya.client.persistence.ClientEntity;
import com.enviosya.client.persistence.TokenEntity;
import com.enviosya.client.tool.Tool;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Gonzalo
 */
@Path("client")
public class ClientResource {

    @EJB
    private ClientBean clientBean;

    @Context
    private UriInfo context;

    public ClientResource() {
    }

    @GET
    @Path("getJson")
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        List<ClientEntity> list = clientBean.listar();
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @POST
    @Path("add")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response agregar(String body) {
        Gson gson = new Gson();
        String  ret = gson.toJson("Error al agregar un cliente. "
                + "Verifique los datos ingresados");
        String existe = "El cliente ya existe en la base de datos";
        ClientEntity u;
        String vacio = "";
        ClientEntity creado;
        try {
            u = gson.fromJson(body, ClientEntity.class);
            if (clientBean.existeCliente(u.getCi())) {
                return Response
                        .status(Response.Status.ACCEPTED)
                        .entity(existe)
                        .build();
            }
            if (u.getEmail().equalsIgnoreCase(vacio)
                || u.getNombre().equalsIgnoreCase(vacio)    
                || u.getNumeroTarjeta().equalsIgnoreCase(vacio)
                || u.getClaveTarjeta().equalsIgnoreCase(vacio)) {
                return Response
                        .status(Response.Status.ACCEPTED)
                        .entity(ret)
                        .build();
            }
            Tool t = new Tool();
            String tarjetaEncriptada = t.Encriptar(u.getNumeroTarjeta());
            u.setNumeroTarjeta(tarjetaEncriptada);
            String claveEncriptada = t.Encriptar(u.getClaveTarjeta());
            u.setClaveTarjeta(claveEncriptada);
            creado = clientBean.agregar(u);
        } catch (DatoErroneoException ex) {
           return Response
                   .status(Response.Status.ACCEPTED)
                   .entity(ret)
                   .build();
        } catch (EntidadNoExisteException ex) {
           return Response
                   .status(Response.Status.ACCEPTED)
                   .entity(existe).build();
        }
        return Response
                    .status(Response.Status.CREATED)
                    .entity(gson.toJson(creado))
                    .build();
    }
    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modificar(String body) {
        Gson gson = new Gson();
         String  ret = gson.toJson("Error al modificar un cliente. "
                + "Verifique los datos ingresados");
        String existe = "El cliente no existe en la base de datos";
        ClientEntity u = gson.fromJson(body, ClientEntity.class);
        Response r;
        ClientEntity modificado;
        try {
            if (clientBean.existeCliente(u.getCi())) {
                return Response
                        .status(Response.Status.ACCEPTED)
                        .entity(existe)
                        .build();
            }
            modificado = clientBean.modificar(u);
            r =  Response
                    .status(Response.Status.CREATED)
                    .entity(gson.toJson(modificado))
                    .build();
        } catch (PersistenceException e) {
            r = Response
                    .status(Response.Status.ACCEPTED)
                    .entity(ret)
                    .build();
        } catch (EntidadNoExisteException ex) {
            r = Response
                    .status(Response.Status.ACCEPTED)
                    .entity(ret)
                    .build();
        }
        return r;
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response eliminar(String body) {
        Gson gson = new Gson();
        ClientEntity u = gson.fromJson(body, ClientEntity.class);
        Response r;
        String  ret = gson.toJson("Error al eliminar un cliente. "
                + "El cliente con id: " + u.getId() + " no existe.");
        String ok = "El cliente fue eliminado exitosamente";
        String existe = "El cliente no existe en la base de datos";
        Boolean eliminado = false;
        try {
             if (clientBean.existeCliente(u.getCi())) {
                return Response
                        .status(Response.Status.ACCEPTED)
                        .entity(existe)
                        .build();
            }
            eliminado = clientBean.eliminar(u);
            r = Response
                    .status(Response.Status.CREATED)
                    .entity(gson.toJson(eliminado))
                    .build();
        } catch (EntidadNoExisteException e) {
            r = Response
                    .status(Response.Status.ACCEPTED)
                    .entity(ret)
                    .build();
        }
        return r;
   }

    @GET
    @Path("getClient/{id}")
    @Consumes(MediaType.TEXT_HTML)

    public String getClienteNotificar(@PathParam("id") String id) {
        Response r;
        Gson gson = new Gson();
        String  ret = gson.toJson("Error al buscar un cadete. "
                + "El cliente no existe en la base de datos.");
        ClientEntity unClient = new ClientEntity();
        unClient.setId(Long.parseLong(id));
        String retorno = "";
        try {
            if (!clientBean.existeCliente(unClient.getId())) {
                retorno = "-5";
            } else {
                retorno = clientBean.obtenerMail(unClient.getId());
                return retorno;
            }
        } catch (EntidadNoExisteException ex) {
            r =  Response
                    .status(Response.Status.ACCEPTED)
                    .entity(ret)
                    .build();
            return "-5";
        }
        return retorno;
    }

    @GET
    @Path("isClient/{id}")
    @Consumes(MediaType.TEXT_HTML)
    public String esCliente(@PathParam("id") String id) {
        Response r;
        Gson gson = new Gson();
        String  ret = gson.toJson("Error al buscar el cliente. "
                + "El cliente no existe en la base de datos.");
        ClientEntity unClient = new ClientEntity();
        unClient.setId(Long.parseLong(id));
        String retorno = "";
        try {
            if (!clientBean.existeCliente(unClient.getId())) {
                retorno = "-5";
            } else {
                retorno = clientBean.obtenerMail(unClient.getId());
                return retorno;
            }
        } catch (EntidadNoExisteException ex) {
            r =  Response
                    .status(Response.Status.ACCEPTED)
                    .entity(ret)
                    .build();
            return "-5";
        }
        return retorno;
    }

    @POST
    @Path("logout/{ci}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cerrarSesion(@PathParam("ci") String ciCliente)
            throws EntidadNoExisteException {
        System.out.println("CERRAR SESION " + ciCliente);
        Gson gson = new Gson();
        clientBean.cerrarSesion(ciCliente);
        return Response.ok().entity(gson.toJson("Cerró sesión "
                + "exitosamente.")).build();
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response iniciarSesion(String json)
            throws EntidadNoExisteException, DatoErroneoException {
        System.out.println("USUARIO JSON " + json);
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map = (Map<String, Object>) gson.fromJson(json, map.getClass());
        String contrasena = map.get("contrasena").toString();
        String usuario = map.get("usuario").toString();
        System.out.println(contrasena + " y " + usuario);
        TokenEntity t = clientBean.iniciarSesion(usuario, contrasena, false);
        return Response.ok().entity(gson.toJson(t)).build();
    }
    @POST
    @Path("islogin/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response estaLogueado(@PathParam("id") String id)
            throws EntidadNoExisteException {
        Gson gson = new Gson();
        ClientEntity unClient = new ClientEntity();
        unClient.setId(Long.parseLong(id));
        System.out.println("DADDSADASD: " + unClient.getId());
        boolean esta = clientBean.estaLogueado(unClient);
        if (esta){
            return Response.ok().entity(gson.toJson("1")).build();
        }
        return Response.ok().entity(gson.toJson("0")).build();
    }
}