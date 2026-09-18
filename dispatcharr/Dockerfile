ARG BUILD_FROM=ghcr.io/hassio-addons/debian-base:9.4.0
ARG DISPATCHARR_VERSION="0.31.0"

# hadolint ignore=DL3006
FROM ghcr.io/astral-sh/uv:0.12.17 AS uv

# Dispatcharr's frontend is a Vite application that is built up front and then
# served as static files by the backend, so it has to exist before the runtime
# image is assembled.
#
# What comes out is JavaScript and CSS, which is the same whatever it was built
# on, so this stage is pinned to the machine doing the building. Left to follow
# the target, an aarch64 image would run the whole Node toolchain under
# emulation for no benefit.
# hadolint ignore=DL3029
FROM --platform=${BUILDPLATFORM} node:24 AS frontend
ARG DISPATCHARR_VERSION

WORKDIR /usr/src/dispatcharr

# Every change made to the source tree happens here, so that what the later
# stages copy is already patched and there is one place to look for it.
#
# ingress teaches the frontend two bases it does not otherwise have, both
# written into the page by NGINX per request: SUB_PATH, the Ingress path this
# request arrived on, and PUBLIC_URL, the address players and HDHomeRun clients
# reach this app at. no-ml-matching drops the optional PyTorch EPG matching,
# and is applied here because the stage below builds from this stage's
# pyproject.toml.
COPY patches /patches

# hadolint ignore=DL3003
RUN \
    curl -J -L -o /tmp/dispatcharr.tar.gz \
        "https://github.com/Dispatcharr/Dispatcharr/archive/v${DISPATCHARR_VERSION}.tar.gz" \
    && tar zxf /tmp/dispatcharr.tar.gz --strip 1 -C /usr/src/dispatcharr \
    \
    && patch -p1 < /patches/ingress.patch \
    && patch -p1 < /patches/no-ml-matching.patch \
    \
    && cd /usr/src/dispatcharr/frontend \
    && CI=true npm clean-install --no-audit --progress=false \
    && npm run build \
    \
    && rm -f -r \
        /usr/src/dispatcharr/.git* \
        /usr/src/dispatcharr/docker \
        /usr/src/dispatcharr/docs \
        /usr/src/dispatcharr/tests \
        /usr/src/dispatcharr/frontend/node_modules \
        /usr/src/dispatcharr/frontend/public \
        /usr/src/dispatcharr/frontend/src

# The backend is installed into a virtual environment of its own, which is the
# only thing that makes it into the app image. Nothing that was needed to
# compile it comes along.
# hadolint ignore=DL3006
FROM ${BUILD_FROM} AS builder

# Set shell
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

ENV \
    UV_LINK_MODE=copy \
    UV_NO_CACHE=1 \
    VIRTUAL_ENV=/opt/dispatcharr

COPY --from=uv /uv /usr/local/bin/uv

# Only what the dependency set is read from, already patched by the stage above.
COPY --from=frontend /usr/src/dispatcharr/pyproject.toml /usr/src/dispatcharr/version.py /usr/src/dispatcharr/README.md /usr/src/dispatcharr/

# Upstream depends on torch and sentence-transformers for one thing: an
# optional "ML enhanced" pass over EPG channel matching. That is a ~2GB of
# wheels for a feature whose own loader is written to cope with the import
# failing (apps/channels/epg_matching.py), falling back to the rapidfuzz string
# matching that does the actual work in every other case. The patch drops both,
# along with the PyTorch CPU wheel index that only existed to serve them.
#
# Dropping them takes NumPy with it, since nothing else in the tree imports it.
# That is what makes libopenblas and upstream's separate no-baseline NumPy
# wheel, built so the image still runs on CPUs without AVX, unnecessary here.
#
# Debian ships Python 3.13, which is what the project asks for, so there is no
# separate interpreter to install and no deadsnakes PPA to add.
#
# libpcre2 is what uWSGI wants at build time; the rest is the toolchain for the
# handful of dependencies with no wheel for this architecture.
#
# Only bytecode and static libraries are pruned afterwards. Directories called
# "test" are deliberately left alone: django.test is a shipped module that
# rest_framework_simplejwt imports, so sweeping them up on name alone takes the
# application down with them.
# hadolint ignore=DL3003,DL3008
RUN \
    apt-get update \
    \
    && apt-get install -y --no-install-recommends \
        build-essential=12.12 \
        libpcre2-dev=10.46-1~deb13u2 \
        python3-dev=3.13.5-1 \
    \
    && uv venv --seed --python python3.13 "${VIRTUAL_ENV}" \
    && uv pip install --python "${VIRTUAL_ENV}/bin/python" \
        --requirements /usr/src/dispatcharr/pyproject.toml \
    \
    && find "${VIRTUAL_ENV}" \
        \( \
            \( -type d -a -name __pycache__ \) \
            -o \( -type f -a \( -name '*.a' -o -name '*.pyc' -o -name '*.pyo' \) \) \
        \) \
        -exec rm -rf '{}' +

# Comskip is what the DVR hands a finished recording to when commercial
# detection is switched on. It is built here rather than installed, because
# Debian has no package for it, and in a stage of its own so a failure to
# compile is obviously separate from the application build.
#
# It links against the distro's shared libav, which is why libavformat-dev is
# pulled in: it needs a real libavcodec to link to, and the ffmpeg binary in
# the final image is a separate process that is unaffected by this.
# hadolint ignore=DL3006
FROM ${BUILD_FROM} AS comskip

SHELL ["/bin/bash", "-o", "pipefail", "-c"]

# Upstream's own base image builds from master instead, because this release
# does not compile against GCC 15 or FFmpeg 8. Debian 13 has GCC 14 and FFmpeg
# 7.1, where neither of those has happened yet, so the tagged release is what
# is used here. The capital V is theirs; the other tags are lowercase.
ARG COMSKIP_VERSION="V0.83"
# hadolint ignore=DL3003,DL3008
RUN \
    apt-get update \
    \
    && apt-get install -y --no-install-recommends \
        autoconf=2.72-3.1 \
        automake=1:1.17-4 \
        build-essential=12.12 \
        libargtable2-dev=13-4 \
        libavformat-dev=7:7.1.5-0+deb13u1 \
        libswscale-dev=7:7.1.5-0+deb13u1 \
        libtool=2.5.4-4 \
        pkgconf=1.8.1-4 \
    \
    && mkdir -p /tmp/comskip \
    && curl -fsSL \
        "https://github.com/erikkaashoek/Comskip/archive/refs/tags/${COMSKIP_VERSION}.tar.gz" \
        | tar -xz --strip-components=1 -C /tmp/comskip \
    \
    && cd /tmp/comskip \
    && ./autogen.sh \
    && ./configure \
    && make -j"$(nproc)" \
    && strip comskip \
    && install -m 0755 comskip /usr/local/bin/comskip

# hadolint ignore=DL3006
FROM ${BUILD_FROM}

# Set shell
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

ENV \
    PATH="/opt/dispatcharr/bin:${PATH}" \
    PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    VIRTUAL_ENV=/opt/dispatcharr

# ffmpeg is what every stream is remuxed or transcoded through, and the one
# piece here that touches the graphics chip: Debian's build carries VA-API, so
# an Intel or AMD GPU passed in as /dev/dri is used without anything further.
#
# PostgreSQL is not a choice. Upstream's settings do have a SQLite branch, but
# three of the migrations run unguarded PostgreSQL SQL (the ~ regex operator,
# ::jsonb casts, ->> extraction, an aliased UPDATE), so a SQLite database never
# finishes migrating in the first place.
#
# It comes from the PostgreSQL project's own repository rather than from
# Debian, which is the one decision here worth explaining. PostgreSQL refuses
# to start on a data directory written by a different major version, so if the
# major came from the base image, bumping the base image across a Debian
# release would silently break every existing installation. Taking it from
# upstream pins the major to this file, where changing it is a deliberate act
# that can ship a pg_upgrade alongside it. apt reads the ASCII-armored key
# directly, so this needs no gnupg.
#
# Changing the major version below is not a version bump. Read
# POSTGRES-UPGRADE.md first: it takes three releases and an extra init step,
# and getting it wrong strands every existing installation.
#
# Redis is separate and equally required: it is the Celery broker, the Django
# Channels layer behind the WebSocket, and the cache.
#
# VLC is one of the commands a stream profile may be pointed at, alongside
# ffmpeg and streamlink, so it is installed for the profiles that ask for it.
# Nothing imports it; the Python binding upstream depends on is never used.
#
# The virtual environment copied in below is only a set of links onto the
# system interpreter, so python3.13 has to be here as well as in the builder,
# and libpython is what the uWSGI binary in it is linked against.
#
# libpcre2 backs uWSGI and libargtable2 backs comskip. NGINX faces Ingress and
# the network; nothing else here listens on anything but loopback.
# hadolint ignore=DL3008
RUN \
    mkdir -p /etc/apt/keyrings \
    && curl -fsSL -o /etc/apt/keyrings/pgdg.asc \
        "https://www.postgresql.org/media/keys/ACCC4CF8.asc" \
    && echo \
        "deb [signed-by=/etc/apt/keyrings/pgdg.asc]" \
        "https://apt.postgresql.org/pub/repos/apt trixie-pgdg main" \
        > /etc/apt/sources.list.d/pgdg.list \
    \
    && apt-get update \
    \
    && apt-get install -y --no-install-recommends \
        ffmpeg=7:7.1.5-0+deb13u1 \
        libargtable2-0=13-4 \
        libpcre2-8-0=10.46-1~deb13u2 \
        libpython3.13=3.13.5-2+deb13u5 \
        nginx=1.26.3-3+deb13u8 \
        postgresql-17=17.11-1.pgdg13+2 \
        procps=2:4.0.4-9 \
        python3.13=3.13.5-2+deb13u5 \
        redis-server=5:8.0.2-3+deb13u2 \
        vlc-bin=3.0.23-0+deb13u1 \
        vlc-plugin-base=3.0.23-0+deb13u1 \
    \
    && rm -f -r /etc/nginx \
    \
    && apt-get clean \
    && rm -f -r \
        /tmp/* \
        /var/{cache,log}/* \
        /var/lib/apt/lists/*

# The application tree, already patched, with the built frontend still in
# frontend/dist where both the index.html template and collectstatic look.
COPY --from=frontend /usr/src/dispatcharr /app
COPY --from=builder /opt/dispatcharr /opt/dispatcharr
COPY --from=comskip /usr/local/bin/comskip /usr/local/bin/comskip

# Gathering the static files is the same work whatever the configuration says,
# so it is done once here rather than on every start. It needs a settings
# module that imports, which is all the placeholder key is for; the real one is
# generated on first start and kept in /data.
# hadolint ignore=DL3003
RUN \
    cd /app \
    && DJANGO_SECRET_KEY="collectstatic" \
        python manage.py collectstatic --noinput --clear \
    && find /app -name '__pycache__' -type d -exec rm -f -r '{}' +

# Copy root filesystem
COPY rootfs /

# Answering at all is the question here, so a 401 from an endpoint that wants
# a login still means the application is up. Only a refused connection, which
# is what a dead uWSGI looks like, fails this.
HEALTHCHECK \
    CMD curl --silent --output /dev/null --max-time 10 \
        http://127.0.0.1:5656/api/core/version/ || exit 1

# Build arguments
ARG BUILD_ARCH
ARG BUILD_DATE
ARG BUILD_DESCRIPTION
ARG BUILD_NAME
ARG BUILD_REF
ARG BUILD_REPOSITORY
ARG BUILD_VERSION

# Labels
LABEL \
    io.hass.name="${BUILD_NAME}" \
    io.hass.description="${BUILD_DESCRIPTION}" \
    io.hass.arch="${BUILD_ARCH}" \
    io.hass.type="addon" \
    io.hass.version=${BUILD_VERSION} \
    maintainer="Franck Nijhof <opensource@frenck.dev>" \
    org.opencontainers.image.title="${BUILD_NAME}" \
    org.opencontainers.image.description="${BUILD_DESCRIPTION}" \
    org.opencontainers.image.vendor="Home Assistant Community Apps" \
    org.opencontainers.image.authors="Franck Nijhof <opensource@frenck.dev>" \
    org.opencontainers.image.licenses="MIT" \
    org.opencontainers.image.url="https://frenck.dev/home-assistant-apps" \
    org.opencontainers.image.source="https://github.com/${BUILD_REPOSITORY}" \
    org.opencontainers.image.documentation="https://github.com/${BUILD_REPOSITORY}/blob/main/README.md" \
    org.opencontainers.image.created=${BUILD_DATE} \
    org.opencontainers.image.revision=${BUILD_REF} \
    org.opencontainers.image.version=${BUILD_VERSION}
