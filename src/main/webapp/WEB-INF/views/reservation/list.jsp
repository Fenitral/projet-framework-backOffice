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

        <div style="margin-bottom: 20px; padding: 15px; background-color: #f5f5f5; border-radius: 5px;">
            <h3>Filtre par date</h3>
            <form method="get" action="<%= request.getContextPath() %>/reservation/list" style="display: flex; gap: 15px; align-items: flex-end;">
                <div>
                    <label for="dateDebut">Date début :</label><br>
                    <input type="date" id="dateDebut" name="dateDebut" value="<%= request.getAttribute("dateDebut") != null ? request.getAttribute("dateDebut") : "" %>" required>
                </div>
                <div>
                    <label for="dateFin">Date fin :</label><br>
                    <input type="date" id="dateFin" name="dateFin" value="<%= request.getAttribute("dateFin") != null ? request.getAttribute("dateFin") : "" %>" required>
                </div>
                <button type="submit" style="padding: 8px 20px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer;">Appliquer</button>
            </form>
        </div>

        <table class="table" border="1" cellpadding="6" cellspacing="0">
            <thead>
                <tr>
                    <th>Id</th>
                    <th>Date et heure arrivee</th>
                    <th>Id client</th>
                    <th>Nb passagers</th>
                    <th>Hotel</th>
                </tr>6
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
