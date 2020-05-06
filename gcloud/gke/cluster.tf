#####################################################################
# GKE Cluster
#####################################################################
resource "google_container_cluster" "jobrunr-tutorial-kubernetes" {
  name               = "jobrunr-tutorial-kubernetes"
  location           = var.region
  initial_node_count = 1

  master_auth {
    username = var.username
    password = var.password
  }

  node_config {
    machine_type = "n1-standard-2"
    oauth_scopes = [
      "https://www.googleapis.com/auth/devstorage.read_only",
      "https://www.googleapis.com/auth/logging.write",
      "https://www.googleapis.com/auth/monitoring",
      "https://www.googleapis.com/auth/service.management.readonly",
      "https://www.googleapis.com/auth/servicecontrol",
      "https://www.googleapis.com/auth/trace.append",
      "https://www.googleapis.com/auth/compute",
      "https://www.googleapis.com/auth/cloud-platform", //needed for sqlservice
      "https://www.googleapis.com/auth/sqlservice.admin"
    ]
  }
}

#####################################################################
# Output for K8S
#####################################################################
output "client_certificate" {
  value     = google_container_cluster.jobrunr-tutorial-kubernetes.master_auth[0].client_certificate
  sensitive = true
}

output "client_key" {
  value     = google_container_cluster.jobrunr-tutorial-kubernetes.master_auth[0].client_key
  sensitive = true
}

output "cluster_ca_certificate" {
  value     = google_container_cluster.jobrunr-tutorial-kubernetes.master_auth[0].cluster_ca_certificate
  sensitive = true
}

output "host" {
  value     = google_container_cluster.jobrunr-tutorial-kubernetes.endpoint
  sensitive = true
}