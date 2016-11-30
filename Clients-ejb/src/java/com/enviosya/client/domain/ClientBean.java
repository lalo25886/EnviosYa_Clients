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
import javax.persistence.PersistenceException;
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
        } catch (PersistenceException e) {
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
            em.flush();
        } catch (PersistenceException e) {
             log.error("Error al modificar Cliente Entity: " + e.getMessage());
             throw new EntidadNoExisteException("Error al modificar un cliente."
                    + " El cliente con el id: " + unClienteEntity.getId() + " "
                    + "no se encuentra.");
        }
        return unClienteEntity;
    }

    public boolean eliminar(ClientEntity unClientEntity)  
            throws EntidadNoExisteException {
        try {
            ClientEntity aBorrar =
            em.find(ClientEntity.class, unClientEntity.getId());
            em.remove(aBorrar);
            return true;
        } catch (PersistenceException e) {
             log.error("Error al eliminar Cliente Entity: " + e.getMessage());
            throw new EntidadNoExisteException("Error al eliminar un cliente. "
                    + "Verifique los datos.");
        }
    }

    public List<ClientEntity> listar() {
        List<ClientEntity> list =
                em.createQuery("select u from ClientEntity u").getResultList();
        return list;
    }

//    public Client buscar(Long id) throws EntidadNoExisteException {
//        ClientEntity ent;
//        try {
//            ent = em.find(ClientEntity.class, id);
//            Client u = new Client();
//            u.setId(ent.getId());
//            u.setNombre(ent.getNombre());
//            return u;
//        } catch (Exception e) {
//            log.error("Error al buscar Cliente Entity: " + e.getMessage());
//            throw new EntidadNoExisteException("Error al buscar un cliente. "
//                    + "El cliente con el id: " + id + " no "
//                    + "se encuentra.");
//        }
//    }

    public boolean existeCliente(String ci)
            throws EntidadNoExisteException {
        List<ClientEntity> listaCliente;
        boolean retorno = false;
        try {
            listaCliente = em.createQuery("SELECT u FROM ClientEntity u "
            + "WHERE u.ci = :ci")
            .setParameter("ci", ci).getResultList();

            if (!listaCliente.isEmpty()) {
                retorno = true;
            }
        } catch (PersistenceException e) {
            log.error("Error existeCliente: " + e.getMessage());
            throw new EntidadNoExisteException("Error en existeCliente");
        }
        return retorno;
    }

//    public List<ClientEntity> listarClientesEnvios()
//            throws EntidadNoExisteException {
//        try {
//        List<ClientEntity> listaClientes = em.createQuery("SELECT u "
//                + "FROM ClientEntity u",
//               ClientEntity.class).getResultList();
//        return listaClientes;
//        } catch (PersistenceException e) {
//            log.error("Error al buscar Cliente Entity: " + e.getMessage());
//            throw new EntidadNoExisteException("Error al buscar un cliente.");
//        }
//   }
    public String obtenerMail(Long id) throws EntidadNoExisteException {
        ClientEntity unClientEntity = null;
        try {
            unClientEntity = em.find(ClientEntity.class, id);
            String retorno = "";
            if (unClientEntity != null) {
                retorno = unClientEntity.getEmail();
            }
            return retorno;
        } catch (PersistenceException e) {
            log.error("Error al obtenerMail: " + e.getMessage());
            throw new EntidadNoExisteException("Error al buscar un cliente. "
                    + "El cliente con el id: " + id + " no "
                    + "se encuentra.");
        }
    }

    public boolean existeCliente(Long idRecibido)
            throws EntidadNoExisteException {
        List<ClientEntity> listaCliente;
        boolean retorno = false;
        try {
            listaCliente = em.createQuery("SELECT u FROM ClientEntity u "
            + "WHERE u.id = :id")
            .setParameter("id", idRecibido).getResultList();

            if (!listaCliente.isEmpty()) {
                retorno = true;
            }
        } catch (PersistenceException e) {
            log.error("Error existeCliente: " + e.getMessage());
            throw new EntidadNoExisteException("Error en existeCliente");
        }
        return retorno;
    }
}
