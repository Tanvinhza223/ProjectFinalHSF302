package huynhtanvinh.projectfinalhsf302.interceptor;

import huynhtanvinh.projectfinalhsf302.model.UserAccount;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
   @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       String uri = request.getRequestURI();
      if (uri.equals("/login")||uri.equals("/")||uri.startsWith("/err")) {
          return true;
      }
      if (uri.startsWith("/css/")||uri.startsWith("/js/")||uri.startsWith("/images/")) {
          return true;
      }
       HttpSession session = request.getSession();
       UserAccount user = (UserAccount) session.getAttribute("user");
       if (user == null) {
           response.sendRedirect("/");
           return false;
       }
       return true;
   }
}


