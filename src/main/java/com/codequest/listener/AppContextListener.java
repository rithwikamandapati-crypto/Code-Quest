package com.codequest.listener;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

// Runs once when Tomcat starts the application
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();

        // ServletContext attributes are shared by ALL users and ALL servlets
        context.setAttribute("appName", "CodeQuest - Programming Learning Game");
        context.setAttribute("loginCount", new AtomicInteger(0));
    }
}