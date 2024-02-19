package edu.pucmm.pw.services;

import com.google.gson.Gson;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import edu.pucmm.pw.dtos.RegistroDnsDigitalOcean;
import edu.pucmm.pw.entidades.RegistroDns;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class RegistroDnsServices extends GestionDB<RegistroDns> {

    private static RegistroDnsServices instancia;

    private RegistroDnsServices(){
        super(RegistroDns.class);
    }

    public static RegistroDnsServices getInstancia() {
        if(instancia == null){
            instancia = new RegistroDnsServices();
        }
        return instancia;
    }

    /**
     *
     * @param reg
     * @return
     */
    public boolean crearRegistroDns(RegistroDns reg) throws IOException, InterruptedException {

        //valiando la Ip
        InetAddressValidator validator = InetAddressValidator.getInstance();
        if(!validator.isValid(reg.getIp())){
            throw new RuntimeException("IP no valida!");
        }

        //
        String comando = String.format("doctl compute domain records create --output json --record-type A --record-name %s --record-ttl 200 --record-data %s %s ",
                reg.getHost(),reg.getIp(), reg.getDominio());
        //
        Process exec = Runtime.getRuntime().exec(comando);
        String salida = getResultadoComando(exec);

        // Esperar a que termine el proceso
        int exitCode = exec.waitFor();
        System.out.println("El comando terminó con el código de salida: " + exitCode);

        //Pasando salida a json
        Gson gson = new Gson();
        RegistroDnsDigitalOcean[] registroDnsDigitalOcean = gson.fromJson(salida, RegistroDnsDigitalOcean[].class);
        System.out.println("Desde el JSON: "+ Arrays.toString(registroDnsDigitalOcean));

        if(exitCode ==0 ){
            reg.setIdDigitalOcean(Arrays.stream(registroDnsDigitalOcean).findFirst().orElseThrow().id());
            crear(reg);
        }

        return true;
    }

    /**
     *
     * @param dominio
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public List<RegistroDnsDigitalOcean> getListaRegistroDns(String dominio) throws IOException, InterruptedException{
        //recuperando el
        String comando = String.format("doctl compute domain records -o json list %s", dominio);
        //
        Process exec = Runtime.getRuntime().exec(comando);
        String salida = getResultadoComando(exec);

        // Esperar a que termine el proceso
        int exitCode = exec.waitFor();
        System.out.println("El comando terminó con el código de salida: " + exitCode);

        //Pasando salida a json
        Gson gson = new Gson();
        RegistroDnsDigitalOcean[] registroDnsDigitalOcean = gson.fromJson(salida, RegistroDnsDigitalOcean[].class);

        //retornando
        return Arrays.asList(registroDnsDigitalOcean);
    }

    /**
     *
     * @param id
     * @param ip
     * @return
     */
    public RegistroDns editarRegistroDns(String id, String ip) throws IOException, InterruptedException {
        RegistroDns reg = null;

        //valiando la Ip
        InetAddressValidator validator = InetAddressValidator.getInstance();
        if(!validator.isValid(ip)){
            throw new RuntimeException("IP no valida!");
        }

        long modifiedCount = getConexionMorphia().find(RegistroDns.class)
                .filter(Filters.eq("_id", new ObjectId(id)))
                .update(UpdateOperators.set("ip", ip))
                .execute().getModifiedCount();
        if(modifiedCount > 0){
            System.out.println("Registro actualizado");
            reg = findByID(id);
            editarRegistroDigitalOcean(reg);
        }

        return reg;
    }

    /**
     *
     * @param id
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public RegistroDns eliminarRegistroDns(String id) throws IOException, InterruptedException {
        RegistroDns reg = findByID(id);
        eliminarRegistroDigitalOcean(reg);
        deleteById(reg.getId().toHexString());
        return reg;
    }

    /**
     *
     * @param reg
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean eliminarRegistroDigitalOcean(RegistroDns reg) throws IOException, InterruptedException{
        //recuperando el
        String comando = String.format("doctl compute domain records delete -f -o json %s %d", reg.getDominio(), reg.getIdDigitalOcean());
        //
        Process exec = Runtime.getRuntime().exec(comando);
        String salida = getResultadoComando(exec);

        // Esperar a que termine el proceso
        int exitCode = exec.waitFor();
        System.out.println("El comando terminó con el código de salida: " + exitCode);

        return exitCode == 0;
    }

    /**
     *
     * @param reg
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean editarRegistroDigitalOcean(RegistroDns reg) throws IOException, InterruptedException{
        //recuperando el
        String comando = String.format("doctl compute domain records update -o json %s --record-id %s --record-data %s", reg.getDominio(), reg.getIdDigitalOcean(), reg.getIp());
        //
        Process exec = Runtime.getRuntime().exec(comando);
        String salida = getResultadoComando(exec);

        // Esperar a que termine el proceso
        int exitCode = exec.waitFor();
        System.out.println("El comando terminó con el código de salida: " + exitCode);

        return exitCode == 0;
    }

    /**
     *
     * @param exec
     * @return
     * @throws IOException
     */
    @NotNull
    private static String getResultadoComando(Process exec) throws IOException {
        // Obtener el resultado
        BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        String linea;
        String salida = "";
        while ((linea = reader.readLine()) != null) {
            salida += linea;
            System.out.println(linea);
        }
        return salida;
    }

    /**
     *
     * @param usuario
     * @return
     */
    public List<RegistroDns> listaRgistroPorUsuario(String usuario){
        Datastore con = getConexionMorphia();
        return con.find(RegistroDns.class).filter(Filters.eq("creadoPor", usuario)).iterator().toList();
    }
}
