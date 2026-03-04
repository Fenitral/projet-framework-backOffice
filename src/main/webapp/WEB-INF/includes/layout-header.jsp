<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String currentPage = (String) request.getAttribute("currentPage");
    if (currentPage == null) {
        currentPage = "";
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Proforma Client - <%= request.getAttribute("pageTitle") != null ? request.getAttribute("pageTitle") : "BackOffice" %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="app-container">
        <!-- Sidebar -->
        <div class="sidebar">
            <div class="sidebar-header">
                <h3><i class="fas fa-chart-line"></i> BackOffice</h3>
            </div>
            
            <nav class="sidebar-nav">
                <ul>
                <li class="nav-item">
                    <a href="<%= request.getContextPath() %>/" class="nav-link <%= "dashboard".equals(currentPage) ? "active" : "" %>">
                        <i class="fas fa-tachometer-alt"></i>
                        Accueil
                    </a>
                </li>
                
                <li class="nav-item">
                    <a href="<%= request.getContextPath() %>/reservation/create" class="nav-link <%= "reservation".equals(currentPage) ? "active" : "" %>">
                        <i class="fas fa-calendar-plus"></i>
                        Nouvelle Réservation
                    </a>
                </li>
                
                <li class="nav-item">
                    <a href="<%= request.getContextPath() %>/vehicule/list" class="nav-link <%= "vehicule-list".equals(currentPage) ? "active" : "" %>">
                        <i class="fas fa-car"></i>
                        Liste Véhicules
                    </a>
                </li>
                
                <li class="nav-item">
                    <a href="<%= request.getContextPath() %>/vehicule/form" class="nav-link <%= "vehicule-form".equals(currentPage) ? "active" : "" %>">
                        <i class="fas fa-plus-circle"></i>
                        Ajouter Véhicule
                    </a>
                </li>
                </ul>
                
                <!-- Section Achats -->
                <div class="nav-section-title">
                    <i class="fas fa-shopping-cart"></i>
                    Achats
                </div>
                <ul>
                
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-box"></i>
                        Articles
                    </a>
                </li>
                
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-warehouse"></i>
                        Stock
                    </a>
                </li>
                
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-truck"></i>
                        Mouvements
                    </a>
                </li>
                
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-exchange-alt"></i>
                        Transferts
                    </a>
                </li>
                </ul>
                
                <!-- Section Ventes -->
                <div class="nav-section-title">
                    <i class="fas fa-chart-pie"></i>
                    Ventes
                </div>
                <ul>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-chart-bar"></i>
                        KPI Ventes
                    </a>
                </li>
                </ul>
                
                <!-- Section Admin -->
                <div class="nav-section-title">
                    <i class="fas fa-cog"></i>
                    Administration
                </div>
                <ul>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-users"></i>
                        Utilisateurs
                    </a>
                </li>
                
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-cogs"></i>
                        Paramètres
                    </a>
                </li>
                </ul>
                
                <div class="sidebar-footer">
                    <a href="#" class="nav-link logout-link">
                        <i class="fas fa-sign-out-alt"></i>
                        Déconnexion
                    </a>
                </div>
            </nav>
        </div>
        
        <!-- Contenu principal -->
        <div class="main-content">
            <div class="content-header">
                <h1><%= request.getAttribute("pageTitle") != null ? request.getAttribute("pageTitle") : "BackOffice" %></h1>
            </div>
            
            <div class="content-body">
                <!-- Le contenu de la page sera injecté ici -->
    <script src="<%= request.getContextPath() %>/js/sidebar.js"></script>