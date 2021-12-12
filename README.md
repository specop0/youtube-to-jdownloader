# YouTube 2 JDownloader

Searches uploads of subscribed YouTube channels for new ones and triggers JDownloader to download them.

## Configuration for YouTube API

YouTube development account - see [How to get YouTube API key (rapidapi.com)](https://rapidapi.com/blog/how-to-get-youtube-api-key/)

[Project](https://console.developers.google.com/project) for YouTube Data API v3 with an OAuth 2.0 Client.

Save downloadable client_secret.json from OAuth 2.0 Client to: `src/main/resources/youtube/client_secret.json`.

```json
{
    "installed": {
        "client_id": "....apps.googleusercontent.com",
        "project_id": "youtubetojdownloader",
        "auth_uri": "https://accounts.google.com/o/oauth2/auth",
        "token_uri": "https://oauth2.googleapis.com/token",
        "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
        "client_secret": "...",
        "redirect_uris": [
            "urn:ietf:wg:oauth:2.0:oob",
            "http://localhost"
        ]
    }
}
```

## Configuration in JSON file needed

cache entry is a [specop0/LocalRestServer](https://github.com/specop0/LocalRestServer)

folderwatch is the [folderwatch extension](https://github.com/cooolinho/jdownloader-folderwatch) of [JDownloader](https://jdownloader.org/)

```json
{
    "cache" : {
        "port" : 6491,
        "authorization" : "secret of LocalRestServer"
    },
    "jdownloader" : {
        "folderwatch" : "/path/to/JDownloader/folderwatch"
    }
}
```