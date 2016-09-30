/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import entity.exceptions.NonexistentEntityException;
import entity.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author TimmosQuadros
 */
public class CountrylanguageJpaController implements Serializable {

    public CountrylanguageJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Countrylanguage countrylanguage) throws PreexistingEntityException, Exception {
        if (countrylanguage.getCountrylanguagePK() == null) {
            countrylanguage.setCountrylanguagePK(new CountrylanguagePK());
        }
        countrylanguage.getCountrylanguagePK().setCountryCode(countrylanguage.getCountry().getCode());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Country country = countrylanguage.getCountry();
            if (country != null) {
                country = em.getReference(country.getClass(), country.getCode());
                countrylanguage.setCountry(country);
            }
            em.persist(countrylanguage);
            if (country != null) {
                country.getCountrylanguageCollection().add(countrylanguage);
                country = em.merge(country);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCountrylanguage(countrylanguage.getCountrylanguagePK()) != null) {
                throw new PreexistingEntityException("Countrylanguage " + countrylanguage + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Countrylanguage countrylanguage) throws NonexistentEntityException, Exception {
        countrylanguage.getCountrylanguagePK().setCountryCode(countrylanguage.getCountry().getCode());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Countrylanguage persistentCountrylanguage = em.find(Countrylanguage.class, countrylanguage.getCountrylanguagePK());
            Country countryOld = persistentCountrylanguage.getCountry();
            Country countryNew = countrylanguage.getCountry();
            if (countryNew != null) {
                countryNew = em.getReference(countryNew.getClass(), countryNew.getCode());
                countrylanguage.setCountry(countryNew);
            }
            countrylanguage = em.merge(countrylanguage);
            if (countryOld != null && !countryOld.equals(countryNew)) {
                countryOld.getCountrylanguageCollection().remove(countrylanguage);
                countryOld = em.merge(countryOld);
            }
            if (countryNew != null && !countryNew.equals(countryOld)) {
                countryNew.getCountrylanguageCollection().add(countrylanguage);
                countryNew = em.merge(countryNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                CountrylanguagePK id = countrylanguage.getCountrylanguagePK();
                if (findCountrylanguage(id) == null) {
                    throw new NonexistentEntityException("The countrylanguage with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(CountrylanguagePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Countrylanguage countrylanguage;
            try {
                countrylanguage = em.getReference(Countrylanguage.class, id);
                countrylanguage.getCountrylanguagePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The countrylanguage with id " + id + " no longer exists.", enfe);
            }
            Country country = countrylanguage.getCountry();
            if (country != null) {
                country.getCountrylanguageCollection().remove(countrylanguage);
                country = em.merge(country);
            }
            em.remove(countrylanguage);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Countrylanguage> findCountrylanguageEntities() {
        return findCountrylanguageEntities(true, -1, -1);
    }

    public List<Countrylanguage> findCountrylanguageEntities(int maxResults, int firstResult) {
        return findCountrylanguageEntities(false, maxResults, firstResult);
    }

    private List<Countrylanguage> findCountrylanguageEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Countrylanguage.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Countrylanguage findCountrylanguage(CountrylanguagePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Countrylanguage.class, id);
        } finally {
            em.close();
        }
    }

    public int getCountrylanguageCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Countrylanguage> rt = cq.from(Countrylanguage.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
