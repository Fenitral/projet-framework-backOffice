<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cousin.dto.GroupementDTO" %>
<%@ page import="com.cousin.dto.TrajetVehiculeDTO" %>
<%@ page import="com.cousin.dto.ReservationAffecteeDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html>
<head>
    <title>Groupements des Voitures</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * { box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Arial, sans-serif;
            margin: 0;
            padding: 24px;
            background: #f5f7fa;
            min-height: 100vh;
            margin-left: 20em;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
        }

        /* ── En-tête ─────────────────────────────────────── */
        .page-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 28px 32px;
            border-radius: 16px;
            margin-bottom: 28px;
            box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
            display: flex;
            align-items: center;
            justify-content: space-between;
            flex-wrap: wrap;
            gap: 12px;
        }
        .page-header h2 {
            margin: 0 0 4px 0;
            font-size: 1.7em;
        }
        .page-header p {
            margin: 0;
            opacity: 0.88;
            font-size: 0.95em;
        }
        .header-actions {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }
        .btn {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 10px 18px;
            border-radius: 8px;
            font-size: 0.9em;
            font-weight: 600;
            text-decoration: none;
            cursor: pointer;
            border: none;
            transition: transform 0.15s, box-shadow 0.15s;
        }
        .btn:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(0,0,0,0.2); }
        .btn-white  { background: white; color: #667eea; }
        .btn-outline { background: transparent; color: white; border: 2px solid rgba(255,255,255,0.6); }
        .btn-outline:hover { background: rgba(255,255,255,0.12); }

        /* ── Résumé global ────────────────────────────────── */
        .summary-row {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 16px;
            margin-bottom: 28px;
        }
        .summary-card {
            background: white;
            padding: 18px 20px;
            border-radius: 12px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.07);
            display: flex;
            align-items: center;
            gap: 14px;
        }
        .summary-card .icon {
            width: 44px;
            height: 44px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.2em;
            color: white;
            flex-shrink: 0;
        }
        .icon-purple { background: linear-gradient(135deg, #667eea, #764ba2); }
        .icon-green  { background: linear-gradient(135deg, #43b89c, #2d9f74); }
        .icon-orange { background: linear-gradient(135deg, #f59e0b, #ef4444); }
        .summary-card .value { font-size: 1.6em; font-weight: 700; color: #1e293b; }
        .summary-card .label { font-size: 0.8em; color: #64748b; margin-top: 2px; }

        /* ── Vide ─────────────────────────────────────────── */
        .empty-state {
            background: white;
            border-radius: 16px;
            padding: 60px 32px;
            text-align: center;
            box-shadow: 0 2px 12px rgba(0,0,0,0.07);
        }
        .empty-state i { font-size: 3.5em; color: #cbd5e1; margin-bottom: 18px; }
        .empty-state h3 { color: #64748b; margin-bottom: 8px; }
        .empty-state p  { color: #94a3b8; }

        /* ── Carte de groupement ──────────────────────────── */
        .groupement-card {
            background: white;
            border-radius: 16px;
            box-shadow: 0 2px 16px rgba(0,0,0,0.08);
            margin-bottom: 28px;
            overflow: hidden;
        }
        .groupement-header {
            background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
            color: white;
            padding: 18px 24px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            flex-wrap: wrap;
            gap: 10px;
        }
        .groupement-header .title {
            display: flex;
            align-items: center;
            gap: 12px;
        }
        .groupement-header .numero {
            width: 38px;
            height: 38px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea, #764ba2);
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 800;
            font-size: 1em;
        }
        .groupement-header h3 {
            margin: 0;
            font-size: 1.1em;
        }
        .groupement-header .meta {
            font-size: 0.82em;
            opacity: 0.75;
            margin-top: 2px;
        }
        .depart-badge {
            background: linear-gradient(135deg, #f59e0b, #ef4444);
            color: white;
            padding: 6px 14px;
            border-radius: 20px;
            font-weight: 700;
            font-size: 1em;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        /* ── Tableau des véhicules ────────────────────────── */
        .table-wrapper { padding: 0; overflow-x: auto; }
        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.9em;
        }
        thead tr {
            background: #f8fafc;
        }
        thead th {
            padding: 14px 16px;
            text-align: left;
            font-weight: 700;
            font-size: 0.78em;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            color: #64748b;
            border-bottom: 2px solid #e2e8f0;
            white-space: nowrap;
        }
        tbody tr {
            border-bottom: 1px solid #f1f5f9;
            transition: background 0.15s;
        }
        tbody tr:last-child { border-bottom: none; }
        tbody tr:hover { background: #f8fafc; }
        tbody td {
            padding: 14px 16px;
            color: #374151;
            vertical-align: middle;
        }

        /* Colonne heure départ */
        .heure-depart {
            font-weight: 800;
            font-size: 1.05em;
            color: #7c3aed;
            white-space: nowrap;
        }

        /* Colonne véhicule */
        .vehicule-cell {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .vehicule-icon {
            width: 34px;
            height: 34px;
            border-radius: 8px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 0.85em;
            flex-shrink: 0;
        }
        .vehicule-ref { font-weight: 700; color: #1e293b; }
        .vehicule-type {
            display: inline-block;
            font-size: 0.72em;
            font-weight: 600;
            padding: 2px 8px;
            border-radius: 10px;
            margin-top: 3px;
        }
        .type-D, .type-Diesel, .type-DIESEL  { background: #fef3c7; color: #92400e; }
        .type-ES, .type-Essence              { background: #dcfce7; color: #166534; }
        .type-H,  .type-Hybride              { background: #dbeafe; color: #1e40af; }
        .type-EL, .type-Electrique           { background: #f0fdf4; color: #15803d; }
        .type-default                        { background: #f1f5f9; color: #475569; }

        /* Colonne hôtels */
        .hotels-list {
            display: flex;
            flex-wrap: wrap;
            gap: 6px;
        }
        .hotel-badge {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            background: #f0f4ff;
            color: #374151;
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 0.82em;
            font-weight: 500;
            border: 1px solid #e0e7ff;
        }
        .hotel-badge .ordre {
            width: 18px;
            height: 18px;
            border-radius: 50%;
            background: #667eea;
            color: white;
            font-size: 0.72em;
            font-weight: 700;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .hotel-badge .passagers {
            color: #667eea;
            font-size: 0.8em;
        }

        /* Colonne km */
        .km-cell {
            font-weight: 700;
            color: #059669;
            white-space: nowrap;
        }
        .km-sub {
            font-size: 0.78em;
            color: #94a3b8;
            font-weight: 400;
        }

        /* Colonne heure retour */
        .retour-cell {
            font-weight: 700;
            color: #dc2626;
            white-space: nowrap;
        }
        .retour-sub {
            font-size: 0.78em;
            color: #94a3b8;
            font-weight: 400;
        }

        @media (max-width: 900px) {
            body { margin-left: 0; padding: 16px; }
        }
    </style>
</head>
<body>
<%@ include file="../includes/sidebar.jsp" %>

<%
    DateTimeFormatter FMT_TIME = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    String datePlanification   = (String) request.getAttribute("datePlanification");

    @SuppressWarnings("unchecked")
    List<GroupementDTO> groupements = (List<GroupementDTO>) request.getAttribute("groupements");

    int totalGroupements   = groupements != null ? groupements.size() : 0;
    int totalReservations  = 0;
    int totalVehicules     = 0;
    if (groupements != null) {
        for (GroupementDTO g : groupements) {
            totalReservations += g.getTotalReservations();
            totalVehicules    += g.getTrajets().size();
        }
    }
%>

<div class="container">

    <!-- En-tête -->
    <div class="page-header">
        <div>
            <h2><i class="fas fa-car-side" style="margin-right:10px"></i>Groupements des Voitures</h2>
            <p>
                <i class="fas fa-calendar-day" style="margin-right:6px"></i>
                <%= datePlanification != null ? datePlanification : "—" %>
                &nbsp;·&nbsp;
                <i class="fas fa-layer-group" style="margin-right:6px"></i>
                <%= totalGroupements %> groupement<%= totalGroupements > 1 ? "s" : "" %>
            </p>
        </div>
        <div class="header-actions">
            <a href="<%= request.getContextPath() %>/groupement" class="btn btn-outline">
                <i class="fas fa-arrow-left"></i> Nouvelle date
            </a>
            <a href="<%= request.getContextPath() %>/" class="btn btn-white">
                <i class="fas fa-home"></i> Accueil
            </a>
        </div>
    </div>

    <!-- Résumé global -->
    <div class="summary-row">
        <div class="summary-card">
            <div class="icon icon-purple"><i class="fas fa-layer-group"></i></div>
            <div>
                <div class="value"><%= totalGroupements %></div>
                <div class="label">Groupements</div>
            </div>
        </div>
        <div class="summary-card">
            <div class="icon icon-green"><i class="fas fa-calendar-check"></i></div>
            <div>
                <div class="value"><%= totalReservations %></div>
                <div class="label">Réservations</div>
            </div>
        </div>
        <div class="summary-card">
            <div class="icon icon-orange"><i class="fas fa-car"></i></div>
            <div>
                <div class="value"><%= totalVehicules %></div>
                <div class="label">Voitures mobilisées</div>
            </div>
        </div>
    </div>

    <!-- État vide -->
    <%
    if (groupements == null || groupements.isEmpty()) {
    %>
    <div class="empty-state">
        <i class="fas fa-car-side"></i>
        <h3>Aucun groupement trouvé</h3>
        <p>Il n'y a pas de réservations pour cette date, ou aucune n'a pu être regroupée.</p>
    </div>
    <%
    } else {
        for (GroupementDTO grp : groupements) {
            String hDept = grp.getHeureDepart() != null ? grp.getHeureDepart().format(FMT_TIME) : "--:--";
    %>

    <!-- Carte de groupement -->
    <div class="groupement-card">

        <div class="groupement-header">
            <div class="title">
                <div class="numero"><%= grp.getNumeroGroupe() %></div>
                <div>
                    <h3>Groupement <%= grp.getNumeroGroupe() %></h3>
                    <div class="meta">
                        <%= grp.getTotalReservations() %> réservation<%= grp.getTotalReservations() > 1 ? "s" : "" %>
                        &nbsp;·&nbsp;
                        <%= grp.getTrajets().size() %> voiture<%= grp.getTrajets().size() > 1 ? "s" : "" %>
                        &nbsp;·&nbsp;
                        <%= grp.getTotalPassagers() %> passager<%= grp.getTotalPassagers() > 1 ? "s" : "" %>
                    </div>
                </div>
            </div>
            <div class="depart-badge">
                <i class="fas fa-plane-departure"></i> Départ&nbsp;<%= hDept %>
            </div>
        </div>

        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th><i class="fas fa-calendar-check" style="margin-right:5px"></i>Heure réservation</th>
                        <th><i class="fas fa-car" style="margin-right:5px"></i>Véhicule</th>
                        <th><i class="fas fa-hotel" style="margin-right:5px"></i>Réservations (Hôtels)</th>
                        <th><i class="fas fa-road" style="margin-right:5px"></i>Km parcouru</th>
                        <th><i class="fas fa-plane-arrival" style="margin-right:5px"></i>Heure retour (aéroport)</th>
                    </tr>
                </thead>
                <tbody>
                <%
                    List<TrajetVehiculeDTO> trajets = grp.getTrajets();
                    if (trajets == null || trajets.isEmpty()) {
                %>
                    <tr>
                        <td colspan="5" style="text-align:center;color:#94a3b8;padding:24px;">
                            <i class="fas fa-exclamation-circle"></i> Aucun véhicule attribué à ce groupe.
                        </td>
                    </tr>
                <%
                    } else {
                        for (TrajetVehiculeDTO trajet : trajets) {

                            String heureReservation = "--:--";
                            if (trajet.getListeReservations() != null && !trajet.getListeReservations().isEmpty()) {
                                java.time.LocalDateTime maxReservation = null;
                                for (ReservationAffecteeDTO res : trajet.getListeReservations()) {
                                    if (res != null && res.getDateHeureArrive() != null
                                            && (maxReservation == null || res.getDateHeureArrive().isAfter(maxReservation))) {
                                        maxReservation = res.getDateHeureArrive();
                                    }
                                }
                                if (maxReservation != null) {
                                    heureReservation = maxReservation.format(FMT_TIME);
                                }
                            }
                            String heureRetour  = trajet.getHeureRetourPrevue() != null ? trajet.getHeureRetourPrevue().format(FMT_TIME) : "--:--";

                            // Badge type carburant
                            String typeRaw   = trajet.getTypeVehicule() != null ? trajet.getTypeVehicule() : "";
                            String typeLabel;
                            String typeCss;
                            switch (typeRaw.toUpperCase()) {
                                case "D":  case "DIESEL":  typeLabel = "Diesel";     typeCss = "type-D";  break;
                                case "ES": case "ESSENCE": typeLabel = "Essence";    typeCss = "type-ES"; break;
                                case "H":  case "HYBRIDE": typeLabel = "Hybride";    typeCss = "type-H";  break;
                                case "EL":                 typeLabel = "Électrique"; typeCss = "type-EL"; break;
                                default:                   typeLabel = typeRaw;      typeCss = "type-default";
                            }

                            double displayDistanceParcourue = trajet.getDistanceParcourue();
                            if (displayDistanceParcourue <= 0 && trajet.getListeReservations() != null) {
                                double sommeDistance = 0;
                                for (ReservationAffecteeDTO res : trajet.getListeReservations()) {
                                    if (res != null) {
                                        sommeDistance += res.getDistance();
                                    }
                                }
                                displayDistanceParcourue = sommeDistance;
                            }

                            double displayDistanceTotale = trajet.getDistanceTotale();
                            if (displayDistanceTotale <= 0) {
                                displayDistanceTotale = displayDistanceParcourue;
                            }
                %>
                    <tr>
                        <!-- Heure de reservation -->
                        <td><span class="heure-depart"><i class="fas fa-calendar-check" style="margin-right:5px;opacity:.6"></i><%= heureReservation %></span></td>

                        <!-- Véhicule -->
                        <td>
                            <div class="vehicule-cell">
                                <div class="vehicule-icon"><i class="fas fa-car"></i></div>
                                <div>
                                    <div class="vehicule-ref"><%= trajet.getVehiculeReference() %></div>
                                    <span class="vehicule-type <%= typeCss %>"><%= typeLabel %></span>
                                    &nbsp;
                                    <span style="font-size:0.78em;color:#94a3b8"><%= trajet.getCapacite() %> places</span>
                                </div>
                            </div>
                        </td>

                        <!-- Hôtels / Réservations -->
                        <td>
                            <div class="hotels-list">
                            <%
                                List<ReservationAffecteeDTO> resList = trajet.getListeReservations();
                                if (resList != null) {
                                    for (ReservationAffecteeDTO res : resList) {
                                        String nomHotel = res.getNomHotel() != null ? res.getNomHotel() : "Hôtel ?";
                                        String heurePassage = res.getHeurePassage() != null ? res.getHeurePassage().format(FMT_TIME) : "";
                            %>
                                <span class="hotel-badge">
                                    <span class="ordre"><%= res.getOrdreVisite() %></span>
                                    <%= nomHotel %>
                                    <% if (!heurePassage.isEmpty()) { %>
                                        <span class="passagers">(<%= heurePassage %>)</span>
                                    <% } %>
                                    <span class="passagers"><i class="fas fa-user"></i>&nbsp;<%= res.getNbPassager() %></span>
                                </span>
                            <%
                                    }
                                }
                            %>
                            </div>
                        </td>

                        <!-- Km parcouru -->
                        <td>
                            <span class="km-cell">
                                <i class="fas fa-road" style="margin-right:4px;opacity:.6"></i>
                                <%= String.format("%.1f", displayDistanceParcourue) %> km
                            </span>
                            <div class="km-sub">
                                total A/R : <%= String.format("%.1f", displayDistanceTotale) %> km
                            </div>
                        </td>

                        <!-- Heure retour -->
                        <td>
                            <span class="retour-cell">
                                <i class="fas fa-plane-arrival" style="margin-right:4px;opacity:.6"></i>
                                <%= heureRetour %>
                            </span>
                            <div class="retour-sub">estimée à l'aéroport</div>
                        </td>
                    </tr>
                <%
                        }
                    }
                %>
                </tbody>
            </table>
        </div>
    </div><!-- /.groupement-card -->

    <%
        } // end for groupements
    } // end if
    %>

</div><!-- /.container -->
</body>
</html>
