<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Réservations</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
        }
        h1 {
            color: white;
            text-align: center;
            margin-bottom: 30px;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
        }
        .card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            margin-bottom: 30px;
        }
        .card h2 {
            color: #333;
            margin-bottom: 20px;
            border-bottom: 2px solid #667eea;
            padding-bottom: 10px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: 600;
            color: #555;
        }
        .form-group input, .form-group select {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 8px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        .form-group input:focus, .form-group select:focus {
            outline: none;
            border-color: #667eea;
        }
        .form-row {
            display: flex;
            gap: 15px;
        }
        .form-row .form-group {
            flex: 1;
        }
        .btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.3s, box-shadow 0.3s;
        }
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
        }
        .btn-success {
            background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }
        th {
            background: #f8f9fa;
            font-weight: 600;
            color: #333;
        }
        tr:hover {
            background: #f8f9fa;
        }
        .loading {
            text-align: center;
            padding: 40px;
            color: #666;
        }
        .loading::after {
            content: '...';
            animation: dots 1.5s steps(5, end) infinite;
        }
        @keyframes dots {
            0%, 20% { content: '.'; }
            40% { content: '..'; }
            60%, 100% { content: '...'; }
        }
        .alert {
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
        }
        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .badge {
            display: inline-block;
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }
        .badge-info {
            background: #e7f3ff;
            color: #0066cc;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🏨 Système de Réservation d'Hôtels</h1>
        
        <!-- Formulaire de réservation -->
        <div class="card">
            <h2>Nouvelle Réservation</h2>
            <div id="alertContainer"></div>
            <form id="reservationForm">
                <div class="form-row">
                    <div class="form-group">
                        <label for="client">Nom du client</label>
                        <input type="text" id="client" name="client" required placeholder="Entrez le nom du client">
                    </div>
                    <div class="form-group">
                        <label for="nbPeople">Nombre de personnes</label>
                        <input type="number" id="nbPeople" name="nbPeople" min="1" required placeholder="Ex: 2">
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label for="dateheure">Date et heure</label>
                        <input type="datetime-local" id="dateheure" name="dateheure" required>
                    </div>
                    <div class="form-group">
                        <label for="hotelId">Hôtel</label>
                        <select id="hotelId" name="hotelId" required>
                            <option value="">Sélectionnez un hôtel</option>
                        </select>
                    </div>
                </div>
                <button type="submit" class="btn btn-success">✓ Créer la réservation</button>
            </form>
        </div>
        
        <!-- Liste des réservations -->
        <div class="card">
            <h2>Liste des Réservations</h2>
            <button class="btn" onclick="loadReservations()">🔄 Actualiser</button>
            <div id="reservationsTable">
                <p class="loading">Chargement des réservations</p>
            </div>
        </div>
    </div>

    <script>
        const API_BASE = window.location.pathname.replace(/\/[^\/]*$/, '');

        // Charger les hôtels au démarrage
        document.addEventListener('DOMContentLoaded', function() {
            loadHotels();
            loadReservations();
        });

        // Charger la liste des hôtels
        function loadHotels() {
            fetch(API_BASE + '/api/hotels')
                .then(response => response.json())
                .then(result => {
                    const select = document.getElementById('hotelId');
                    if (result.data && Array.isArray(result.data)) {
                        result.data.forEach(hotel => {
                            const option = document.createElement('option');
                            option.value = hotel.hotelId;
                            option.textContent = hotel.name + ' - ' + hotel.addresse;
                            select.appendChild(option);
                        });
                    }
                })
                .catch(error => {
                    console.error('Erreur lors du chargement des hôtels:', error);
                });
        }

        // Charger les réservations
        function loadReservations() {
            const container = document.getElementById('reservationsTable');
            container.innerHTML = '<p class="loading">Chargement des réservations</p>';
            
            fetch(API_BASE + '/api/reservations')
                .then(response => response.json())
                .then(result => {
                    if (result.data && Array.isArray(result.data) && result.data.length > 0) {
                        let html = '<table>';
                        html += '<thead><tr>';
                        html += '<th>ID</th>';
                        html += '<th>Client</th>';
                        html += '<th>Personnes</th>';
                        html += '<th>Date & Heure</th>';
                        html += '<th>Hôtel</th>';
                        html += '</tr></thead>';
                        html += '<tbody>';
                        
                        result.data.forEach(reservation => {
                            const date = new Date(reservation.dateheure);
                            const formattedDate = date.toLocaleString('fr-FR');
                            const hotelName = reservation.hotel ? reservation.hotel.name : 'N/A';
                            
                            html += '<tr>';
                            html += '<td><span class="badge badge-info">#' + reservation.reservationId + '</span></td>';
                            html += '<td>' + escapeHtml(reservation.client) + '</td>';
                            html += '<td>' + reservation.nbPeople + '</td>';
                            html += '<td>' + formattedDate + '</td>';
                            html += '<td>' + escapeHtml(hotelName) + '</td>';
                            html += '</tr>';
                        });
                        
                        html += '</tbody></table>';
                        container.innerHTML = html;
                    } else {
                        container.innerHTML = '<p style="text-align: center; color: #666; padding: 40px;">Aucune réservation trouvée.</p>';
                    }
                })
                .catch(error => {
                    console.error('Erreur:', error);
                    container.innerHTML = '<p style="text-align: center; color: #dc3545; padding: 40px;">Erreur lors du chargement des réservations.</p>';
                });
        }

        // Soumettre le formulaire
        document.getElementById('reservationForm').addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            const params = new URLSearchParams();
            formData.forEach((value, key) => params.append(key, value));
            
            fetch(API_BASE + '/api/reservations', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: params.toString()
            })
            .then(response => response.json())
            .then(result => {
                if (result.status === 'error') {
                    showAlert('Erreur: ' + (result.data || 'Échec de la création'), 'error');
                } else {
                    showAlert('Réservation créée avec succès!', 'success');
                    document.getElementById('reservationForm').reset();
                    loadReservations();
                }
            })
            .catch(error => {
                console.error('Erreur:', error);
                showAlert('Erreur lors de la création de la réservation.', 'error');
            });
        });

        // Afficher une alerte
        function showAlert(message, type) {
            const container = document.getElementById('alertContainer');
            container.innerHTML = '<div class="alert alert-' + type + '">' + escapeHtml(message) + '</div>';
            setTimeout(() => {
                container.innerHTML = '';
            }, 5000);
        }

        // Échapper le HTML
        function escapeHtml(text) {
            if (!text) return '';
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }
    </script>
</body>
</html>
