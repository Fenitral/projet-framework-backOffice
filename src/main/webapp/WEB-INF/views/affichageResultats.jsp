<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cousin.dto.PlanificationDTO" %>
<%@ page import="com.cousin.dto.TrajetVehiculeDTO" %>
<%@ page import="com.cousin.dto.ReservationAffecteeDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html>
<head>
    <title>Résultats de la Planification</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * { box-sizing: border-box; }
        body { 
            font-family: 'Segoe UI', Arial, sans-serif; 
            margin: 0; 
            padding: 20px;
            background: #f5f7fa;
            min-height: 100vh;
            margin-left: 20em; /* Laisser de l'espace pour la sidebar */
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
        }
        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            border-radius: 16px;
            margin-bottom: 30px;
            box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
        }
        .header h2 {
            margin: 0 0 10px 0;
            font-size: 1.8em;
        }
        .header p {
            margin: 0;
            opacity: 0.9;
        }
        .stats-row {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .stat-card {
            background: white;
            padding: 20px;
            border-radius: 12px;
            text-align: center;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        .stat-card i {
            font-size: 2em;
            margin-bottom: 10px;
        }
        .stat-card .value {
            font-size: 2em;
            font-weight: bold;
            margin: 5px 0;
        }
        .stat-card .label {
            color: #666;
            font-size: 0.9em;
        }
        .stat-reservations { color: #28a745; }
        .stat-vehicules { color: #007bff; }
        .stat-distance { color: #fd7e14; }
        .stat-passagers { color: #6f42c1; }
        
        .section { 
            background: white;
            border-radius: 12px; 
            padding: 25px; 
            margin-bottom: 30px; 
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        .section h3 {
            margin: 0 0 20px 0;
            padding-bottom: 15px;
            border-bottom: 2px solid #eee;
            display: flex;
            align-items: center;
        }
        .section h3 i {
            margin-right: 12px;
            font-size: 1.2em;
        }
        .assigned h3 { color: #28a745; border-bottom-color: #28a745; }
        .unassigned h3 { color: #dc3545; border-bottom-color: #dc3545; }
        
        .trajet-card {
            background: #f8f9fa;
            border-left: 5px solid #28a745;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
        }
        .trajet-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
            margin-bottom: 15px;
            padding-bottom: 15px;
            border-bottom: 1px dashed #ddd;
        }
        .vehicule-info {
            display: flex;
            align-items: center;
            gap: 15px;
        }
        .vehicule-icon {
            width: 50px;
            height: 50px;
            background: #28a745;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 1.5em;
        }
        .vehicule-details h4 {
            margin: 0;
            color: #333;
        }
        .vehicule-details span {
            color: #666;
            font-size: 0.9em;
        }
        .trajet-stats {
            display: flex;
            gap: 20px;
            flex-wrap: wrap;
        }
        .trajet-stat {
            text-align: center;
            padding: 10px 15px;
            background: white;
            border-radius: 8px;
        }
        .trajet-stat .value {
            font-weight: bold;
            font-size: 1.2em;
            color: #333;
        }
        .trajet-stat .label {
            font-size: 0.8em;
            color: #666;
        }
        
        table { 
            width: 100%; 
            border-collapse: collapse; 
        }
        th, td { 
            padding: 12px 15px; 
            text-align: left;
            border-bottom: 1px solid #eee;
        }
        th { 
            background-color: #f8f9fa; 
            font-weight: 600;
            color: #555;
        }
        tr:hover {
            background: #f8f9fa;
        }
        
        .badge {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.85em;
            font-weight: 500;
        }
        .badge-diesel { background: #d4edda; color: #155724; }
        .badge-essence { background: #fff3cd; color: #856404; }
        .badge-hybride { background: #cce5ff; color: #004085; }
        .badge-electrique { background: #e2e3e5; color: #383d41; }
        
        .empty-state {
            text-align: center;
            padding: 40px;
            color: #999;
        }
        .empty-state i {
            font-size: 3em;
            margin-bottom: 15px;
        }
        
        .actions {
            display: flex;
            gap: 15px;
            flex-wrap: wrap;
            margin-top: 20px;
        }
        .btn {
            display: inline-flex;
            align-items: center;
            padding: 12px 24px;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 600;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .btn:hover {
            transform: translateY(-2px);
        }
        .btn i {
            margin-right: 8px;
        }
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        .btn-success {
            background: #28a745;
            color: white;
        }
        .btn-secondary {
            background: #6c757d;
            color: white;
        }
    </style>
</head>
<body>
    <%@ include file="../includes/sidebar.jsp" %>
<%
    PlanificationDTO planification = (PlanificationDTO) request.getAttribute("planification");
    String datePlanification = (String) request.getAttribute("datePlanification");
    String heureDepart = (String) request.getAttribute("heureDepart");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    
    int totalTrajets = 0;
    int totalNonAffectees = 0;
    double distanceTotale = 0;
    int totalPassagers = 0;
    int totalReservations = 0;
    
    if (planification != null) {
        totalTrajets = planification.getTrajets() != null ? planification.getTrajets().size() : 0;
        totalNonAffectees = planification.getReservationsNonAffectees() != null ? planification.getReservationsNonAffectees().size() : 0;
        distanceTotale = planification.getDistanceTotaleJour();
        totalPassagers = planification.getTotalPassagers();
        totalReservations = planification.getTotalReservations();
    }
%>
    <div class="container">
        <!-- Message de succès -->
        <% String successMessage = (String) request.getAttribute("successMessage"); %>
        <% if (successMessage != null) { %>
        <div style="background: #d4edda; color: #155724; padding: 15px 20px; border-radius: 8px; margin-bottom: 20px; display: flex; align-items: center; gap: 10px;">
            <i class="fas fa-check-circle"></i>
            <span><%= successMessage %></span>
        </div>
        <% } %>
        
        <!-- Header -->
        <div class="header">
            <h2><i class="fas fa-clipboard-check"></i> Résultats de la Planification</h2>
            <p><i class="fas fa-calendar"></i> Date : <%= datePlanification %> | <i class="fas fa-clock"></i> Heure de départ : <%= heureDepart %></p>
        </div>
        
        <!-- Statistics -->
        <div class="stats-row">
            <div class="stat-card">
                <i class="fas fa-calendar-check stat-reservations"></i>
                <div class="value stat-reservations"><%= totalReservations %></div>
                <div class="label">Réservations totales</div>
            </div>
            <div class="stat-card">
                <i class="fas fa-shuttle-van stat-vehicules"></i>
                <div class="value stat-vehicules"><%= totalTrajets %></div>
                <div class="label">Véhicules utilisés</div>
            </div>
            <div class="stat-card">
                <i class="fas fa-road stat-distance"></i>
                <div class="value stat-distance"><%= String.format("%.1f", distanceTotale) %></div>
                <div class="label">Distance totale (km)</div>
            </div>
            <div class="stat-card">
                <i class="fas fa-users stat-passagers"></i>
                <div class="value stat-passagers"><%= totalPassagers %></div>
                <div class="label">Passagers transportés</div>
            </div>
        </div>
        
        <!-- Trajets assignés -->
        <div class="section assigned">
            <h3><i class="fas fa-check-circle"></i> Trajets Assignés</h3>
            
            <% if (planification != null && planification.getTrajets() != null && !planification.getTrajets().isEmpty()) { %>
                <% for (TrajetVehiculeDTO trajet : planification.getTrajets()) { 
                    String typeVehicule = trajet.getTypeVehicule();
                    String badgeClass = "badge-essence";
                    String typeLabel = "Essence";
                    if ("D".equalsIgnoreCase(typeVehicule) || "DIESEL".equalsIgnoreCase(typeVehicule)) {
                        badgeClass = "badge-diesel";
                        typeLabel = "Diesel";
                    } else if ("H".equalsIgnoreCase(typeVehicule) || "HYBRIDE".equalsIgnoreCase(typeVehicule)) {
                        badgeClass = "badge-hybride";
                        typeLabel = "Hybride";
                    } else if ("EL".equalsIgnoreCase(typeVehicule) || "ELECTRIQUE".equalsIgnoreCase(typeVehicule)) {
                        badgeClass = "badge-electrique";
                        typeLabel = "Électrique";
                    }
                %>
                <div class="trajet-card">
                    <div class="trajet-header">
                        <div class="vehicule-info">
                            <div class="vehicule-icon">
                                <i class="fas fa-shuttle-van"></i>
                            </div>
                            <div class="vehicule-details">
                                <h4><%= trajet.getVehiculeReference() %></h4>
                                <span><span class="badge <%= badgeClass %>"><%= typeLabel %></span> | Capacité: <%= trajet.getCapacite() %> places</span>
                            </div>
                        </div>
                        <div class="trajet-stats">
                            <div class="trajet-stat">
                                <div class="value"><%= trajet.getHeureDepart() != null ? trajet.getHeureDepart().format(timeFormatter) : "-" %></div>
                                <div class="label">Départ</div>
                            </div>
                            <div class="trajet-stat">
                                <div class="value"><%= trajet.getHeureRetourPrevue() != null ? trajet.getHeureRetourPrevue().format(timeFormatter) : "-" %></div>
                                <div class="label">Retour prévu</div>
                            </div>
                            <div class="trajet-stat">
                                <div class="value"><%= String.format("%.1f", trajet.getDistanceParcourue()) %> km</div>
                                <div class="label">Distance parcourue</div>
                            </div>
                            <div class="trajet-stat">
                                <div class="value"><%= String.format("%.1f", trajet.getDistanceTotale()) %> km</div>
                                <div class="label">Distance totale</div>
                            </div>
                        </div>
                    </div>
                    
                    <table>
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Client</th>
                                <th>Hôtel</th>
                                <th>Passagers</th>
                                <th>Distance Aéroport-Hôtel</th>
                                <th>Distance depuis précédent</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (ReservationAffecteeDTO res : trajet.getListeReservations()) { %>
                            <tr>
                                <td><%= res.getOrdreVisite() %></td>
                                <td><i class="fas fa-user-circle" style="color: #6f42c1; margin-right: 5px;"></i><%= res.getClientId() != null ? res.getClientId() : (res.getIdClient() != null ? res.getIdClient() : "-") %></td>
                                <td><i class="fas fa-hotel" style="color: #667eea; margin-right: 8px;"></i><%= res.getNomHotel() %></td>
                                <td><i class="fas fa-users" style="color: #28a745; margin-right: 5px;"></i><%= res.getNbPassager() %></td>
                                <td><i class="fas fa-plane" style="color: #fd7e14; margin-right: 5px;"></i><%= String.format("%.1f", res.getDistanceDepuisAeroport()) %> km</td>
                                <td><i class="fas fa-route" style="color: #17a2b8; margin-right: 5px;"></i><%= String.format("%.1f", res.getDistance()) %> km</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                    
                    <!-- Timeline du trajet -->
                    <div class="timeline-container" style="margin-top: 20px; padding: 15px; background: linear-gradient(90deg, #f8f9fa 0%, #e9ecef 100%); border-radius: 8px;">
                        <div style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 10px;">
                            <!-- Départ -->
                            <div style="text-align: center; min-width: 80px;">
                                <div style="font-size: 0.75em; color: #666;">Départ</div>
                                <div style="font-weight: bold; color: #28a745;"><%= trajet.getHeureDepart() != null ? trajet.getHeureDepart().format(timeFormatter) : "-" %></div>
                                <i class="fas fa-plane-departure" style="color: #28a745; font-size: 1.2em;"></i>
                            </div>
                            
                            <% for (ReservationAffecteeDTO res : trajet.getListeReservations()) { %>
                            <!-- Flèche -->
                            <div style="flex-grow: 1; height: 2px; background: linear-gradient(90deg, #28a745, #667eea); max-width: 50px;"></div>
                            
                            <!-- Hôtel -->
                            <div style="text-align: center; min-width: 80px;">
                                <div style="font-size: 0.75em; color: #666;"><%= res.getNomHotel() %></div>
                                <div style="font-weight: bold; color: #667eea;"><%= res.getHeurePassage() != null ? res.getHeurePassage().format(timeFormatter) : "-" %></div>
                                <i class="fas fa-hotel" style="color: #667eea; font-size: 1.2em;"></i>
                            </div>
                            <% } %>
                            
                            <!-- Flèche retour -->
                            <div style="flex-grow: 1; height: 2px; background: linear-gradient(90deg, #667eea, #dc3545); max-width: 50px;"></div>
                            
                            <!-- Retour -->
                            <div style="text-align: center; min-width: 80px;">
                                <div style="font-size: 0.75em; color: #666;">Retour</div>
                                <div style="font-weight: bold; color: #dc3545;"><%= trajet.getHeureRetourPrevue() != null ? trajet.getHeureRetourPrevue().format(timeFormatter) : "-" %></div>
                                <i class="fas fa-plane-arrival" style="color: #dc3545; font-size: 1.2em;"></i>
                            </div>
                        </div>
                    </div>
                </div>
                <% } %>
            <% } else { %>
                <div class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <p>Aucun trajet assigné pour cette date</p>
                </div>
            <% } %>
        </div>
        
        <!-- Réservations non assignées -->
        <div class="section unassigned">
            <h3><i class="fas fa-exclamation-triangle"></i> Réservations Non Assignées</h3>
            
            <% if (planification != null && planification.getReservationsNonAffectees() != null && !planification.getReservationsNonAffectees().isEmpty()) { %>
                <table>
                    <thead>
                        <tr>
                            <th>ID Réservation</th>
                            <th>Client</th>
                            <th>Hôtel</th>
                            <th>Passagers</th>
                            <th>Motif</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (ReservationAffecteeDTO res : planification.getReservationsNonAffectees()) { %>
                        <tr>
                            <td>#<%= res.getIdReservation() %></td>
                            <td><i class="fas fa-user-circle" style="color: #6f42c1; margin-right: 5px;"></i><%= res.getClientId() != null ? res.getClientId() : (res.getIdClient() != null ? res.getIdClient() : "-") %></td>
                            <td><i class="fas fa-hotel" style="color: #dc3545; margin-right: 8px;"></i><%= res.getNomHotel() %></td>
                            <td><%= res.getNbPassager() %></td>
                            <td><span class="badge" style="background: #f8d7da; color: #721c24;">Capacité insuffisante</span></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <div class="empty-state">
                    <i class="fas fa-check-circle" style="color: #28a745;"></i>
                    <p>Toutes les réservations ont été assignées avec succès !</p>
                </div>
            <% } %>
        </div>
        
        <!-- Actions -->
        <div class="actions">
            <a href="<%= request.getContextPath() %>/planification" class="btn btn-primary">
                <i class="fas fa-arrow-left"></i> Nouvelle planification
            </a>
            <a href="<%= request.getContextPath() %>/planification/save?date=<%= datePlanification %>&heureDepart=<%= heureDepart %>" class="btn btn-success">
                <i class="fas fa-save"></i> Sauvegarder cette planification
            </a>
            <a href="<%= request.getContextPath() %>/" class="btn btn-secondary">
                <i class="fas fa-home"></i> Accueil
            </a>
        </div>
    </div>
</body>
</html>
