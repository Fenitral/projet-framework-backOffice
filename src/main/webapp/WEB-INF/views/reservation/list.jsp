<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.cousin.model.Reservation" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Liste Reservations</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <%@ include file="../../includes/sidebar.jsp" %>

    <div class="main-content">
        <h2>Liste des reservations</h2>

        <table class="table" border="1" cellpadding="6" cellspacing="0">
            <thead>
                <tr>
                    <th>Id</th>
                    <th>Date et heure arrivee</th>
                    <th>Id client</th>
                    <th>Nb passagers</th>
                    <th>Hotel</th>
                </tr>
            </thead>
            <tbody>
            <%
                List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");
                if (reservations != null && !reservations.isEmpty()) {
                    for (Reservation r : reservations) {
            %>
                <tr>
                    <td><%= r.getIdReservation() %></td>
                    <td><%= r.getDateHeureArrive() != null ? r.getDateHeureArrive() : "-" %></td>
                    <td><%= r.getIdClient() != null ? r.getIdClient() : "-" %></td>
                    <td><%= r.getNbPassager() %></td>
                    <td><%= (r.getHotel() != null && r.getHotel().getNom() != null) ? r.getHotel().getNom() : "-" %></td>
                </tr>
            <%
                    }
                } else {
            %>
                <tr>
                    <td colspan="5">Aucune reservation</td>
                </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>
</body>
</html>
