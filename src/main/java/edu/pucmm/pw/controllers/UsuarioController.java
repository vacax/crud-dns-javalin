package edu.pucmm.pw.controllers;

import edu.pucmm.pw.entidades.RegistroDns;
import edu.pucmm.pw.entidades.Usuario;
import edu.pucmm.pw.services.UsuarioServices;
import edu.pucmm.pw.utils.DatosEstaticos;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static j2html.TagCreator.*;
import static j2html.TagCreator.button;

public class UsuarioController {

    private static final UsuarioServices usuarioServices = UsuarioServices.getInstancia();

    public static void listadoRegistroHtml(@NotNull Context ctx) throws Exception{

        //Retomando
        Usuario usuario = ctx.sessionAttribute(DatosEstaticos.USUARIO.name());

        //
        List<Usuario> listaUsuarios = usuarioServices.listaUsuarios();

        String salida = "";
        for(Usuario r: listaUsuarios){
            salida += crearTrUsuario(r);
        }

        ctx.result(salida);
    }

    /**
     *
     * @param ctx
     * @throws Exception
     */
    public static void formularioCreacion(@NotNull Context ctx) throws Exception{
        String salida = crearTagModalCreacionEdicionUsuario(null);
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
    public static void creacionUsuario(@NotNull Context ctx) throws Exception{
        //
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        //
        //Usuario usuario = ctx.sessionAttribute(DatosEstaticos.USUARIO.getValor());

        //
        System.out.println(String.format("Recibiendo username: %s y IP: %s", username, password));
        Usuario usuario = new Usuario(username, password);
        usuarioServices.crear(usuario);

        //
        ctx.result(crearTrUsuario(usuario));
    }

    /**
     *
     * @param ctx
     * @throws Exception
     */
    public static void eliminarUsuario(@NotNull Context ctx) throws Exception{
        String id = ctx.pathParam("id");
        Usuario usuario = usuarioServices.eliminarUsuario(id);
        ctx.result(crearTrUsuario(usuario));
    }

    /**
     *
     * @param ctx
     * @throws Exception
     */
    public static void formularioEdicion(@NotNull Context ctx) throws Exception{
        String id = ctx.pathParam("id");
        Usuario reg = usuarioServices.findByID(id);
        ctx.result(crearTagModalCreacionEdicionUsuario(reg));
    }

    public static void editarUsuario(@NotNull Context ctx) throws Exception{
        String id = ctx.pathParam("id");
        String password = ctx.formParam("password");
        System.out.println(String.format("Editando -> ID: %s, IP: %s", id, password));
        Usuario reg = usuarioServices.editarUsuario(id, password);
        ctx.result(crearTrUsuario(reg));
    }

    /**
     *
     * @param ctx
     * @throws Exception
     */
    public static void listadoBotonesPermisos(@NotNull Context ctx) throws Exception{
        Usuario usuario = ctx.sessionAttribute(DatosEstaticos.USUARIO.getValor());
        String salida = "";
        salida += button("Crear Usuario")
                .withType("button")
                .withClasses("btn", "btn-primary")
                .attr("hx-get", "/usuario/crear")
                .attr("hx-target", "#modal-1")
                .attr("hx-trigger", "click")
                .attr("data-bs-toggle", "modal")
                .attr("data-bs-target", "#modal-1")
                .render();
        if(usuario.isAdministrador()){
            salida+=a("Registro DNS")
                    .withHref("/")
                    .withClasses("btn", "btn-dark")
                    .render();
        }
        salida+=a("Salida")
                .withHref("/logout")
                .withClasses("btn", "btn-dark")
                .render();

        ctx.result(salida);

    }

    @NotNull
    private static String crearTrUsuario(Usuario r) {
        String registro = tr(
                input().withType("hidden").withValue(r.getId().toString()),
                td(""+ r.getUsername()),
                td(r.getPassword()),
                td(""+r.isAdministrador()),
                td(
                        button("Editar")
                                .withClasses("btn", "btn-secondary")
                                .attr("hx-get", String.format("/usuario/%s",r.getId().toHexString()))
                                .attr("hx-target", "#modal-1")
                                .attr("hx-trigger", "click")
                                .attr("data-bs-toggle", "modal")
                                .attr("data-bs-target", "#modal-1"),
                        button("Eliminar")
                                .withClasses("btn", "btn-danger")
                                .attr("hx-delete",String.format("/usuario/%s",r.getId().toHexString()))
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
    private static String crearTagModalCreacionEdicionUsuario(Usuario reg) {
        String salida = div(
                //div(
                div(
                        form(

                                div(
                                        h5(reg == null ? "Creacion de Usuario" : "Edicion de Usuario").withClass("modal-title"),
                                        button()
                                                .attr("type", "button")
                                                .attr("class","btn-close")
                                                .attr("data-bs-dismiss","modal")
                                                .attr("data-bs-target","#modal-1")
                                                .attr("aria-label","Close")
                                ).withClass("modal-header"),
                                div(
                                        div(
                                                label("Usuario")
                                                        .withClass("form-label"),
                                                input()
                                                        .withType("text")
                                                        .withName("username")
                                                        .withCondRequired(true)
                                                        .withPlaceholder("Nombre Usuario")
                                                        .withClass("form-control")
                                                        .withCondReadonly(reg !=null) //si viene con valor es solo lectura.
                                                        .withCondValue(reg !=null, reg!=null ? reg.getUsername() : "")
                                        ).withClass("mb-3"),

                                        div(
                                                label("Password")
                                                        .withClass("form-label"),
                                                input()
                                                        .withType("password")
                                                        .withName("password")
                                                        .withCondRequired(true)
                                                        .withPlaceholder("Password")
                                                        .withClass("form-control")
                                                        .withCondValue(reg !=null, reg!=null ? reg.getPassword() : "")
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
                                .condAttr(reg == null,"hx-post","/usuario")
                                .condAttr(reg != null,"hx-put",reg!= null ? "/usuario/"+reg.getId().toHexString() : "")
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
