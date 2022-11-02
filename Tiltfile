
app_name = "fastapi"

docker_build('localhost:5005/{}-app'.format(app_name),
	context=".",
	dockerfile='.docker/images/{app_name}/Dockerfile'
)

k8s_yaml(kustomize('./manifests/overlays/dev/{app_name}))