/**
 * Script pour la gestion du sidebar
 */
document.addEventListener('DOMContentLoaded', function() {
    // Récupération de l'URL actuelle
    const currentPath = window.location.pathname;
    
    // Sélection de tous les liens de navigation
    const navLinks = document.querySelectorAll('.nav-link');
    
    // Fonction pour marquer le lien actif
    function setActiveLink() {
        navLinks.forEach(link => {
            link.classList.remove('active');
            
            // Vérification si l'href du lien correspond à l'URL actuelle
            const linkPath = new URL(link.href).pathname;
            
            if (linkPath === currentPath || 
                (currentPath.includes(linkPath) && linkPath !== '/')) {
                link.classList.add('active');
            }
        });
    }
    
    // Appel de la fonction au chargement
    setActiveLink();
    
    // Ajout d'effets visuels supplémentaires
    navLinks.forEach(link => {
        // Effet ripple au clic
        link.addEventListener('click', function(e) {
            const ripple = document.createElement('span');
            const rect = this.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height);
            const x = e.clientX - rect.left - size / 2;
            const y = e.clientY - rect.top - size / 2;
            
            ripple.style.cssText = `
                width: ${size}px;
                height: ${size}px;
                left: ${x}px;
                top: ${y}px;
                position: absolute;
                border-radius: 50%;
                background: rgba(255,255,255,0.3);
                transform: scale(0);
                animation: ripple 0.6s linear;
                pointer-events: none;
            `;
            
            this.style.position = 'relative';
            this.style.overflow = 'hidden';
            this.appendChild(ripple);
            
            setTimeout(() => {
                ripple.remove();
            }, 600);
        });
        
        // Animation au survol
        link.addEventListener('mouseenter', function() {
            this.style.transform = 'translateX(5px)';
        });
        
        link.addEventListener('mouseleave', function() {
            if (!this.classList.contains('active')) {
                this.style.transform = 'translateX(0)';
            }
        });
    });
    
    // Ajout de l'animation CSS pour l'effet ripple
    const style = document.createElement('style');
    style.textContent = `
        @keyframes ripple {
            to {
                transform: scale(2);
                opacity: 0;
            }
        }
    `;
    document.head.appendChild(style);
});