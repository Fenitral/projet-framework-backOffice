<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.cousin.model.Vehicule" %>
<%@ page import="com.cousin.model.Hotel" %>
<%@ page import="com.cousin.model.Distance" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Liste Vehicules</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <%@ include file="../../includes/sidebar.jsp" %>
    
    <div class="main-content">
        <h2>Liste des vehicules</h2>

        <%
            Object message = request.getAttribute("message");
            if (message != null) {
        %>
            <p class="alert alert-success"><%= message %></p>
        <%
            }
        %>

        <p>
            <a href="<%= request.getContextPath() %>/vehicule/form" class="btn btn-primary">Ajouter un vehicule</a>
        </p>

        <table class="table" border="1" cellpadding="6" cellspacing="0">
            <thead>
            <tr>
                <th>Id</th>
                <th>Reference</th>
                <th>Nb place</th>
                <th>Type vehicule</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <%
                List<Vehicule> vehicules = (List<Vehicule>) request.getAttribute("vehicules");
                if (vehicules != null && !vehicules.isEmpty()) {
                    for (Vehicule v : vehicules) {
            %>
            <tr>
                <td><%= v.getIdVehicule() %></td>
                <td><%= v.getReference() %></td>
                <td><%= v.getNbPlace() %></td>
                <td><%= v.getTypeVehicule() %></td>
                <td>
                    <a href="<%= request.getContextPath() %>/vehicule/list?action=edit&id_vehicule=<%= v.getIdVehicule() %>" class="btn btn-warning">Modifier</a>
                    |
                    <a href="<%= request.getContextPath() %>/vehicule/list?action=delete&id_vehicule=<%= v.getIdVehicule() %>" class="btn btn-danger"
                       onclick="return confirm('Supprimer ce vehicule ?');">Supprimer</a>
                </td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr>
                <td colspan="5">Aucun vehicule</td>
            </tr>
            <%
                }
            %>
            </tbody>
        </table>

        <h3 style="margin-top:24px;">Distances Aéroport et Hôtels</h3>
        <%
            List<Hotel> hotels = (List<Hotel>) request.getAttribute("hotels");
            List<Distance> distances = (List<Distance>) request.getAttribute("distances");
            Map<Integer, String> hotelNames = new HashMap<>();
            if (hotels != null) {
                for (Hotel h : hotels) {
                    hotelNames.put(h.getIdHotel(), h.getNom());
                }
            }
        %>

        <h4> Distances entre l'aéroport et les hôtels</h4>
        <table class="table" border="1" cellpadding="6" cellspacing="0">
            <thead>
                <tr>
                    <th>Départ</th>
                    <th>Arrivée</th>
                    <th>Distance (km)</th>
                </tr>
            </thead>
            <tbody>
            <%
                boolean hasAirportDistance = false;
                if (distances != null) {
                    for (Distance d : distances) {
                        boolean aeroportFrom = d.getIdHotelFrom() == 0;
                        boolean aeroportTo = d.getIdHotelTo() == 0;
                        if (aeroportFrom || aeroportTo) {
                            hasAirportDistance = true;
                            int hotelId = aeroportFrom ? d.getIdHotelTo() : d.getIdHotelFrom();
                            String hotelName = hotelNames.getOrDefault(hotelId, "Hôtel #" + hotelId);
            %>
                <tr>
                    <td><%= aeroportFrom ? "AEROPORT" : hotelName %></td>
                    <td><%= aeroportFrom ? hotelName : "AEROPORT" %></td>
                    <td><%= d.getValeur() %></td>
                </tr>
            <%
                        }
                    }
                }
                if (!hasAirportDistance) {
            %>
                <tr>
                    <td colspan="3">Aucune distance Aéroport-Hôtel</td>
                </tr>
            <%
                }
            %>
            </tbody>
        </table>

        <h4 style="margin-top:16px;"> Distances entre hôtels</h4>
        <table class="table" border="1" cellpadding="6" cellspacing="0">
            <thead>
                <tr>
                    <th>Hôtel départ</th>
                    <th>Hôtel arrivée</th>
                    <th>Distance (km)</th>
                </tr>
            </thead>
            <tbody>
            <%
                boolean hasHotelDistance = false;
                if (distances != null) {
                    for (Distance d : distances) {
                        if (d.getIdHotelFrom() > 0 && d.getIdHotelTo() > 0) {
                            hasHotelDistance = true;
                            String fromName = hotelNames.getOrDefault(d.getIdHotelFrom(), "Hôtel #" + d.getIdHotelFrom());
                            String toName = hotelNames.getOrDefault(d.getIdHotelTo(), "Hôtel #" + d.getIdHotelTo());
            %>
                <tr>
                    <td><%= fromName %></td>
                    <td><%= toName %></td>
                    <td><%= d.getValeur() %></td>
                </tr>
            <%
                        }
                    }
                }
                if (!hasHotelDistance) {
            %>
                <tr>
                    <td colspan="3">Aucune distance Hôtel-Hôtel</td>
                </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>
</body>
</html>
