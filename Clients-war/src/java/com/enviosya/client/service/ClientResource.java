package com.enviosya.client.service;

import com.enviosya.client.domain.ClientBean;
import com.enviosya.client.persistence.ClientEntity;
import com.enviosya.client.tool.Tool;
import com.google.gson.Gson;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
    public Response modificar(String body) {
        Gson gson = new Gson();
        ClientEntity u = gson.fromJson(body, ClientEntity.class);
        Response r;
        ClientEntity modificado = clientBean.modificar(u);
        if (modificado == null) {
            r = Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Client")
                    .build();
        } else {
            r = Response
                    .status(Response.Status.CREATED)
                    .entity(gson.toJson(modificado))
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
        Boolean modificado = clientBean.eliminar(u);
        if (!modificado) {
            r = Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Client")
                    .build();
        } else {
            r = Response
                    .status(Response.Status.CREATED)
                    .entity(gson.toJson(modificado))
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
}