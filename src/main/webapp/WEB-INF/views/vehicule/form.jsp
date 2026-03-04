<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.cousin.model.Vehicule" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Vehicule</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <%@ include file="../../includes/sidebar.jsp" %>
    
    <div class="main-content">
        <%
            Object message = request.getAttribute("message");
            Vehicule vehicule = (Vehicule) request.getAttribute("vehicule");
            String action = (String) request.getAttribute("action");
            if (action == null || action.isBlank()) {
                action = "insert";
            }
            boolean isEdit = "edit".equalsIgnoreCase(action) && vehicule != null;
            String reference = vehicule != null && vehicule.getReference() != null ? vehicule.getReference() : "";
            String nbPlace = vehicule != null ? String.valueOf(vehicule.getNbPlace()) : "";
            String typeVehicule = vehicule != null && vehicule.getTypeVehicule() != null ? vehicule.getTypeVehicule() : "";
        %>

        <h2><%= isEdit ? "Modification Vehicule" : "Insertion Vehicule" %></h2>

        <% if (message != null) { %>
            <p class="alert alert-success"><%= message %></p>
        <% } %>

        <form method="post" action="<%= request.getContextPath() %>/vehicule/form?action=<%= action %>" class="simple-form">
            <% if (isEdit) { %>
                <input type="hidden" name="id_vehicule" value="<%= vehicule.getIdVehicule() %>">
            <% } %>

            <label>Reference</label><br>
            <input type="text" name="reference" value="<%= reference %>" class="form-control" required><br><br>

            <label>Nombre de places</label><br>
            <input type="number" name="nbPlace" min="1" value="<%= nbPlace %>" class="form-control" required><br><br>

            <label>Type vehicule</label><br>
            <select name="typeVehicule" class="form-control" required>
                <option value="">Sélectionnez un type de carburant</option>
                <option value="Diesel" <%= "Diesel".equals(typeVehicule) ? "selected" : "" %>>Diesel</option>
                <option value="Essence" <%= "Essence".equals(typeVehicule) ? "selected" : "" %>>Essence</option>
                <option value="Électrique" <%= "Électrique".equals(typeVehicule) ? "selected" : "" %>>Électrique</option>
                <option value="Hybride" <%= "Hybride".equals(typeVehicule) ? "selected" : "" %>>Hybride</option>
                <option value="GPL" <%= "GPL".equals(typeVehicule) ? "selected" : "" %>>GPL (Gaz de Pétrole Liquéfié)</option>
                <option value="GNV" <%= "GNV".equals(typeVehicule) ? "selected" : "" %>>GNV (Gaz Naturel Véhicule)</option>
                <option value="Hydrogène" <%= "Hydrogène".equals(typeVehicule) ? "selected" : "" %>>Hydrogène</option>
            </select><br><br>

            <button type="submit" class="btn btn-primary">Enregistrer</button>
        </form>

        <p>
            <a href="<%= request.getContextPath() %>/vehicule/list" class="btn btn-secondary">Voir la liste</a>
        </p>
    </div>
</body>
</html>
