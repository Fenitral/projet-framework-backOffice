<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.cousin.model.Hotel" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reservation</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <%@ include file="../includes/sidebar.jsp" %>
    
    <div class="main-content">
        <h2>Insertion Reservation</h2>

        <%
            Object message = request.getAttribute("message");
            if (message != null) {
        %>
            <p class="alert alert-success"><%= message %></p>
        <%
            }
        %>

        <form method="post" action="<%= request.getContextPath() %>/reservation/create" class="simple-form">
            <label>Date et heure d'arrivee</label><br>
            <input type="datetime-local" name="dateHeureArrive" class="form-control" required><br><br>

            <label>Id client</label><br>
            <input type="text" name="idClient" class="form-control" required><br><br>

            <label>Nombre de passagers</label><br>
            <input type="number" name="nbPassager" min="1" class="form-control" required><br><br>

            <label>Hotel</label><br>
            <select name="hotel.idHotel" class="form-control" required>
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

            <button type="submit" class="btn btn-primary">Enregistrer</button>
        </form>
    </div>
</body>
</html>
</body>
</html>
