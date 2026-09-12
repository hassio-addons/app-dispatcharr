server {
    listen {{ .interface }}:{{ .port }} default_server;

    include /etc/nginx/includes/server_params.conf;

    allow   172.30.32.2;
    deny    all;

    # The path Home Assistant is serving this app from. It is settled per
    # request and is different every time the token is rotated, so it can only
    # ever be read off the request itself.
    #
    # It goes two places. As SCRIPT_NAME it tells Django where it is mounted,
    # which fixes the URLs that come back inside JSON payloads where no rewrite
    # could reach them. As SUB_PATH it is written into the page, which is what
    # the frontend resolves its own requests against.
    set $script_name $http_x_ingress_path;
    set $sub_path    $http_x_ingress_path;

    # The address players, Plex, Emby, Jellyfin and anything else outside Home
    # Assistant reach this app at. Deliberately not the Ingress path: that is
    # bound to a browser session and authenticated by Home Assistant, so it is
    # of no use to a device. This is what the M3U, EPG and HDHomeRun links in
    # the interface are built from, whichever way the interface is being
    # looked at.
    set $public_url "{{ .public_url }}";

    # Django's XFrameOptions middleware is enabled and X_FRAME_OPTIONS is left
    # at its default, so every response says it may not be framed at all.
    # Ingress is an iframe, so the page would never render. Dropping the header
    # applies only here; the direct server keeps it.
    uwsgi_hide_header X-Frame-Options;

    include /etc/nginx/includes/dispatcharr.conf;
}
