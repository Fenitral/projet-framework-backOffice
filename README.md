# TestApp-FrameWork
Test App Framework
# genere le token
java -Ddb.url="jdbc:postgresql://localhost:5432/reservation_voiture?currentSchema=dev" -Ddb.user="postgres" -Ddb.password="postgres" -cp build/WEB-INF/classes;build/WEB-INF/lib/* com.cousin.Main