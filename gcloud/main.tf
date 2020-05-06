#####################################################################
# Variables
#####################################################################
variable "project" {
  default = "jobrunr-tutorial-kubernetes"
}
variable "region" {
  default = "europe-west1"
}
variable "username" {
  default = "admin"
}
variable "password" {
  default = "cluster-password-change-me"
}

#####################################################################
# Modules
#####################################################################
module "gke" {
  source = "./gke"
  project = var.project
  region = var.region
  username = var.username
  password = var.password
}

module "k8s" {
  source = "./k8s"
  host = module.gke.host
  username = var.username
  password = var.password

  client_certificate = module.gke.client_certificate
  client_key = module.gke.client_key
  cluster_ca_certificate = module.gke.cluster_ca_certificate
  cloudsql_instance = module.gke.cloudsql_db_instance
  cloudsql_db_name = module.gke.cloudsql_db_name
  cloudsql_db_user = module.gke.cloudsql_db_user
  cloudsql_db_password = module.gke.cloudsql_db_password
}