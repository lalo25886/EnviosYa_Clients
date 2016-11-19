package com.enviosya.service.client;

import com.enviosya.domain.client.ClientBean;
import com.enviosya.persistence.client.ClientEntity;
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
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        List<ClientEntity> list = clientBean.listar();
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response agregar(String body) {
        Gson gson = new Gson();
        ClientEntity u = gson.fromJson(body, ClientEntity.class);
        Response r;
        ClientEntity creado = clientBean.agregar(u);
        if (creado == null) {
            r = Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("AgregarClient")
                    .build();
        } else {
            r = Response
                    .status(Response.Status.CREATED)
                    .entity(gson.toJson(creado))
                    .build();
        }
        return r;
    } 
    
}

