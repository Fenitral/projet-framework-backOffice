<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.cousin.model.Hotel" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reservation</title>
</head>
<body>
<h2>Insertion Reservation</h2>

<%
    Object message = request.getAttribute("message");
    if (message != null) {
%>
    <p><strong><%= message %></strong></p>
<%
    }
%>

<form method="post" action="<%= request.getContextPath() %>/reservation/create">
    <label>Date et heure d'arrivee</label><br>
    <input type="datetime-local" name="dateHeureArrive" required><br><br>

    <label>Id client</label><br>
    <input type="text" name="idClient" required><br><br>

    <label>Nombre de passagers</label><br>
    <input type="number" name="nbPassager" min="1" required><br><br>

    <label>Hotel</label><br>
    <select name="hotel.idHotel" required>
        <option value="">-- Choisir --</option>
        <%
            List<Hotel> hotels = (List<Hotel>) request.getAttribute("hotels");
            if (hotels != null) {
                for (Hotel hotel : hotels) {
        %>
            <option value="<%= hotel.getIdHotel() %>"><%= hotel.getNom() %></option>
        <%
                }
            }
        %>
    </select>
    <br><br>

    <button type="submit">Enregistrer</button>
</form>
</body>
</html>
