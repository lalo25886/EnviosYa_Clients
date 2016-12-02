/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enviosya.client.domain;

import com.enviosya.client.exception.EntidadNoExisteException;
import com.enviosya.client.persistence.TokenEntity;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Gonzalo
 */
@Stateless
@LocalBean
public class TokenBean {

    @PersistenceContext
    public EntityManager em;

    public void registarToken(TokenEntity t) {
        em.persist(t);
    }

    public void borrarToken(TokenEntity t) {
        em.remove(t);
    }

    public TokenEntity obtenerToken(Long id) throws EntidadNoExisteException {
        try {
            return em.find(TokenEntity.class, id);
        } catch (Exception e) {
            throw new EntidadNoExisteException("Entidad no existe");
        }
    }

    public TokenEntity obtenerTokenPorUsuario(Long id) throws EntidadNoExisteException {
        System.out.println("idddddddddd" + id);
        return (TokenEntity) em.createQuery("select t from TokenEntity t "
                + "where t.usuario.id=:id")
                .setParameter("id", id).getSingleResult();
    }
}

