FROM qa.stratio.com/stratio/ubuntu-base:16.04

MAINTAINER stratio

ARG VERSION
ARG KMS_UTILS_VERSION

RUN apt-get update && apt-get install -y screen

RUN wget http://sodio.stratio.com/repository/paas/kms_utils/${KMS_UTILS_VERSION}/kms_utils-${KMS_UTILS_VERSION}.sh
RUN mv kms_utils-${KMS_UTILS_VERSION}.sh kms_utils.sh

COPY target/khermes-${VERSION}-allinone.jar /khermes.jar
COPY docker/docker-entrypoint-dcos.sh /
COPY docker/docker-entrypoint-local.sh /
COPY docker/docker-entrypoint.sh /
COPY src/main/resources/application.conf /
RUN touch /khermes.log

EXPOSE 8080

ENTRYPOINT ["/docker-entrypoint.sh"]
