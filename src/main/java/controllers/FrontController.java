package controllers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import actions.ActionBase;
import actions.UnknownAction;
import constants.ForwardConst;

@WebServlet(name="FrontController", urlPatterns={"/"})
public class FrontController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public FrontController() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ActionBase action = getAction(request,response);

        action.init(getServletContext(),request,response);

        action.process();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    private ActionBase getAction(HttpServletRequest request,HttpServletResponse response ) {
        Class type = null;
        ActionBase action = null;

        try {
            String actionString = request.getParameter(ForwardConst.ACT.getValue());

            type = Class.forName(String.format("actions.%sAction", actionString));

            action = (ActionBase)(type.asSubclass(ActionBase.class)
                        .getDeclaredConstructor()
                        .newInstance());

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SecurityException
                |IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
            action  = new UnknownAction();
        }

        return action;
    }

}
