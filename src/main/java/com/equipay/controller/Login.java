/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.equipay.controller;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author junco
 */
@Named(value = "login")
@SessionScoped
public class Login implements Serializable {

    private String usuario;
    private String contrasenna;
    private boolean logueado = false;

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getContrasenna() { return contrasenna; }
    public void setContrasenna(String contrasenna) { this.contrasenna = contrasenna; }

    public boolean isLogueado() {
        return logueado;
    }

    public String iniciarSesion() {
        if (usuario.equals("admin") && contrasenna.equals("clave123")) {
            this.logueado = true;

            // ***** LÍNEAS ADICIONALES PARA DEPURAR LA SESIÓN EN Login.java *****
            FacesContext currentContext = FacesContext.getCurrentInstance();
            // Obtener la sesión, creando una si no existe (aunque @SessionScoped ya lo debería hacer)
            HttpSession session = (HttpSession) currentContext.getExternalContext().getSession(true); // CAMBIO: Usar getSession(true) para asegurar que la sesión existe o se crea

            if (session != null) {
                // ** LÍNEA CLAVE PARA SOLUCIONAR EL PROBLEMA **
                session.setAttribute("login", this); // Asegurar que el bean 'login' esté en la sesión

                System.out.println("Login [DEBUG]: *** DENTRO DE iniciarSesion() ***");
                System.out.println("Login [DEBUG]: ID de Sesión: " + session.getId());
                System.out.println("Login [DEBUG]: ¿Atributo 'login' en sesión (DESPUÉS de ponerlo)?: " + session.getAttribute("login")); // Verificar que ya no sea null
                System.out.println("Login [DEBUG]: Estado 'logueado' del bean actual: " + this.isLogueado());
            } else {
                System.out.println("Login [DEBUG]: ¡ADVERTENCIA! Sesión es NULA en iniciarSesion(). Esto es inusual, JSF suele crearla.");
            }
            // ********************************************************************************

            return "inicio?faces-redirect=true";
        } else {
            this.logueado = false;
            FacesContext contexto = FacesContext.getCurrentInstance();
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario y/o contraseña inválidos", "MSG_ERROR");
            contexto.addMessage(null, fm);
            return null;
        }
    }

    public String cerrarSesion() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate();
            System.out.println("Login [DEBUG]: Sesión invalidada."); // LOG
        }
        this.logueado = false;
        return "login?faces-redirect=true";
    }

    public Login() {
        // Constructor vacío
    }
}