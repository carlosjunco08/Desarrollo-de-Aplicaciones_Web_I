package com.equipay.security;

import com.equipay.controller.Login;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author junco
 */
public class Filtro implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Método de inicialización del filtro (puede dejarse vacío si no se necesita)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath(); // Por ejemplo, /Equipay
        System.out.println("Filtro [DEBUG]: Interceptando URI: " + requestURI); // LOG
        System.out.println("Filtro [DEBUG]: Context Path: " + contextPath); // LOG

        // === CAMBIO IMPORTANTE AQUÍ ===
        // Permitimos acceso a:
        // 1. La página de login (login.xhtml)
        // 2. Recursos estáticos de JSF (CSS, JS, imágenes manejados por JSF)
        // 3. La URI raíz de la aplicación (e.g., /Equipay/ o /Equipay)
        if (requestURI.endsWith("/login.xhtml") ||
            requestURI.contains("/javax.faces.resource/") ||
            requestURI.equals(contextPath + "/") || // Para la URL con barra final (e.g., /Equipay/)
            requestURI.equals(contextPath) // Para la URL sin barra final (e.g., /Equipay)
           ) {
            System.out.println("Filtro [DEBUG]: Permitiendo acceso a login, recursos estáticos o URI raíz: " + requestURI); // LOG
            chain.doFilter(request, response);
        } else {
            // Lógica de autenticación para páginas protegidas
            HttpSession session = req.getSession(false);
            Login loginBean = null;

            if (session != null) {
                System.out.println("Filtro [DEBUG]: ID de Sesión del filtro: " + session.getId()); // LOG
                loginBean = (Login) session.getAttribute("login");
                System.out.println("Filtro [DEBUG]: Sesión existente. ¿Bean 'login' en sesión?: " + (loginBean != null)); // LOG
                if (loginBean != null) {
                    System.out.println("Filtro [DEBUG]: Estado 'logueado' del bean 'login': " + loginBean.isLogueado()); // LOG
                }
            } else {
                System.out.println("Filtro [DEBUG]: No se encontró ninguna sesión."); // LOG
            }

            if (session != null && loginBean != null && loginBean.isLogueado()) {
                System.out.println("Filtro [DEBUG]: Usuario logueado. Procediendo a: " + requestURI); // LOG
                res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                res.setHeader("Pragma", "no-cache");
                res.setDateHeader("Expires", 0);
                chain.doFilter(request, response);
            } else {
                System.out.println("Filtro [DEBUG]: Usuario NO logueado o sesión inválida. Redirigiendo a login.xhtml desde: " + requestURI); // LOG
                res.sendRedirect(req.getContextPath() + "/login.xhtml");
            }
        }
    }

    @Override
    public void destroy() {
        // ...
    }
}