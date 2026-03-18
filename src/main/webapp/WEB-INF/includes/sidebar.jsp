<%
    String contextPath = request.getContextPath();
    String requestUri = request.getRequestURI();
    String currentPath = requestUri.startsWith(contextPath)
        ? requestUri.substring(contextPath.length())
        : requestUri;

    boolean isHome = "/".equals(currentPath) || "".equals(currentPath);
    boolean isPlanification = currentPath.startsWith("/planification") || currentPath.startsWith("/affichageResultats");
    boolean isVehiculeList = currentPath.startsWith("/vehicule/list");
    boolean isVehiculeForm = currentPath.startsWith("/vehicule/form");
    boolean isReservation = "/".equals(currentPath) || "".equals(currentPath) || currentPath.startsWith("/reservation/form");
    boolean isReservationList = currentPath.startsWith("/reservation/list");
    boolean isHotelList = currentPath.startsWith("/hotel/list");
    boolean isGroupement = currentPath.startsWith("/groupement");
%>

<style>
    @import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css');

    .sidebar {
        position: fixed;
        top: 0;
        left: 0;
        width: 280px;
        height: 100vh;
        padding: 24px 18px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: #e2e8f0;
        box-shadow: 14px 0 36px rgba(15, 23, 42, 0.22);
        overflow-y: auto;
        z-index: 1000;
    }

    .sidebar-header {
        margin-bottom: 28px;
        padding: 18px 16px;
        border: 1px solid rgba(148, 163, 184, 0.2);
        border-radius: 18px;
        background: rgba(255, 255, 255, 0.06);
        backdrop-filter: blur(6px);
    }

    .sidebar-header h3 {
        margin: 0;
        display: flex;
        align-items: center;
        gap: 10px;
        font-size: 1.2rem;
        font-weight: 700;
        letter-spacing: 0.02em;
        color: #f8fafc;
    }

    .sidebar-header i {
        width: 38px;
        height: 38px;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        border-radius: 12px;
        background: linear-gradient(135deg, #f59e0b 0%, #ef4444 100%);
        color: #fff;
        box-shadow: 0 12px 24px rgba(239, 68, 68, 0.28);
    }

    .sidebar-nav ul {
        margin: 0;
        padding: 0;
        list-style: none;
        display: flex;
        flex-direction: column;
        gap: 8px;
    }

    .sidebar-nav li {
        margin: 0;
    }

    .nav-link {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 14px 16px;
        border-radius: 14px;
        color: #cbd5e1;
        text-decoration: none;
        font-weight: 600;
        transition: background-color 0.2s ease, color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
    }

    .nav-link i {
        width: 18px;
        text-align: center;
        color: #f8fafc;
        opacity: 0.9;
    }

    .nav-link:hover {
        background: rgba(148, 163, 184, 0.16);
        color: #ffffff;
        transform: translateX(4px);
    }

    .nav-link.active {
        background: linear-gradient(135deg, #f59e0b 0%, #ef4444 100%);
        color: #ffffff;
        box-shadow: 0 16px 30px rgba(239, 68, 68, 0.24);
    }

    .nav-link.active i {
        color: #ffffff;
    }

    .sidebar + .main-content {
        margin-left: 280px;
        min-height: 100vh;
        padding: 32px;
        background: #f8fafc;
    }

    @media (max-width: 900px) {
        .sidebar {
            position: relative;
            width: 100%;
            height: auto;
            border-radius: 0 0 24px 24px;
        }

        .sidebar + .main-content {
            margin-left: 0;
            padding: 24px 18px;
        }
    }
</style>

<!-- Sidebar Navigation -->
<div class="sidebar">
    <div class="sidebar-header">
        <h3><i class="fas fa-building"></i> BackOffice</h3>
    </div>

    <nav class="sidebar-nav">
        <ul>
            <li><a href="<%= contextPath %>/" class="nav-link <%= isHome ? "active" : "" %>"><i class="fas fa-home"></i> Accueil</a></li>
            <li><a href="<%= contextPath %>/planification" class="nav-link <%= isPlanification ? "active" : "" %>"><i class="fas fa-calendar-alt"></i> Planification</a></li>
            <li><a href="<%= contextPath %>/vehicule/list" class="nav-link <%= isVehiculeList ? "active" : "" %>"><i class="fas fa-car"></i>Véhicules</a></li>
            <li><a href="<%= contextPath %>/reservation/list" class="nav-link <%= isReservationList ? "active" : "" %>"><i class="fas fa-list"></i>Réservations</a></li>
            <li><a href="<%= contextPath %>/hotel/list" class="nav-link <%= isHotelList ? "active" : "" %>"><i class="fas fa-hotel"></i>Hôtels</a></li>
            <li><a href="<%= contextPath %>/groupement" class="nav-link <%= isGroupement ? "active" : "" %>"><i class="fas fa-car-side"></i> Groupement des voitures</a></li>
        </ul>
    </nav>
</div>