# LolByte Service

Next generation LolByte API. Details coming soon.

## Build

```bash
> ./gradlew build
```

## Deploy

```bash
> ./gradlew bootBuildImage --imageName=registry.digitalocean.com/lolbyte/lolbyte-service
> docker push registry.digitalocean.com/lolbyte/lolbyte-service
```