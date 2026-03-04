<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Accueil - Gestion Réservations</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <%@ include file="WEB-INF/includes/sidebar.jsp" %>
    
    <div class="main-content">
        <h1>Bienvenue dans le système de gestion</h1>
        
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; margin: 40px 0;">
            
            <div class="welcome-card">
                <h3 style="color: #3498db; margin-bottom: 15px;">🚗 Véhicules</h3>
                <p style="color: #666; margin-bottom: 20px;">Gérez votre parc automobile</p>
                <a href="<%= request.getContextPath() %>/vehicule/list" class="btn btn-primary">Liste des véhicules</a>
                <a href="<%= request.getContextPath() %>/vehicule/form" class="btn btn-secondary">Ajouter véhicule</a>
            </div>
            
            <div class="welcome-card">
                <h3 style="color: #27ae60; margin-bottom: 15px;">📅 Réservations</h3>
                <p style="color: #666; margin-bottom: 20px;">Gestion des réservations clients</p>
                <a href="<%= request.getContextPath() %>/reservation/create" class="btn btn-primary">Nouvelle réservation</a>
            </div>
            
        </div>
    </div>
</body>
</html>