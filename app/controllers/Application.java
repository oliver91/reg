package controllers;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;
import views.html.login;
import views.html.registration;

public class Application extends Controller
{
    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(index.render(User.find.byId(request().username())));

    }
    public static Result login() {
        return ok(login.render(Form.form(Login.class))
        );
    }
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
                routes.Application.login()
        );
    }
    public static Result authenticate()
    {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("email", loginForm.get().email);
            return redirect(
                    routes.Application.index()
            );
        }
    }

    public static Result registration()
    {
        return ok(registration.render(Form.form(Registration.class)));
    }

    public static Result addUser()
    {
        Form<Registration> reg_form = Form.form(Registration.class).bindFromRequest();
        new User(reg_form.get().email, reg_form.get().name, reg_form.get().password).save();
        authenticate();
        return redirect(routes.Application.index());
    }

    public static class Login
    {
        public String email;
        public String password;


        public String validate()
        {
            if (User.authenticate(email, password) == null) {
                return "Invalid user or password";
            }
            return null;
        }
    }

    public static class Registration
    {
        public String name;
        public String email;
        public String password;

//        public static void addUser()
//        {
//
//        }
    }
}