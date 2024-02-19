package edu.pucmm.pw.controllers;

import edu.pucmm.pw.Main;
import edu.pucmm.pw.entidades.RegistroDns;
import edu.pucmm.pw.entidades.Usuario;
import edu.pucmm.pw.services.RegistroDnsServices;
import edu.pucmm.pw.utils.DatosEstaticos;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import static j2html.TagCreator.*;

import java.util.Date;
import java.util.List;

public class RegistroController {

    private static final RegistroDnsServices registroDnsServices = RegistroDnsServices.getInstancia();



    /**
     *
     * @param ctx
     * @throws Exception
     */
    public static void listarRegistrosDns(@NotNull Context ctx) throws Exception{
        ctx.json(registroDnsServices.getListaRegistroDns(Main.Dominio));
    }

    /**
     *
     * @param ctx
     * @throws Exception
     */
    public static void listadoRegistroHtml(@NotNull Context ctx) throws Exception{

        //Retomando
        Usuario usuario = ctx.sessionAttribute(DatosEstaticos.USUARIO.name());

        //
        List<RegistroDns> listaRegistroDns = registroDnsServices.listaRgistroPorUsuario(usuario.getUsername());

        String salida = "";
        for(RegistroDns r: listaRegistroDns){
            salida += crearTrRegistroDns(r);
        }

        ctx.result(salida);
    }

    /**
     *
     * @param ctx
     * @throws Exception
     */
    public static void creacionRegistroDns(@NotNull Context ctx) throws Exception{
        //
        String host = ctx.formParam("host");
        String ip = ctx.formParam("ip");

        //
        Usuario usuario = ctx.sessionAttribute(DatosEstaticos.USUARIO.getValor());

        //
        System.out.println(String.format("Recibiendo host: %s y IP: %s", host, ip));
        RegistroDns reg = new RegistroDns(host,ip, Main.Dominio,usuario.getUsername(),new Date(), 200);
        registroDnsServices.crearRegistroDns(reg);

        //
        ctx.result(crearTrRegistroDns(reg));
    }

    /**
     *
     * @param ctx
     * @throws Exception
     */
    public static void eliminarRegistroDns(@NotNull Context ctx) throws Exception{
        String id = ctx.pathParam("id");
        RegistroDns registroDns = registroDnsServices.eliminarRegistroDns(id);
        ctx.result(crearTrRegistroDns(registroDns));
    }

    public static void editarRegistroDns(@NotNull Context ctx) throws Exception{
        String id = ctx.pathParam("id");
        String ip = ctx.formParam("ip");
        System.out.println(String.format("Editando -> ID: %s, IP: %s", id, ip));
        RegistroDns reg = registroDnsServices.editarRegistroDns(id, ip);
        ctx.result(crearTrRegistroDns(reg));
    }

    /**
     *
     * @param ctx
     * @throws Exception
     */
    public static void formularioCreacion(@NotNull Context ctx) throws Exception{
        String salida = crearTagModalCreacionEdicionRegistroDns(null);
        //
        System.out.println("modal: "+salida);
        //
        ctx.result(salida);
    }


    /**
     *
     * @param ctx
     * @throws Exception
     */
    public static void formularioEdicion(@NotNull Context ctx) throws Exception{
        String id = ctx.pathParam("id");
        RegistroDns reg = registroDnsServices.findByID(id);
        ctx.result(crearTagModalCreacionEdicionRegistroDns(reg));
    }

    /**
     *
     * @param r
     * @return
     */
    @NotNull
    private static String crearTrRegistroDns(RegistroDns r) {
        String registro = tr(
                input().withType("hidden").withValue(r.getId().toString()),
                td(""+ r.getIdDigitalOcean()),
                td(r.getHost()),
                td(r.getIp()),
                td(r.getCreadoPor()),
                td(""+ r.getFechaCreacion()),
                td(
                        button("Editar")
                                .withClasses("btn", "btn-secondary")
                                .attr("hx-get", String.format("/registro/%s",r.getId().toHexString()))
                                .attr("hx-target", "#modal-1")
                                .attr("hx-trigger", "click")
                                .attr("data-bs-toggle", "modal")
                                .attr("data-bs-target", "#modal-1"),
                        button("Eliminar")
                                .withClasses("btn", "btn-danger")
                                .attr("hx-delete",String.format("/registro/%s",r.getId().toHexString()))
                                .attr("hx-trigger", "click")
                                .attr("hx-target", "#reg-"+r.getId().toHexString())
                                .attr("hx-swap", "delete")
                                .attr("hx-confirm", "¿Seguro en eliminar?")
                )
        ).withId("reg-"+r.getId().toHexString())
                .render();

        return registro;
    }





    /**
     *
     * @param reg
     * @return
     */
    private static String crearTagModalCreacionEdicionRegistroDns(RegistroDns reg) {
        String salida = div(
                //div(
                div(
                        form(

                                div(
                                        h5(reg == null ? "Creacion de Registro" : "Edicion de Registro").withClass("modal-title"),
                                        button()
                                                .attr("type", "button")
                                                .attr("class","btn-close")
                                                .attr("data-bs-dismiss","modal")
                                                .attr("data-bs-target","#modal-1")
                                                .attr("aria-label","Close")
                                ).withClass("modal-header"),
                                div(
                                        div(
                                                label("Host")
                                                        .withClass("form-label"),
                                                input()
                                                        .withType("text")
                                                        .withName("host")
                                                        .withCondRequired(true)
                                                        .withPlaceholder("Host")
                                                        .withClass("form-control")
                                                        .withCondReadonly(reg !=null) //si viene con valor es solo lectura.
                                                        .withCondValue(reg !=null, reg!=null ? reg.getHost() : "")
                                        ).withClass("mb-3"),

                                        div(
                                                label("IP")
                                                        .withClass("form-label"),
                                                input()
                                                        .withType("text")
                                                        .withName("ip")
                                                        .withCondRequired(true)
                                                        .withPlaceholder("Host")
                                                        .withClass("form-control")
                                                        .withCondValue(reg !=null, reg!=null ? reg.getIp() : "")
                                        ).withClass("mb-3")

                                ).withClass("modal-body"),
                                div(
                                        button("Procesar")
                                                .attr("type", "submit")
                                                .attr("class","btn btn-primary")
                                                .attr("data-bs-dismiss","modal")

                                ).withClass("modal-footer")
                        )
                                //Un endpoint para la creación y otro para la edición.
                                .condAttr(reg == null,"hx-post","/registro")
                                .condAttr(reg != null,"hx-put",reg!= null ? "/registro/"+reg.getId().toHexString() : "")
                                // Si es creando, agrego el elemento al inicio de la tabla.
                                .condAttr(reg == null,"hx-target","#filas")
                                // De lo contrario reemplazo el id, con el valor modificado.
                                .condAttr(reg != null,"hx-target",reg!= null ? "#reg-"+reg.getId().toHexString() : "")
                                // Si es creando, agrego al inicio.
                                .condAttr(reg == null,"hx-swap","afterbegin")
                                // De lo contrario reemplazo con la actualización
                                .condAttr(reg != null,"hx-swap","outerHTML")
                ).withClass("modal-content")
                // ).withClass("modal-dialog")
        ).withClass("modal-dialog modal-dialog-centered").renderFormatted();
        return salida;
    }

}
