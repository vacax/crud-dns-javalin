package edu.pucmm.pw.services;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import edu.pucmm.pw.entidades.RegistroDns;
import edu.pucmm.pw.entidades.Usuario;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
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
        //System.out.println("Autenticando Usuario "+usuario+", "+password);
        Datastore conexionMorphia = getConexionMorphia();
        Query<Usuario> query = conexionMorphia.find(Usuario.class).filter(Filters.and(Filters.eq("username", usuario), Filters.eq("password", password)));
        if(query.count() == 0){
            System.out.println("Usuario y contraseña no coinciden");

            return null;
        }

        return query.first();
    }

    /**
     *
     * @return
     */
    public List<Usuario> listaUsuarios(){
        Datastore con = getConexionMorphia();
        return con.find(Usuario.class).iterator().toList();
    }

    /**
     *
     * @param id
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public Usuario eliminarUsuario(String id) throws IOException, InterruptedException {
        Usuario reg = findByID(id);
        deleteById(reg.getId().toHexString());
        return reg;
    }

    /**
     *
     * @param id
     * @param password
     * @return
     */
    public Usuario editarUsuario(String id, String password) throws IOException, InterruptedException {
        Usuario reg = null;


        long modifiedCount = getConexionMorphia().find(Usuario.class)
                .filter(Filters.eq("_id", new ObjectId(id)))
                .update(UpdateOperators.set("password", password))
                .execute().getModifiedCount();
        if(modifiedCount > 0){
            System.out.println("Usuario actualizado");
            reg = findByID(id);
        }

        return reg;
    }



}
