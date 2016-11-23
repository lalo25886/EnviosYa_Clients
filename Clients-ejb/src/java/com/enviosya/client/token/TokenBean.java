/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enviosya.client.token;

import javax.ejb.Stateless;
import javax.ejb.LocalBean;
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

    public void registarToken(Token t) {
        em.persist(t);
    }

    public void borrarToken(Token t) {
        em.remove(t);
    }

    public Token obtenerToken(Long id)  {
        try {
            return em.find(Token.class, id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Token obtenerTokenPorUsuario(Long id) {
        System.out.println("idddddddddd" + id);
        return (Token) em.createQuery("select t from Token t where t.cliente.id=:id")
                .setParameter("id", id).getSingleResult();
    }
}
