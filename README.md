# LolByte Service

## Description

Next generation LolByte service which aims to fully replace the [lolbyteapi](https://github.com/ACSchott526/lolbyteapi).

## Architecture

LolByte is a RESTful web service written in [Kotlin](https://kotlinlang.org/) and uses [Spring Boot](https://spring.io/projects/spring-boot). It is currently deployed using the managed Kubernetes service on [Digital Ocean](https://www.digitalocean.com/products/kubernetes/). You can access the live production version at: https://lolbyte.services/health

The Kubernetes deployment is defined under `kube/`. The primary components are a Deployment resource which wraps the `lolbyte-service` image (pulled from the Digital Ocean container registry) and a Service resource. All of that is fronted by an `nginx` load balancer where TLS is terminated. Lastly, there are two CertManager resources used to issue staging and production certificates.

## Dependencies

LolByte's primary dependency is [Orianna](https://github.com/meraki-analytics/orianna) which is used to interact with the Riot API. LolByte is mostly just an adapter layer which transforms data from the Riot API into a form that the LolByte clients can interact with.

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

### Useful Kubernetes Commands

Restart deployment:

```bash
> kubectl rollout restart deployment lolbyte
```

### Resources

- https://learnk8s.io/spring-boot-kubernetes-guide
- https://www.digitalocean.com/community/tutorials/how-to-set-up-an-nginx-ingress-with-cert-manager-on-digitalocean-kubernetes

#### TODO

- [ ] Document APIs in Swagger
- [ ] Metrics / Monitoring
- [ ] CI / CD integrated w/ GitHub repo