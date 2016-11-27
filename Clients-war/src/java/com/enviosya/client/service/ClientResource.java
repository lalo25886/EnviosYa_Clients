package com.enviosya.client.service;

import com.enviosya.client.domain.ClientBean;
import com.enviosya.client.exception.DatoErroneoException;
import com.enviosya.client.exception.EntidadNoExisteException;
import com.enviosya.client.persistence.ClientEntity;
import com.enviosya.client.tool.Tool;
import com.google.gson.Gson;
import java.util.List;
import javax.ejb.EJB;
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
    public Response agregar(String body) throws DatoErroneoException {
        Gson gson = new Gson();
        ClientEntity u = gson.fromJson(body, ClientEntity.class);
        Tool t = new Tool();
        String tarjetaEncriptada = t.Encriptar(u.getNumeroTarjeta());
        u.setNumeroTarjeta(tarjetaEncriptada);
        String claveEncriptada = t.Encriptar(u.getClaveTarjeta());
        u.setClaveTarjeta(claveEncriptada);
        Response r;
        ClientEntity creado = clientBean.agregar(u);
        if (creado == null) {
            r = Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Client")
                    .build();
        } else {
            r = Response
                    .status(Response.Status.CREATED)
                    .entity(gson.toJson(creado))
                    .build();
        }
        return r;
    }
    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modificar(String body)  throws EntidadNoExisteException {
        Gson gson = new Gson();
        ClientEntity u = gson.fromJson(body, ClientEntity.class);
        Response r;
        ClientEntity modificado = null;
        try {
            modificado = clientBean.modificar(u);
            r = Response
                    .status(Response.Status.CREATED)
                    .entity(gson.toJson(modificado))
                    .build();
        } catch (Exception e) {
            r = Response
                    .status(Response.Status.CONFLICT)
                    .entity("Client")
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
        Boolean eliminado = clientBean.eliminar(u);
        if (!eliminado) {
            r = Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Client")
                    .build();
        } else {
            r = Response
                    .status(Response.Status.CREATED)
                    .entity(gson.toJson(eliminado))
                    .build();
        }
        return r;
    }

    @GET
    @Path("getClientesEnvios")
    @Consumes(MediaType.APPLICATION_JSON)
    public String  getClientesEnvios() {
       Gson gson = new Gson();
       List<ClientEntity> list = clientBean.listarClientesEnvios();
       return gson.toJson(list);
    }

    @GET
    @Path("getClient/{id}")
    @Consumes(MediaType.TEXT_HTML)
    public String getClienteNotificar(@PathParam("id") String id)
            throws EntidadNoExisteException {
        ClientEntity unClient = new ClientEntity();
        unClient.setId(Long.parseLong(id));
        try {
            String retorno = clientBean.obtenerMail(unClient.getId());
            return retorno;
        } catch (Exception e) {
            throw new EntidadNoExisteException("Error al buscar un cliente. "
                    + "El cliente con el id: " + id + " no "
                    + "se encuentra.");
        }
    }
}