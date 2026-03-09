<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Planification des Assignations</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * { box-sizing: border-box; }
        body { 
            font-family: 'Segoe UI', Arial, sans-serif; 
            margin: 0; 
            padding: 0;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
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
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
        }
        .form-section h2 {
            margin: 0 0 30px 0;
            color: #333;
            text-align: center;
            font-size: 1.5em;
        }
        .form-section h2 i {
            color: #667eea;
            margin-right: 10px;
        }
        .form-group {
            margin-bottom: 20px;
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
        input[type="date"], input[type="time"] { 
            width: 100%; 
            padding: 14px; 
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 16px;
            transition: border-color 0.3s, box-shadow 0.3s;
        }
        input[type="date"]:focus, input[type="time"]:focus {
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
            margin-top: 10px;
        }
        button:hover { 
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
        }
        button i {
            margin-right: 10px;
        }
        .back-link {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: white;
            text-decoration: none;
            font-weight: 500;
        }
        .back-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="form-section">
            <h2><i class="fas fa-calendar-alt"></i>Planification des Assignations</h2>
            <form action="<%= request.getContextPath() %>/affichageResultats" method="get">
                <div class="form-group">
                    <label for="date"><i class="fas fa-calendar-day"></i>Date de planification :</label>
                    <input type="date" id="date" name="dateStr" value="${today}" required>
                </div>
                
                <div class="form-group">
                    <label for="heureDepart"><i class="fas fa-clock"></i>Heure de départ :</label>
                    <input type="time" id="heureDepart" name="heureDepart" value="08:00" required>
                </div>
                
                <button type="submit">
                    <i class="fas fa-cogs"></i>Générer planification
                </button>
            </form>
        </div>
        <a href="<%= request.getContextPath() %>/" class="back-link">
            <i class="fas fa-arrow-left"></i> Retour à l'accueil
        </a>
    </div>
</body>
</html>
