.PHONY: gen db_init db_clean db_restart help

# Default target
help:
	@echo "Available targets:"
	@echo "  gen         - Generate jOOQ code from database schema"
	@echo "  db_init     - Initialize database with Docker Compose"
	@echo "  db_clean    - Clean up database containers and volumes"
	@echo "  db_restart  - Restart database (clean + init)"
	@echo "  help        - Show this help message"

# Generate jOOQ code
gen:
	./gradlew generateJooq

# Initialize database
db_init:
	@echo "Starting database initialization..."
	docker compose up -d
	@echo "Waiting for PostgreSQL to be ready..."
	@sleep 15
	@echo "Loading schema..."
	docker compose exec -T postgres psql -U carparazzi_user -d carparazzi < ./entity/src/main/resources/schema.sql
	@echo "Database initialization completed!"

# Clean database (remove containers and volumes)
db_clean:
	@echo "Cleaning up database..."
	docker compose down -v
	@echo "Database cleanup completed!"

# Restart database (clean + init)
db_restart: db_clean db_init
	@echo "Database restart completed!"
