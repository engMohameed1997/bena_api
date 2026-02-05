#!/bin/bash
# SSL Certificate Renewal Script

echo "Renewing SSL certificates..."

cd /home/hussain/bena_api

# Renew certificates
docker compose -f docker-compose.prod.yml run --rm certbot renew

# Reload Nginx
docker compose -f docker-compose.prod.yml exec nginx nginx -s reload

echo "Certificate renewal completed."
