package com.webank.wedatasphere.qualitis.interceptor;

import com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.HttpRequestUserInterceptor;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.qualitis.common.Constants;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.service.LoginService;
import com.webank.wedatasphere.qualitis.service.UserService;
import com.webank.wedatasphere.qualitis.util.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


public class WTSSHttpRequestUserInterceptor implements HttpRequestUserInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WTSSHttpRequestUserInterceptor.class.getName());

    @Autowired
    private UserDao userDao;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    public WTSSHttpRequestUserInterceptor(){
    }

    @Override
    public HttpServletRequest addUserToRequest(String username, HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().setAttribute("username", username);
        return createDssToken(username, httpServletRequest);
    }

    private HttpServletRequest createDssToken(final String username, final HttpServletRequest req) {
        logger.info(username + " enters the createDssToken method ,and ignore operation.");

        String userTicketId = CookieUtils.getCookieValue(req, Constants.USER_TICKET_ID_STRING);

        final HttpServletRequestWrapper httpServletRequestWrapper = new HttpServletRequestWrapper(req);
        logger.info("qualitis userTicketId {} for user {} .", userTicketId,username);
        return httpServletRequestWrapper;
    }

    @Override
    public boolean isUserExistInSession(HttpServletRequest httpServletRequest) {
        String userTicket = CookieUtils.getCookieValue(httpServletRequest,Constants.USER_TICKET_ID_STRING);
        logger.info("dss userTicket {} ", userTicket);
        if (userTicket != null && !userTicket.isEmpty()) {
            String username = SecurityFilter.getLoginUsername(httpServletRequest);
            if(username == null){
                return false;
            }
            if(!username.equals(getUser(httpServletRequest))){
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public String getUser(HttpServletRequest httpServletRequest) {
        Object username = httpServletRequest.getSession().getAttribute("username");
        return username == null?null:username.toString();
    }

    private void loginByUser(String username, HttpServletRequest request) {
        // 查询数据库，看用户是否存在
        User userInDb = userDao.findByUsername(username);
        if (userInDb != null) {
            // 放入session
            logger.info("User: {} succeed to login", username);
            loginService.addToSession(username, request);
        } else {
            // 自动创建用户
            logger.warn("user: {}, do not exist, trying to create user", username);
            try {
                userService.autoAddUser(username);
                loginService.addToSession(username, request);
            } catch (RoleNotFoundException e) {
                logger.error("Failed to auto add user, cause by: Failed to get role [PROJECTOR]", e);
            }
        }

    }
}
