resource "kubernetes_service" "jobrunr-tutorial" {
  metadata {
    name = "jobrunr-tutorial"
  }
  spec {
    selector = {
      app = kubernetes_deployment.jobrunr-tutorial.spec.0.template.0.metadata[0].labels.app
    }
    port {
      name = "dashboard"
      port = 8000
      target_port = 8000
    }
    port {
      name = "rest-api"
      port = 8080
      target_port = 8080
    }

    type = "LoadBalancer"
  }
}


output "loadbalancer_ip" {
  value = kubernetes_service.jobrunr-tutorial.load_balancer_ingress[0].ip
}