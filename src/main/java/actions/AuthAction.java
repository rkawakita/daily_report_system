package actions;

import java.io.IOException;

import javax.servlet.ServletException;

import constants.AttributeConst;
import constants.ForwardConst;
import services.EmployeeService;

public class AuthAction extends ActionBase{

    private EmployeeService service;

    @Override
    public void process() throws ServletException,IOException {

        service = new EmployeeService();

        invoke();

        service.close();
    }

    public void showLogin() throws ServletException,IOException {
        putRequestScope(AttributeConst.TOKEN,getTokenId());

        String flush = getSessionScope(AttributeConst.FLUSH);
        if(flush != null) {
            putRequestScope(AttributeConst.FLUSH,flush);
        }

        forward(ForwardConst.FW_LOGIN);
    }



}
