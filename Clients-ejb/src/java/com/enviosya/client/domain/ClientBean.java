package com.enviosya.client.domain;

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

    public ClientEntity agregar(ClientEntity unClientEntity) {
        try {
            em.persist(unClientEntity);
            return unClientEntity;
        } catch (Exception e) {
            log.error("Error en agregar Cliente Entity: " + e.getMessage());
        }
         return null;
    }

    public ClientEntity modificar(ClientEntity unClienteEntity) {
        try {
            em.merge(unClienteEntity);
            return unClienteEntity;
        } catch (Exception e) {
             log.error("Error en eliminar Cliente Entity: " + e.getMessage());
        }
        return null;
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

    public Client buscar(Long id) {
        ClientEntity ent = em.find(ClientEntity.class, id);
        Client u = new Client();
        u.setId(ent.getId());
        u.setNombre(ent.getNombre());
        return u;
    }

    public List<ClientEntity> buscar(String nombre) {
        List<ClientEntity> listaCliente =
                em.createQuery("select u from ClientEntity u "
                + "where u.nombre = :nombre")
                .setParameter("nombre", nombre).getResultList();
        return listaCliente;
    }

    public List<ClientEntity> listarClientesEnvios() {
        List<ClientEntity> listaClientes = em.createQuery("SELECT u "
                + "FROM ClientEntity u",
               ClientEntity.class).getResultList();
       return listaClientes;
   }
    public String obtenerMail(Long id) {
        ClientEntity unClientEntity = em.find(ClientEntity.class, id);
        return unClientEntity.getEmail();
    }

}
