# LolByte Service

Next generation LolByte API. Details coming soon.

## Build

```bash
> ./gradlew build
```

## Update Docker Image

Creates a new docker image and pushes to the Digital Ocean container registry.

```bash
> ./gradlew bootBuildImage --imageName=registry.digitalocean.com/lolbyte/lolbyte-service:<version>
> docker push registry.digitalocean.com/lolbyte/lolbyte-service:<version>
```

## Deploy

Update `kube/lolbyte.yaml` with the above version and run:

```bash
> kubectl apply -f kube/lolbyte.yaml
```

### Useful K8s commands

Restart deployment:

```bash
> kubectl rollout restart deployment lolbyte
```