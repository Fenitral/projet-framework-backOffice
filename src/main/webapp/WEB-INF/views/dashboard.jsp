<%@ page contentType="text/html;charset=UTF-8" %>

<%
    request.setAttribute("pageTitle", "Tableau de Bord");
    request.setAttribute("currentPage", "dashboard");
%>



<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-bottom: 30px;">
    
    <!-- Card Réservations -->
    <div class="card">
        <div class="card-body text-center">
            <i class="fas fa-calendar-check" style="font-size: 3em; color: #28a745; margin-bottom: 15px;"></i>
            <h3 style="color: #28a745; margin: 0;">Réservations</h3>
            <p style="color: #6c757d; margin: 5px 0;">Gestion des réservations clients</p>
            <a href="<%= request.getContextPath() %>/reservation/create" class="btn btn-success">
                <i class="fas fa-plus"></i> Nouvelle Réservation
            </a>
        </div>
    </div>
    
    <!-- Card Véhicules -->
    <div class="card">
        <div class="card-body text-center">
            <i class="fas fa-car" style="font-size: 3em; color: #007bff; margin-bottom: 15px;"></i>
            <h3 style="color: #007bff; margin: 0;">Véhicules</h3>
            <p style="color: #6c757d; margin: 5px 0;">Gestion du parc automobile</p>
            <a href="<%= request.getContextPath() %>/vehicule/list" class="btn btn-primary">
                <i class="fas fa-list"></i> Voir les Véhicules
            </a>
        </div>
    </div>
    
    <!-- Card Planification -->
    <div class="card">
        <div class="card-body text-center">
            <i class="fas fa-calendar-alt" style="font-size: 3em; color: #667eea; margin-bottom: 15px;"></i>
            <h3 style="color: #667eea; margin: 0;">Planification</h3>
            <p style="color: #6c757d; margin: 5px 0;">Assignation des véhicules</p>
            <a href="<%= request.getContextPath() %>/planification" class="btn" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
                <i class="fas fa-cogs"></i> Planifier
            </a>
        </div>
    </div>
    
    <!-- Card Articles -->
    <div class="card">
        <div class="card-body text-center">
            <i class="fas fa-box" style="font-size: 3em; color: #fd7e14; margin-bottom: 15px;"></i>
            <h3 style="color: #fd7e14; margin: 0;">Articles</h3>
            <p style="color: #6c757d; margin: 5px 0;">Gestion des articles et produits</p>
            <a href="#" class="btn" style="background-color: #fd7e14; color: white;">
                <i class="fas fa-tag"></i> Gérer Articles
            </a>
        </div>
    </div>
    
    <!-- Card Stock -->
    <div class="card">
        <div class="card-body text-center">
            <i class="fas fa-warehouse" style="font-size: 3em; color: #6f42c1; margin-bottom: 15px;"></i>
            <h3 style="color: #6f42c1; margin: 0;">Stock</h3>
            <p style="color: #6c757d; margin: 5px 0;">Suivi des inventaires</p>
            <a href="#" class="btn" style="background-color: #6f42c1; color: white;">
                <i class="fas fa-chart-bar"></i> Voir Stock
            </a>
        </div>
    </div>
    
</div>

<!-- Statistiques rapides -->
<div class="card">
    <div class="card-header">
        <h3><i class="fas fa-chart-line"></i> Aperçu Rapide</h3>
    </div>
    <div class="card-body">
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 30px;">
            
            <div class="text-center">
                <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 10px; margin-bottom: 10px;">
                    <i class="fas fa-calendar-day" style="font-size: 2em; margin-bottom: 10px;"></i>
                    <div style="font-size: 2em; font-weight: bold;">0</div>
                    <div style="font-size: 0.9em; opacity: 0.8;">Réservations Aujourd'hui</div>
                </div>
            </div>
            
            <div class="text-center">
                <div style="background: linear-gradient(135deg, #28a745 0%, #20c997 100%); color: white; padding: 20px; border-radius: 10px; margin-bottom: 10px;">
                    <i class="fas fa-car-side" style="font-size: 2em; margin-bottom: 10px;"></i>
                    <div style="font-size: 2em; font-weight: bold;">0</div>
                    <div style="font-size: 0.9em; opacity: 0.8;">Véhicules Actifs</div>
                </div>
            </div>
            
            <div class="text-center">
                <div style="background: linear-gradient(135deg, #ffc107 0%, #fd7e14 100%); color: white; padding: 20px; border-radius: 10px; margin-bottom: 10px;">
                    <i class="fas fa-users" style="font-size: 2em; margin-bottom: 10px;"></i>
                    <div style="font-size: 2em; font-weight: bold;">0</div>
                    <div style="font-size: 0.9em; opacity: 0.8;">Clients Actifs</div>
                </div>
            </div>
            
            <div class="text-center">
                <div style="background: linear-gradient(135deg, #dc3545 0%, #e83e8c 100%); color: white; padding: 20px; border-radius: 10px; margin-bottom: 10px;">
                    <i class="fas fa-euro-sign" style="font-size: 2em; margin-bottom: 10px;"></i>
                    <div style="font-size: 2em; font-weight: bold;">0€</div>
                    <div style="font-size: 0.9em; opacity: 0.8;">Chiffre d'Affaires</div>
                </div>
            </div>
            
        </div>
    </div>
</div>

<!-- Actions rapides -->
<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-top: 30px;">
    
    <div class="card">
        <div class="card-header">
            <h4><i class="fas fa-tachometer-alt"></i> Actions Rapides</h4>
        </div>
        <div class="card-body">
            <div style="display: flex; flex-direction: column; gap: 10px;">
                <a href="<%= request.getContextPath() %>/planification" class="btn" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
                    <i class="fas fa-calendar-alt"></i> Planification
                </a>
                <a href="<%= request.getContextPath() %>/reservation/create" class="btn btn-success">
                    <i class="fas fa-plus"></i> Nouvelle Réservation
                </a>
                <a href="<%= request.getContextPath() %>/vehicule/form" class="btn btn-primary">
                    <i class="fas fa-car"></i> Ajouter Véhicule
                </a>
                <a href="<%= request.getContextPath() %>/vehicule/list" class="btn btn-secondary">
                    <i class="fas fa-list"></i> Liste Véhicules
                </a>
            </div>
        </div>
    </div>
    
    <div class="card">
        <div class="card-header">
            <h4><i class="fas fa-clock"></i> Activité Récente</h4>
        </div>
        <div class="card-body">
            <div style="color: #6c757d; text-align: center; padding: 20px;">
                <i class="fas fa-history" style="font-size: 2em; opacity: 0.3; margin-bottom: 10px;"></i>
                <p>Aucune activité récente</p>
            </div>
        </div>
    </div>
    
</div>

<%@ include file="../includes/layout-footer.jsp" %>