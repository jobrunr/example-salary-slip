resource "kubernetes_deployment" "jobrunr-tutorial" {
  metadata {
    name = "jobrunr"

    labels = {
      app = "jobrunr"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "jobrunr"
      }
    }

    template {
      metadata {
        labels = {
          app = "jobrunr"
        }
      }

      spec {

        container {
          image = "gcr.io/jobrunr-tutorial-kubernetes/jobrunr-example-paycheck:1.0"
          name = "jobrunr"

          port {
            container_port = 8000
          }
          port {
            container_port = 8080
          }

          env {
            name = "CLOUD_SQL_INSTANCE"
            value = var.cloudsql_instance
          }

          env {
            name = "DB_NAME"
            value = var.cloudsql_db_name
          }

          env {
            name = "DB_USER"
            value = var.cloudsql_db_user
          }

          env {
            name = "DB_PASS"
            value = var.cloudsql_db_password
          }

          resources {
            limits {
              cpu = "0.5"
              memory = "1024Mi"
            }
            requests {
              cpu = "250m"
              memory = "512Mi"
            }
          }
        }
      }
    }
  }
}