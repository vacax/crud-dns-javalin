package edu.pucmm.pw.services;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import edu.pucmm.pw.entidades.RegistroDns;
import edu.pucmm.pw.entidades.Usuario;
import org.bson.Document;

import java.util.List;

public class UsuarioServices extends GestionDB<Usuario>{

    private static UsuarioServices instancia;

    private UsuarioServices() {
        super(Usuario.class);
    }

    public static UsuarioServices getInstancia(){
        if(instancia == null){
            instancia =new UsuarioServices();
        }
        return instancia;
    }

    /**
     *
     * @param usuario
     * @param password
     * @return
     */
    public Usuario autenticacion(String usuario, String password){
        System.out.println("Autenticando Usuario "+usuario+", "+password);
        Datastore conexionMorphia = getConexionMorphia();
        Query<Usuario> query = conexionMorphia.find(Usuario.class).filter(Filters.and(Filters.eq("username", usuario), Filters.eq("password", password)));
        if(query.count() == 0){
            System.out.println("Usuario y contraseña no coinciden");

            return null;
        }

        return query.first();
    }




}
