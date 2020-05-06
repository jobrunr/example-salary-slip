resource "google_sql_database_instance" "postgres" {
  database_version = "POSTGRES_11"

  settings {
    tier = "db-g1-small"
    database_flags {
      name = "max_connections"
      value = 100
    }
  }
  timeouts {
    delete = "10m"
  }
}

resource "google_sql_user" "users" {
  name = "jobrunr"
  instance = google_sql_database_instance.postgres.name
  password = "changeme"
}

resource "google_sql_database" "database" {
  name = "jobrunr"
  instance = google_sql_database_instance.postgres.name
}

#####################################################################
# Output for K8S
#####################################################################
output "cloudsql_db_name" {
  value = google_sql_database.database.name
  sensitive = true
}

output "cloudsql_db_user" {
  value = google_sql_user.users.name
  sensitive = true
}

output "cloudsql_db_password" {
  value = google_sql_user.users.password
  sensitive = true
}

output "cloudsql_db_instance" {
  value = "${var.project}:${var.region}:${google_sql_database_instance.postgres.name}"
  sensitive = true
}