# Sourced by the demo scripts. Terraform's Docker provider reads DOCKER_HOST, not the
# docker CLI's context, so point it at a socket this user can actually use:
#   - Linux with rootless Podman (e.g. the Red Hat TechZone VM): the user's Podman socket.
#     /var/run/docker.sock from podman-docker points at root's socket, which a normal user can't use.
#   - Colima / Podman machine on macOS: the active docker context's socket.
if [ -z "${DOCKER_HOST:-}" ]; then
    _podman_sock="${XDG_RUNTIME_DIR:-/run/user/$(id -u)}/podman/podman.sock"
    if [ "$(uname)" = "Linux" ] && [ -S "$_podman_sock" ] && [ ! -w /var/run/docker.sock ]; then
        export DOCKER_HOST="unix://$_podman_sock"
    elif [ ! -S /var/run/docker.sock ] && command -v docker > /dev/null 2>&1; then
        _ctx_host=$(docker context inspect --format '{{.Endpoints.docker.Host}}' 2> /dev/null || true)
        [ -n "$_ctx_host" ] && export DOCKER_HOST="$_ctx_host"
        unset _ctx_host
    fi
    unset _podman_sock
fi
