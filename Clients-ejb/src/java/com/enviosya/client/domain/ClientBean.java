package com.enviosya.client.domain;

import com.enviosya.client.exception.DatoErroneoException;
import com.enviosya.client.exception.EntidadNoExisteException;
import com.enviosya.client.persistence.ClientEntity;
import com.enviosya.client.persistence.TokenEntity;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityExistsException;
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
    @EJB
    private TokenBean tokenBean;

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
    public void cerrarSesion(String ci) throws EntidadNoExisteException {
        try {
            ClientEntity cliente = this.obtenerClientePorCi(ci);
            TokenEntity n = new TokenEntity();
            TokenEntity t = tokenBean.obtenerTokenPorUsuario(cliente.getId());
            tokenBean.borrarToken(t);
        } catch (Exception e) {
            throw new EntidadNoExisteException(e.getMessage());
        }
    }
    private ClientEntity obtenerClientePorCi(String ci)
            throws EntidadNoExisteException {
        try {
            ClientEntity retorno = null;
            List<ClientEntity> lista = null;
            lista = em.createQuery("SELECT u FROM ClientEntity u "
                    + "WHERE u.ci = :ci")
                    .setParameter("ci", ci)
                    .setMaxResults(1)
                    .getResultList();
            for (int i = 0; i<= lista.size(); i++) {
                if(lista.get(i) != null){
                    retorno = lista.get(i);
                    i = lista.size() +1;
                }
            }
            return retorno;
        } catch (Exception e) {
            throw new EntidadNoExisteException("Entidad no existe");
        }
    }

    public boolean estaLogueado(ClientEntity usuario) throws EntidadNoExisteException {
        if (tokenBean.obtenerTokenPorUsuario(usuario.getId()) != null) {
            return true;
        } else {
            return false;
        }
    }


    public TokenEntity iniciarSesion(String ci, String pass, boolean modoEdit)
            throws EntidadNoExisteException, DatoErroneoException {
        String token = "";
        System.out.println(ci + " y " + pass);
        TokenEntity t = null;
        try {
            ClientEntity cliente = obtenerClientePorCi(ci);
            if(cliente == null){
                throw new EntidadNoExisteException("El cliente con ci "
                    + "" + ci + " no fue encontrado.");
            }
            if (modoEdit) {
                if (!estaLogueado(cliente)) {
                    t = crearToken(cliente, pass);
                    return t;
                }
            } else {
                t = crearToken(cliente, pass);
                return t;
            }
        } catch (EntityExistsException e) {
            throw new EntidadNoExisteException("El cliente con ci "
                    + "" + ci + " no fue encontrado.");
        } catch (Exception e) {
            throw e;
        }
        return t;
    }

    private TokenEntity crearToken(ClientEntity cli, String pass)
            throws DatoErroneoException {
        String token = " ";
        if (cli.getContrasena().equals(pass)) {
            token = UUID.randomUUID().toString();
            TokenEntity t = new TokenEntity();
            t.setToken(token);
            t.setUsuario(cli);
            tokenBean.registarToken(t);
            return t;
        } else {
            throw new DatoErroneoException("Las contraseñas no coinciden.");
        }
    }

}
