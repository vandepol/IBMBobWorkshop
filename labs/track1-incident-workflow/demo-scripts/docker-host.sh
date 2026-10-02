# Sourced by the demo scripts. Terraform's Docker provider reads DOCKER_HOST, not the
# docker CLI's context, so on Colima / Podman point it at the active context's socket.
if [ -z "${DOCKER_HOST:-}" ] && [ ! -S /var/run/docker.sock ] && command -v docker > /dev/null 2>&1; then
    _ctx_host=$(docker context inspect --format '{{.Endpoints.docker.Host}}' 2> /dev/null || true)
    [ -n "$_ctx_host" ] && export DOCKER_HOST="$_ctx_host"
    unset _ctx_host
fi
