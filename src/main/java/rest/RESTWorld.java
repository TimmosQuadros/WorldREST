/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import com.google.gson.Gson;
import com.sun.org.glassfish.gmbal.ParameterNames;
import entity.City;
import entity.CityJpaController;
import entity.Country;
import entity.CountryJpaController;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author TimmosQuadros
 */
@Path("world")
public class RESTWorld {

    private CountryJpaController cjpa;
    private CityJpaController cityjpa;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of RESTWorld
     */
    public RESTWorld() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_RESTAndJSON_war_1.0-SNAPSHOTPU");
        cjpa = new CountryJpaController(emf);
        cityjpa = new CityJpaController(emf);
    }

    /**
     * Retrieves representation of an instance of rest.RESTWorld
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public String getAllCountries() {
        String JSON = "{countries: [";
        List<Country> countries = cjpa.findCountryEntities();
        List<City> cities = cityjpa.findCityEntities();
        for (Country country : countries) {
            JSON += "{\"code\": " + country.getCode() + ",";
            JSON += "\"name\": " + country.getName() + ",";
            JSON += "\"continent\": " + country.getContinent() + ",";
            if (country.getCapital() != null) {
                JSON += "\"capital\": " + cities.get(country.getCapital() - 1).getName() + "},";
            } else {
                JSON += "\"capital\": " + country.getCapital() + "},";
            }
        }
        JSON = JSON.substring(0, JSON.length() - 1);
        JSON += "]}";
        return JSON;
    }

    /**
     * Retrieves representation of an instance of rest.RESTWorld
     *
     * @param population
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all/{population}")
    public String getAllCountriesWithPopulationGreater(@PathParam("population") int population) {
        String JSON = "{countries: [";
        List<Country> countries = cjpa.findCountryEntities();
        List<City> cities = cityjpa.findCityEntities();
        for (Country country : countries) {
            if (country.getPopulation() > population) {
                JSON += "{\"code\": " + country.getCode() + ",";
                JSON += "\"name\": " + country.getName() + ",";
                JSON += "\"continent\": " + country.getContinent() + ",";
                if (country.getCapital() != null) {
                    JSON += "\"capital\": " + cities.get(country.getCapital() - 1).getName() + "},";
                } else {
                    JSON += "\"capital\": " + country.getCapital() + "},";
                }
            }
        }
        JSON = JSON.substring(0, JSON.length() - 1);
        JSON += "]}";
        return JSON;
    }
    
    /**
     *
     * @param countrycode
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all/cities/{countrycode}")
    public String getListOfCitiesInCountry(@PathParam("countrycode") String countrycode) {
        List<City> cities = cityjpa.findCityEntities();
        String JSON = "{cities: [";
        for (City city : cities) {
            if(city.getCountryCode().getCode().equalsIgnoreCase(countrycode)){
                JSON += "{\"name\": " + city.getName() + ",";
                JSON += "\"population\": " + city.getPopulation() + ",";
            }
        }
        JSON = JSON.substring(0, JSON.length() - 1);
        JSON += "]}";
        return JSON;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postCity(String content) 
    {
        City city = new Gson().fromJson(content, City.class);
        Country country = cjpa.findCountry(city.getCountryCode().getCode());
        country.setPopulation(country.getPopulation()+city.getPopulation()); //update the population
        cityjpa.create(city);
        return new Gson().toJson(city, City.class);
    }
    
    /**
     * PUT method for updating or creating an instance of RESTWorld
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
}
