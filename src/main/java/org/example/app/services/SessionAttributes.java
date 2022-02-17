package org.example.app.services;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;

public class SessionAttributes {

    public static Object getSessionAttribute(@NotNull HttpServletRequest request, String attributeName) {
        return request.getSession().getAttribute(attributeName);
    }
}
