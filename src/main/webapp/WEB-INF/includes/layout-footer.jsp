            </div>
        </div>
    </div>
    
    <!-- Scripts -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        $(document).ready(function() {
            // Animation pour les liens de navigation
            $('.nav-link').hover(
                function() {
                    $(this).find('i').addClass('fa-spin');
                },
                function() {
                    $(this).find('i').removeClass('fa-spin');
                }
            );
            
            // Auto-hide des messages d'alerte
            $('.alert').delay(5000).fadeOut(500);
            
            // Confirmation pour les suppressions
            $('.btn-danger').click(function(e) {
                if (!confirm('Êtes-vous sûr de vouloir supprimer cet élément ?')) {
                    e.preventDefault();
                }
            });
        });
    </script>
</body>
</html>