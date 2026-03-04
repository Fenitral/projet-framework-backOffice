<!-- Sidebar Navigation -->
<div class="sidebar">
    <div class="sidebar-header">
        <h3><i class="fas fa-building"></i> BackOffice</h3>
    </div>
    
    <nav class="sidebar-nav">
        <ul>
            <li><a href="<%= request.getContextPath() %>/" class="nav-link"><i class="fas fa-home"></i> Accueil</a></li>
            <li><a href="<%= request.getContextPath() %>/vehicule/list" class="nav-link"><i class="fas fa-car"></i> Liste Véhicules</a></li>
            <li><a href="<%= request.getContextPath() %>/vehicule/form" class="nav-link"><i class="fas fa-plus-circle"></i> Ajouter Véhicule</a></li>
            <li><a href="<%= request.getContextPath() %>/reservation/form" class="nav-link"><i class="fas fa-calendar-plus"></i> Nouvelle Réservation</a></li>
        </ul>
    </nav>
</div>