# LolByte Service

## Description

Next generation LolByte service which aims to fully replace the [lolbyteapi](https://github.com/lolbyte-code/lolbyteapi). 

API Documentation: https://lolbyte.services/swagger-ui/#/

## Architecture

LolByte is a RESTful web service written in [Kotlin](https://kotlinlang.org/) and uses [Spring Boot](https://spring.io/projects/spring-boot). It is currently deployed using the managed Kubernetes service on [Digital Ocean](https://www.digitalocean.com/products/kubernetes/). You can access the live production version at: https://lolbyte.services/health

The Kubernetes deployment is defined under `kube/`. The primary components are a Deployment resource which wraps the `lolbyte-service` container (pulled from the Digital Ocean container registry) and a Service resource. All of that is fronted by an `nginx` load balancer where TLS is terminated. Lastly, there are two CertManager resources used to issue staging and production certificates.

## Dependencies

LolByte's primary dependency is [R4J](https://github.com/stelar7/R4J) which is used to interact with the Riot API. LolByte is mostly just an adapter layer which transforms data from the Riot API into a form that the LolByte clients can interact with.

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

## Access Metrics CronJob

The cronjob defined in `kube/access_metrics_cronjob.yaml` utilizes a Docker container which runs [GoAccess](https://goaccess.io/) to upload daily dashboards of metrics from the NGINX ingress controller access logs to Digital Ocean Spaces.

### Update CronJob Docker Image

```bash
> docker build -t access-metrics:<version> access-metrics/.
> docker tag access-metrics:<version> registry.digitalocean.com/lolbyte/access-metrics:<version>
> docker push registry.digitalocean.com/lolbyte/access-metrics:<version>
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

- [ ] CI / CD integrated w/ GitHub repo
