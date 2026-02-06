package com.framework;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;

import com.exceptions.*;
import com.utils.*;
@MultipartConfig
public class FrontServlet extends HttpServlet {
    private RequestDispatcher defaultDispatcher;
    
    @Override
    public void init() throws ServletException {
        try {
            ServletContext ctx = getServletContext();
            defaultDispatcher = ctx.getNamedDispatcher("default");
            String uploadDir = PropertiesUtil.get("framework.upload.dir");
            if (uploadDir == null || uploadDir.isEmpty()) {
                uploadDir = "uploads";
            }
            String uploadBase =
                ctx.getRealPath("/") + File.separator + uploadDir;
            FileStorage.init(uploadBase);
            System.out.println("Upload folder initialized at: " + uploadBase);
            String pkg = PropertiesUtil.get("framework.scan.package");
            if (pkg == null || pkg.isEmpty()) {
                pkg = "tests";
            }
            Map<String,List<MappingHandler>> urlMappings =
                ScanningUrl.scanUrlMappings(pkg);
            ctx.setAttribute("urlMappings", urlMappings);
        } catch(Exception e){
            throw new ServletException(e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            String path = req.getRequestURI()
                            .substring(req.getContextPath().length());
            @SuppressWarnings("unchecked")
            Map<String, List<MappingHandler>> urlMappings =
                (Map<String, List<MappingHandler>>) getServletContext()
                    .getAttribute("urlMappings");
            if (path.equals("/") || path.isEmpty()) {
                String indexPath = ScanningUrl.findExistingIndex(getServletContext());
                if (indexPath != null) {
                    req.getRequestDispatcher(indexPath).forward(req, res);
                    return;
                }
                throw new NotFoundException("Index introuvable");
            }
            List<MappingHandler> mapH = urlMappings.get(path);
            MappingHandler actualMapH = null;
            if (mapH != null) {
                String reqMethod = req.getMethod();
                for (MappingHandler mh : mapH) {
                    if (mh.getHttpMethod().equals(reqMethod)
                    || mh.getHttpMethod().equals("ALL")) {
                        actualMapH = mh;
                        break;
                    }
                }
            }
            if (actualMapH == null) {
                actualMapH = ScanningUrl.findMatchingPattern(path, urlMappings, req);
            }
            if (actualMapH == null) {
                throw new NotFoundException(
                    "URL introuvable : " + path
                );
            }
            MappingExecutor.execute(req, res, actualMapH);

        } catch (Exception e) {
            Throwable cause = e;
            if (e instanceof InvocationTargetException ite
                && ite.getCause() != null) {
                cause = ite.getCause();
            }
            ErrorHandler.handle(req, res, (Exception) cause);
        }
    }

}