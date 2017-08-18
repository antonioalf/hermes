#!/bin/bash -xe

if [[ ! -z "$DOWNLOAD_KAFKA_SECRETS" ]]; then
	# Obtain secrets for kafka-framework
	if [[ -z "$KAFKA_BROKER_INSTANCE_NAME" ]]; then
		echo "ERROR: Kafka broker instance name not defined. Secrets cannot be downloaded!"
		exit 1
	fi

	if [[ -z "$KAFKA_BROKER_PRINCIPAL" ]]; then
		echo "ERROR: Kafka broker principal not defined. Secrets cannot be downloaded!"
		exit 1
	fi

	source ./kms_utils.sh

	getCert userland "${KAFKA_BROKER_INSTANCE_NAME}" ${KAFKA_BROKER_PRINCIPAL} "JKS" /vault_secrets
	getCAbundle . "JKS" "/vault_secrets/kafka-truststore.jks" "ca-trust" "default"
	mv vault_secrets/${KAFKA_BROKER_PRINCIPAL}.jks vault_secrets/kafka-keystore.jks
fi

if [[ ! -z "$DCOS" ]]; then 
	./docker-entrypoint-dcos.sh
else
	./docker-entrypoint-local.sh
fi

tail -F /khermes.log
