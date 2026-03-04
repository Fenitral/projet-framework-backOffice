<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.cousin.model.Vehicule" %>

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
    </div>
</body>
</html>
