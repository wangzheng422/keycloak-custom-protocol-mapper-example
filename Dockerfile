ARG KEYCLOAK_IMAGE="quay.io/keycloak/keycloak:24.0.5"

# Build protocoll mapper so that it always has the current version
FROM docker.io/maven:3.8 as jdk-builder

WORKDIR /workspace
COPY . ./

RUN mvn clean package

# Build keycloak
FROM ${KEYCLOAK_IMAGE} as keycloak-builder

COPY --from=jdk-builder /workspace/protocol-mapper/target/keycloak-custom-protocol-mapper-example.jar /opt/keycloak/providers/

RUN /opt/keycloak/bin/kc.sh build

FROM registry.access.redhat.com/ubi9 AS ubi-micro-build
RUN mkdir -p /mnt/rootfs
RUN dnf install --installroot /mnt/rootfs curl --releasever 9 --setopt install_weak_deps=false --nodocs -y && \
    dnf --installroot /mnt/rootfs clean all && \
    rpm --root /mnt/rootfs -e --nodeps setup

# Create keycloak image
FROM ${KEYCLOAK_IMAGE}

COPY --from=ubi-micro-build /mnt/rootfs /

COPY --from=keycloak-builder /opt/keycloak/ /opt/keycloak/

WORKDIR /opt/keycloak

COPY ./docker-entrypoint.sh /opt/keycloak
COPY ./data-setup/src/main/bash/populate-data.sh /opt/keycloak
COPY --from=jdk-builder /workspace/data-setup/target/data-setup.jar /opt/keycloak
COPY ./data-setup/src/main/bash/populate-data.sh /opt/keycloak

USER root

# hostname is needed for the testdata population script to work, because we need it to figure out the rest api url
# RUN microdnf update -y && microdnf install hostname -y

RUN mkdir -p /app

# we use a custom entry point for testdata population
COPY ./docker-entrypoint.sh /app

RUN chmod +x /app/docker-entrypoint.sh \
   && chmod +x /opt/keycloak/populate-data.sh

USER 1000

ENTRYPOINT ["/app/docker-entrypoint.sh"]

CMD ["--verbose", "start-dev", "--http-enabled=true", "--http-relative-path=/auth", "--http-port=8080", "--hostname-strict=false", "--hostname-strict-https=false"]
