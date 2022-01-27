package actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constants.AttributeConst;
import constants.ForwardConst;
import constants.PropertyConst;

public abstract class ActionBase {

    protected ServletContext context;
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    public void init(
            ServletContext servletContext,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        this.context = servletContext;
        this.request = servletRequest;
        this.response = servletResponse;
    }

    /**
     * フロントコントローラから呼び出されるメソッド
     * @throws ServletException
     * @throws IOException
     */

    public abstract void process() throws ServletException,IOException;

    /**
     * パラメータのcommandの値に該当するメソッドを実行する
     * @throws ServletException
     * @throws IOException
     */
    protected void invoke()
        throws ServletException,IOException {
        Method commandMethod;

        try {

            String command = request.getParameter(ForwardConst.CMD.getValue());

            //ommandに該当するメソッドを実行する
            //(例: action=Employee command=show の場合 EmployeeActionクラスのshow()メソッドを実行する)
            commandMethod = this.getClass().getDeclaredMethod(command, new Class[0]);
            commandMethod.invoke(this,new Object[0]);

        } catch ( NoSuchMethodException
                | SecurityException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException
                | NullPointerException e) {
            e.printStackTrace();
            forward(ForwardConst.FW_ERR_UNKNOWN);
        }
    }

    protected void forward(ForwardConst target) throws ServletException, IOException {

        String forward = String.format("/WEB-INF/views/%s.jsp" , target.getValue());
        RequestDispatcher dispatcher = request.getRequestDispatcher(forward);

        dispatcher.forward(request,response);
    }

    protected void redirect(ForwardConst action,ForwardConst command)
        throws ServletException,IOException {

        String redirectUrl = request.getContextPath() + "/?action=" + action.getValue();
        if (command != null ) {
            redirectUrl = redirectUrl + "&command=" + command.getValue();
        }

        response.sendRedirect(redirectUrl);

    }

    protected boolean checkToken() throws ServletException, IOException {

        String _token = getRequestParam(AttributeConst.TOKEN);

        if (_token == null || !(_token.equals(getTokenId()))){
            forward(ForwardConst.FW_ERR_UNKNOWN);

            return false;

        } else {
            return true;
        }
    }

    protected String getTokenId() {
        return request.getSession().getId();
    }

    protected int getPage() {
        int page;
        page = toNumber(request.getParameter(AttributeConst.PAGE.getValue()));
        if ( page == Integer.MIN_VALUE) {
            page = 1;
        }
        return page;
    }

    protected int toNumber(String strNumber) {
        int number = 0;

        try {
            number = Integer.parseInt(strNumber);
        } catch (Exception e) {
            number = Integer.MIN_VALUE;
        }
        return number;
    }

    /**
     * 文字列をLocalDate型に変換する
     * @param strDate 変換前文字列
     * @return 変換後LocalDateインスタンス
     */
    protected LocalDate toLocalDate(String strDate) {
        if (strDate == null || strDate.equals("")) {
            return LocalDate.now();
        }

        return LocalDate.parse(strDate);
    }

    /**
     * リクエストスコープから指定されたパラメータの値を取得し、返却する
     * @param key パラメータ名
     * @return パラメータの値
     */
    protected String getRequestParam(AttributeConst key) {
        return request.getParameter(key.getValue());
    }

    /**
     * リクエストスコープにパラメータを設定する
     * @param key パラメータ名
     * @param value パラメータの値
     */
    protected <V> void putRequestScope(AttributeConst key,V value) {
        request.setAttribute(key.getValue(),value);
    }

    /**
     * セッションスコープから指定されたパラメータの値を取得し、返却する
     * @param key パラメータ名
     * @return パラメータの値
     */
    @SuppressWarnings("unchecked")
    protected <R> R getSessionScope(AttributeConst key) {
        return (R) request.getSession().getAttribute(key.getValue());
    }

    /**
     * セッションスコープにパラメータを設定する
     * @param key パラメータ名
     * @param value パラメータの値
     */
    protected <V> void putSessionScope(AttributeConst key,V value) {
        request.getSession().setAttribute(key.getValue(),value);
    }

    /**
     * セッションスコープから指定された名前のパラメータを除去する
     * @param key パラメータ名
     */
    protected void removeSessionScope(AttributeConst key) {
        request.getSession().removeAttribute(key.getValue());
    }

    /**
     * アプリケーションスコープから指定されたパラメータの値を取得し、返却する
     * @param key パラメータ名
     * @return パラメータの値
     */
    @SuppressWarnings("unchecked")
    protected <R> R getContextScope(PropertyConst key) {
        return (R) context.getAttribute(key.getValue());
    }



}
