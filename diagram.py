from diagrams import Diagram, Cluster
from diagrams.k8s.network import Service
from diagrams.k8s.compute import Deployment, StatefulSet, Job
from diagrams.k8s.podconfig import ConfigMap

with Diagram("Środowisko Testowe", show=False): #

    gke = Cluster("Google Kubernetes Engine")
    with gke:
        node1 = Cluster("Węzeł 1")
        node2 = Cluster("Węzeł 2")
        with node1:
            grafana = Deployment("Grafana")
            influx = StatefulSet("InfluxDB")
            scenario = ConfigMap("Konfiguracja\n Scenariusza")
            k6 = Job("K6")
            postgres = StatefulSet("Postgres")
        with node2:
            application = Deployment("Instancja\n aplikacji")
            app_svc = Service()
        scenario >> k6 >> app_svc >> application >> postgres
        k6 >> influx >> grafana



# with Diagram("")