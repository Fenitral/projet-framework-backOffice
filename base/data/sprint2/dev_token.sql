-- Tokens de test pour le schéma dev
-- Ces tokens expirent dans 24h à partir de l'insertion
INSERT INTO dev.token_expiration(token, expiration)
VALUES
    ('550e8400-e29b-41d4-a716-446655440000', NOW() + INTERVAL '24 hours'),
    ('6ba7b810-9dad-11d1-80b4-00c04fd430c8', NOW() + INTERVAL '48 hours');
