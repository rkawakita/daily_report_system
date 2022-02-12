package actions;

import java.io.IOException;

import javax.servlet.ServletException;

import constants.AttributeConst;
import constants.ForwardConst;

public class TopAction extends ActionBase{

    @Override
    public void process() throws ServletException , IOException {

        invoke();


    }

    public void index() throws ServletException,IOException  {
        String flush = getSessionScope(AttributeConst.FLUSH);

        if(flush != null) {
            putRequestScope(AttributeConst.FLUSH,flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        forward(ForwardConst.FW_TOP_INDEX);
    }
}
