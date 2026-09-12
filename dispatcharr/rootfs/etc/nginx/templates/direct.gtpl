server {
    {{ if not .ssl }}
    listen {{ .port }} default_server;
    {{ else }}
    listen {{ .port }} default_server ssl;
    http2 on;
    {{ end }}

    include /etc/nginx/includes/server_params.conf;

    {{ if .ssl }}
    include /etc/nginx/includes/ssl_params.conf;

    ssl_certificate /ssl/{{ .certfile }};
    ssl_certificate_key /ssl/{{ .keyfile }};
    {{ end }}

    # Served from the root here, so none of what makes Ingress work applies.
    # Every rewrite in the shared configuration collapses into a no-op with
    # these empty, and Dispatcharr's own login is what guards it.
    #
    # An empty public URL is what the frontend wants too: it falls back to the
    # address the browser is already using, which on this server is the right
    # answer by construction.
    set $script_name "";
    set $sub_path    "";
    set $public_url  "";

    include /etc/nginx/includes/dispatcharr.conf;
}
