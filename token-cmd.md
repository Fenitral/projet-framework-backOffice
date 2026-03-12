<!-- Ao amn back io no atao -->
java -Ddb.url="jdbc:postgresql://localhost:5432/reservation_voiture?currentSchema=dev" -Ddb.user="postgres" -Ddb.password="postgres" -cp build/WEB-INF/classes;build/WEB-INF/lib/* com.cousin.Main generate 24

<!-- Migenerer token -->

<!-- Ao amn front: -->
D:\etudes\Mr_Naina\Framework\Projet\projet-framework\FrontOffice>curl -H "Authorization: Bearer cd08c8e8-7155-45d8-a6db-e7be3117412b" http://localhost:8081/reservation/api/reservation/list/


String token = "cd08c8e8-7155-45d8-a6db-e7be3117412b";
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);
            org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(apiUrl, org.springframework.http.HttpMethod.GET, entity, String.class);
            String json = response.getBody();