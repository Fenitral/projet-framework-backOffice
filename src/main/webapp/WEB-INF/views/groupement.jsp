<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Groupement des Voitures</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * { box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Arial, sans-serif;
            margin: 0;
            padding: 0;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            background: #f5f7fa;
        }
        .container {
            width: 100%;
            max-width: 500px;
            padding: 20px;
        }
        .form-section {
            background: white;
            padding: 40px;
            border-radius: 16px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.15);
        }
        .form-section h2 {
            margin: 0 0 8px 0;
            color: #333;
            text-align: center;
            font-size: 1.5em;
        }
        .form-section h2 i {
            color: #667eea;
            margin-right: 10px;
        }
        .subtitle {
            text-align: center;
            color: #888;
            font-size: 0.9em;
            margin-bottom: 28px;
        }
        .form-group {
            margin-bottom: 22px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #555;
        }
        label i {
            margin-right: 8px;
            color: #667eea;
        }
        input[type="date"],
        input[type="time"] {
            width: 100%;
            padding: 14px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 16px;
            transition: border-color 0.3s, box-shadow 0.3s;
        }
        input[type="date"]:focus,
        input[type="time"]:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
        }
        button {
            width: 100%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 16px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 16px;
            font-weight: 600;
            transition: transform 0.2s, box-shadow 0.2s;
            margin-top: 6px;
        }
        button:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
        }
        button i {
            margin-right: 10px;
        }
        .info-box {
            background: #f0f4ff;
            border-left: 4px solid #667eea;
            padding: 12px 16px;
            border-radius: 8px;
            margin-bottom: 24px;
            font-size: 0.88em;
            color: #444;
            line-height: 1.5;
        }
        .info-box i {
            color: #667eea;
            margin-right: 6px;
        }
    </style>
</head>
<body>
    <%@ include file="../includes/sidebar.jsp" %>
    <div class="container">
        <div class="form-section">
            <h2><i class="fas fa-car-side"></i>Groupement des Voitures</h2>
            <p class="subtitle">Visualisez les groupements de réservations par fenêtre horaire</p>

            <div class="info-box">
                <i class="fas fa-info-circle"></i>
                Les réservations sont regroupées par fenêtres de <strong>30 minutes</strong>.
                Un véhicule peut aussi partir à son <strong>heure de retour</strong> s'il revient pendant la fenêtre.
                Les réservations non affectées sont <strong>reconsidérées au groupe suivant</strong>.
            </div>

            <form action="<%= request.getContextPath() %>/groupementResultats" method="get">
                <div class="form-group">
                    <label for="dateStr"><i class="fas fa-calendar-day"></i>Date :</label>
                    <input type="date" id="dateStr" name="dateStr" value="${today}" required>
                </div>

                <div class="form-group">
                    <label for="heureStr"><i class="fas fa-clock"></i>Filtre heure :</label>
                    <input type="time" id="heureStr" name="heureStr">
                </div>

                <button type="submit">
                    <i class="fas fa-layer-group"></i>Voir les groupements
                </button>
            </form>
        </div>
    </div>
</body>
</html>
