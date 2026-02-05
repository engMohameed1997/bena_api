#!/bin/bash
# Database Backup Script for Bena-API

BACKUP_DIR="/home/hussain/bena_api/backups"
DATE=$(date +%Y-%m-%d_%H-%M-%S)
CONTAINER_NAME="bena-db"
RETENTION_DAYS=7

# Load environment variables
source /home/hussain/bena_api/.env

# Create backup directory
mkdir -p ${BACKUP_DIR}

# Create backup
echo "Starting database backup at ${DATE}..."
docker exec ${CONTAINER_NAME} pg_dump -U ${POSTGRES_USER} ${POSTGRES_DB} > ${BACKUP_DIR}/backup_${DATE}.sql

# Check if backup was successful
if [ $? -eq 0 ]; then
    echo "Backup completed successfully: backup_${DATE}.sql"
    
    # Compress the backup
    gzip ${BACKUP_DIR}/backup_${DATE}.sql
    echo "Backup compressed: backup_${DATE}.sql.gz"
    
    # Delete backups older than RETENTION_DAYS
    find ${BACKUP_DIR} -name "backup_*.sql.gz" -mtime +${RETENTION_DAYS} -delete
    echo "Old backups cleaned up (retention: ${RETENTION_DAYS} days)"
else
    echo "ERROR: Backup failed!"
    exit 1
fi

echo "Backup process completed."
