package com.enviosya.client.domain;

import com.enviosya.client.exception.DatoErroneoException;
import com.enviosya.client.exception.EntidadNoExisteException;
import com.enviosya.client.persistence.ClientEntity;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;

/**
 *
 * @author Gonzalo
 */
@Stateless
@LocalBean
public class ClientBean {

 static Logger log = Logger.getLogger("FILE");
    @PersistenceContext
    private EntityManager em;
    @PostConstruct
    private void init() {
    }

    public ClientEntity agregar(ClientEntity unClientEntity) 
            throws DatoErroneoException {
        try {
            em.persist(unClientEntity);
            em.flush();
        } catch (Exception e) {
            log.error("Error en agregar Cliente Entity: " + e.getMessage());
            throw new DatoErroneoException("Error al agregar un cliente. "
                    + "Verifique los datos ingresados.");
        }
        return unClientEntity;
    }

    public ClientEntity modificar(ClientEntity unClienteEntity)
            throws EntidadNoExisteException {
        try {
            em.merge(unClienteEntity);
        } catch (Exception e) {
             log.error("Error al modificar Cliente Entity: " + e.getMessage());
             throw new EntidadNoExisteException("Error al modificar un cliente."
                    + " El cliente con el id: " + unClienteEntity.getId() + " "
                    + "no se encuentra.");
        }
        return unClienteEntity;
    }

    public boolean eliminar(ClientEntity unClientEntity) {
        try {
            ClientEntity aBorrar =
            em.find(ClientEntity.class, unClientEntity.getId());
            em.remove(aBorrar);
            return true;
        } catch (Exception e) {
             log.error("Error en eliminar Cliente Entity: " + e.getMessage());
        }
          return false;
    }

    public List<ClientEntity> listar() {
        List<ClientEntity> list =
                em.createQuery("select u from ClientEntity u").getResultList();
        return list;
    }

    public Client buscar(Long id) throws EntidadNoExisteException {
        ClientEntity ent = null;
        try {
            ent = em.find(ClientEntity.class, id);
            Client u = new Client();
            u.setId(ent.getId());
            u.setNombre(ent.getNombre());
            return u;
        } catch (Exception e) {
            log.error("Error al buscar Cliente Entity: " + e.getMessage());
            throw new EntidadNoExisteException("Error al buscar un cliente. "
                    + "El cliente con el id: " + id + " no "
                    + "se encuentra.");
        }
    }

    public List<ClientEntity> buscar(String nombre)
            throws EntidadNoExisteException {
        List<ClientEntity> listaCliente = null;
        try {
            em.createQuery("select u from ClientEntity u "
            + "where u.nombre = :nombre")
            .setParameter("nombre", nombre).getResultList();
            return listaCliente;
        } catch (Exception e) {
            log.error("Error al buscar Cliente Entity: " + e.getMessage());
            throw new EntidadNoExisteException("Error al buscar un cliente. "
                    + "El cliente con el nombre: " + nombre + " no "
                    + "se encuentra.");
        }
    }

    public List<ClientEntity> listarClientesEnvios() {
        List<ClientEntity> listaClientes = em.createQuery("SELECT u "
                + "FROM ClientEntity u",
               ClientEntity.class).getResultList();
       return listaClientes;
   }
    public String obtenerMail(Long id) throws EntidadNoExisteException {
        ClientEntity unClientEntity = null;
        try {
            unClientEntity = em.find(ClientEntity.class, id);
            String retorno = "";
            if (unClientEntity != null) {
                retorno = unClientEntity.getEmail();
            }
            return retorno;
        } catch (Exception e) {
            log.error("Error al obtenerMail: " + e.getMessage());
            throw new EntidadNoExisteException("Error al buscar un cliente. "
                    + "El cliente con el id: " + id + " no "
                    + "se encuentra.");
        }
    }
}
