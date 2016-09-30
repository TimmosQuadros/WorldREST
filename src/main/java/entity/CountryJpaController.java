/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import entity.exceptions.IllegalOrphanException;
import entity.exceptions.NonexistentEntityException;
import entity.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author TimmosQuadros
 */
public class CountryJpaController implements Serializable {

    public CountryJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Country country) throws PreexistingEntityException, Exception {
        if (country.getCountrylanguageCollection() == null) {
            country.setCountrylanguageCollection(new ArrayList<Countrylanguage>());
        }
        if (country.getCityCollection() == null) {
            country.setCityCollection(new ArrayList<City>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Countrylanguage> attachedCountrylanguageCollection = new ArrayList<Countrylanguage>();
            for (Countrylanguage countrylanguageCollectionCountrylanguageToAttach : country.getCountrylanguageCollection()) {
                countrylanguageCollectionCountrylanguageToAttach = em.getReference(countrylanguageCollectionCountrylanguageToAttach.getClass(), countrylanguageCollectionCountrylanguageToAttach.getCountrylanguagePK());
                attachedCountrylanguageCollection.add(countrylanguageCollectionCountrylanguageToAttach);
            }
            country.setCountrylanguageCollection(attachedCountrylanguageCollection);
            Collection<City> attachedCityCollection = new ArrayList<City>();
            for (City cityCollectionCityToAttach : country.getCityCollection()) {
                cityCollectionCityToAttach = em.getReference(cityCollectionCityToAttach.getClass(), cityCollectionCityToAttach.getId());
                attachedCityCollection.add(cityCollectionCityToAttach);
            }
            country.setCityCollection(attachedCityCollection);
            em.persist(country);
            for (Countrylanguage countrylanguageCollectionCountrylanguage : country.getCountrylanguageCollection()) {
                Country oldCountryOfCountrylanguageCollectionCountrylanguage = countrylanguageCollectionCountrylanguage.getCountry();
                countrylanguageCollectionCountrylanguage.setCountry(country);
                countrylanguageCollectionCountrylanguage = em.merge(countrylanguageCollectionCountrylanguage);
                if (oldCountryOfCountrylanguageCollectionCountrylanguage != null) {
                    oldCountryOfCountrylanguageCollectionCountrylanguage.getCountrylanguageCollection().remove(countrylanguageCollectionCountrylanguage);
                    oldCountryOfCountrylanguageCollectionCountrylanguage = em.merge(oldCountryOfCountrylanguageCollectionCountrylanguage);
                }
            }
            for (City cityCollectionCity : country.getCityCollection()) {
                Country oldCountryCodeOfCityCollectionCity = cityCollectionCity.getCountryCode();
                cityCollectionCity.setCountryCode(country);
                cityCollectionCity = em.merge(cityCollectionCity);
                if (oldCountryCodeOfCityCollectionCity != null) {
                    oldCountryCodeOfCityCollectionCity.getCityCollection().remove(cityCollectionCity);
                    oldCountryCodeOfCityCollectionCity = em.merge(oldCountryCodeOfCityCollectionCity);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCountry(country.getCode()) != null) {
                throw new PreexistingEntityException("Country " + country + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Country country) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Country persistentCountry = em.find(Country.class, country.getCode());
            Collection<Countrylanguage> countrylanguageCollectionOld = persistentCountry.getCountrylanguageCollection();
            Collection<Countrylanguage> countrylanguageCollectionNew = country.getCountrylanguageCollection();
            Collection<City> cityCollectionOld = persistentCountry.getCityCollection();
            Collection<City> cityCollectionNew = country.getCityCollection();
            List<String> illegalOrphanMessages = null;
            for (Countrylanguage countrylanguageCollectionOldCountrylanguage : countrylanguageCollectionOld) {
                if (!countrylanguageCollectionNew.contains(countrylanguageCollectionOldCountrylanguage)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Countrylanguage " + countrylanguageCollectionOldCountrylanguage + " since its country field is not nullable.");
                }
            }
            for (City cityCollectionOldCity : cityCollectionOld) {
                if (!cityCollectionNew.contains(cityCollectionOldCity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain City " + cityCollectionOldCity + " since its countryCode field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Countrylanguage> attachedCountrylanguageCollectionNew = new ArrayList<Countrylanguage>();
            for (Countrylanguage countrylanguageCollectionNewCountrylanguageToAttach : countrylanguageCollectionNew) {
                countrylanguageCollectionNewCountrylanguageToAttach = em.getReference(countrylanguageCollectionNewCountrylanguageToAttach.getClass(), countrylanguageCollectionNewCountrylanguageToAttach.getCountrylanguagePK());
                attachedCountrylanguageCollectionNew.add(countrylanguageCollectionNewCountrylanguageToAttach);
            }
            countrylanguageCollectionNew = attachedCountrylanguageCollectionNew;
            country.setCountrylanguageCollection(countrylanguageCollectionNew);
            Collection<City> attachedCityCollectionNew = new ArrayList<City>();
            for (City cityCollectionNewCityToAttach : cityCollectionNew) {
                cityCollectionNewCityToAttach = em.getReference(cityCollectionNewCityToAttach.getClass(), cityCollectionNewCityToAttach.getId());
                attachedCityCollectionNew.add(cityCollectionNewCityToAttach);
            }
            cityCollectionNew = attachedCityCollectionNew;
            country.setCityCollection(cityCollectionNew);
            country = em.merge(country);
            for (Countrylanguage countrylanguageCollectionNewCountrylanguage : countrylanguageCollectionNew) {
                if (!countrylanguageCollectionOld.contains(countrylanguageCollectionNewCountrylanguage)) {
                    Country oldCountryOfCountrylanguageCollectionNewCountrylanguage = countrylanguageCollectionNewCountrylanguage.getCountry();
                    countrylanguageCollectionNewCountrylanguage.setCountry(country);
                    countrylanguageCollectionNewCountrylanguage = em.merge(countrylanguageCollectionNewCountrylanguage);
                    if (oldCountryOfCountrylanguageCollectionNewCountrylanguage != null && !oldCountryOfCountrylanguageCollectionNewCountrylanguage.equals(country)) {
                        oldCountryOfCountrylanguageCollectionNewCountrylanguage.getCountrylanguageCollection().remove(countrylanguageCollectionNewCountrylanguage);
                        oldCountryOfCountrylanguageCollectionNewCountrylanguage = em.merge(oldCountryOfCountrylanguageCollectionNewCountrylanguage);
                    }
                }
            }
            for (City cityCollectionNewCity : cityCollectionNew) {
                if (!cityCollectionOld.contains(cityCollectionNewCity)) {
                    Country oldCountryCodeOfCityCollectionNewCity = cityCollectionNewCity.getCountryCode();
                    cityCollectionNewCity.setCountryCode(country);
                    cityCollectionNewCity = em.merge(cityCollectionNewCity);
                    if (oldCountryCodeOfCityCollectionNewCity != null && !oldCountryCodeOfCityCollectionNewCity.equals(country)) {
                        oldCountryCodeOfCityCollectionNewCity.getCityCollection().remove(cityCollectionNewCity);
                        oldCountryCodeOfCityCollectionNewCity = em.merge(oldCountryCodeOfCityCollectionNewCity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = country.getCode();
                if (findCountry(id) == null) {
                    throw new NonexistentEntityException("The country with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Country country;
            try {
                country = em.getReference(Country.class, id);
                country.getCode();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The country with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Countrylanguage> countrylanguageCollectionOrphanCheck = country.getCountrylanguageCollection();
            for (Countrylanguage countrylanguageCollectionOrphanCheckCountrylanguage : countrylanguageCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Country (" + country + ") cannot be destroyed since the Countrylanguage " + countrylanguageCollectionOrphanCheckCountrylanguage + " in its countrylanguageCollection field has a non-nullable country field.");
            }
            Collection<City> cityCollectionOrphanCheck = country.getCityCollection();
            for (City cityCollectionOrphanCheckCity : cityCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Country (" + country + ") cannot be destroyed since the City " + cityCollectionOrphanCheckCity + " in its cityCollection field has a non-nullable countryCode field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(country);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Country> findCountryEntities() {
        return findCountryEntities(true, -1, -1);
    }

    public List<Country> findCountryEntities(int maxResults, int firstResult) {
        return findCountryEntities(false, maxResults, firstResult);
    }

    private List<Country> findCountryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Country.class));
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
    
    

    public Country findCountry(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Country.class, id);
        } finally {
            em.close();
        }
    }

    public int getCountryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Country> rt = cq.from(Country.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
