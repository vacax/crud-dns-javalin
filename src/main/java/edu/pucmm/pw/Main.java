package edu.pucmm.pw;

import edu.pucmm.pw.controllers.RegistroController;
import edu.pucmm.pw.entidades.Usuario;
import edu.pucmm.pw.services.UsuarioServices;
import edu.pucmm.pw.utils.DatosEstaticos;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.HashMap;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static String Dominio; //TODO: reubicar en otra clase de configuracion

    public static void main(String[] args) {

        Dominio = System.getenv(DatosEstaticos.CRUD_DNS_JAVALIN_DOMINIO.getValor());
        if(Dominio==null){
            System.out.println("Debe indicar el dominio de la aplicación vía variable de ambiente $CRUD_DNS_JAVALIN_DOMINIO");
            System.exit(-1);
        }

        var render = new JavalinThymeleaf();

        var app = Javalin.create(javalinConfig -> {
                    // Definiendo los archivo public
                    javalinConfig.staticFiles.add("/publico");

                    // Habilitando los Webjars.
                    javalinConfig.staticFiles.enableWebjars();

                    //En la versión 6, cambio la forma de registrar los sistemas de plantilla.
                    // ver en https://javalin.io/migration-guide-javalin-5-to-6
                    javalinConfig.fileRenderer(render);

                    //Rutas para API
                    javalinConfig.router.apiBuilder(() -> {
                        path("/registro", () -> {

                            //Consulta generales
                            get("/db",RegistroController::creacionRegistroDns);
                            get("/lista",RegistroController::listadoRegistroHtml);
                            get("/crear", RegistroController::formularioCreacion);
                            post(RegistroController::creacionRegistroDns);

                            //Consulta de un registro
                            path("/{id}",() -> {
                                get(RegistroController::formularioEdicion);
                                delete("",RegistroController::eliminarRegistroDns);
                                put(RegistroController::editarRegistroDns);
                            });
                        });
                    });
                })
                .before(ctx -> {
                    //validando si existe el usuario logueado.
                    System.out.println("Logueado: "+DatosEstaticos.USUARIO.name());
                    Usuario usuario = ctx.sessionAttribute(DatosEstaticos.USUARIO.name());
                    /**
                     * Si, el usuario no está en la sesión, y la vista no es login.html y no es el endpoint de autenticar,
                     * lo mando al login.html, lo contrario continuamos con la peticion.
                     */
                    if(usuario== null && !(ctx.path().contains("login.html") || ctx.path().contains("/autenticar"))){
                        ctx.redirect("/login.html");
                    }
                })
                /**
                 * Para nuestro ejemplo no importa los valores recibido, lo estaremos validando.
                 */
                .post("/autenticar", ctx -> {
                    //
                    String username = ctx.formParam("username");
                    String password = ctx.formParam("password");
                    //
                    Usuario usuario = UsuarioServices.getInstancia().autenticacion(username, password);
                    ctx.sessionAttribute(DatosEstaticos.USUARIO.getValor(), usuario);
                    //
                    ctx.redirect("/");
                })

                //Enviando al template
                .get("/", ctx -> {
                    Map<String, Object> modelo = new HashMap<>();
                    modelo.put("titulo", "Registro DNS DigitalOcean - "+Dominio);
                    ctx.render("/templates/index.html", modelo);
                })
                .start(7000);
    }
}