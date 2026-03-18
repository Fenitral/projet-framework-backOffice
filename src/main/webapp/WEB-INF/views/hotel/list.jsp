<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.cousin.model.Hotel" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Liste Hotels</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <%@ include file="../../includes/sidebar.jsp" %>

    <div class="main-content">
        <h2>Liste des hotels</h2>

        <table class="table" border="1" cellpadding="6" cellspacing="0">
            <thead>
                <tr>
                    <th>Id</th>
                    <th>Nom</th>
                    <th>Aeroport</th>
                </tr>
            </thead>
            <tbody>
            <%
                List<Hotel> hotels = (List<Hotel>) request.getAttribute("hotels");
                if (hotels != null && !hotels.isEmpty()) {
                    for (Hotel h : hotels) {
            %>
                <tr>
                    <td><%= h.getIdHotel() %></td>
                    <td><%= h.getNom() != null ? h.getNom() : "-" %></td>
                    <td><%= h.getAeroport() != null ? h.getAeroport() : "-" %></td>
                </tr>
            <%
                    }
                } else {
            %>
                <tr>
                    <td colspan="3">Aucun hotel</td>
                </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>
</body>
</html>
