package com.enviosya.client.service;

import com.enviosya.client.domain.ClientBean;
import com.enviosya.client.exception.DatoErroneoException;
import com.enviosya.client.exception.EntidadNoExisteException;
import com.enviosya.client.persistence.ClientEntity;
import com.enviosya.client.tool.Tool;
import com.google.gson.Gson;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;
import javax.ws.rs.Consumes;
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

//    @GET
//    @Path("getClientesEnvios")
//    @Consumes(MediaType.APPLICATION_JSON)
//    public String  getClientesEnvios() {
//        Gson gson = new Gson();
//        String  ret = gson.toJson("Error al obtener un cliente.");
//        Response r;
//        List<ClientEntity> list = null;
//        try {
//            list = clientBean.listarClientesEnvios();
//       } catch (PersistenceException e) {
//            r = Response
//                    .status(Response.Status.ACCEPTED)
//                    .entity(ret)
//                    .build();
//       } catch (EntidadNoExisteException ex) {
//            r = Response
//                    .status(Response.Status.ACCEPTED)
//                    .entity(ret)
//                    .build();
//        }
//        return gson.toJson(list);
//    }

    @GET
    @Path("getClient/{id}")
    @Consumes(MediaType.TEXT_HTML)
    public String getClienteNotificar(@PathParam("id") String id) {
        Response r;
        Gson gson = new Gson();
        String  ret = gson.toJson("Error al buscar un cadete. "
                + "El cadete no existe en la base de datos.");
        ClientEntity unClient = new ClientEntity();
        unClient.setId(Long.parseLong(id));
        String retorno = "";
        try {
            if (!clientBean.existeCliente(unClient.getCi())) {
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
}