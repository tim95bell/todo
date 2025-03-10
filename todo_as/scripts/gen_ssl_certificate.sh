#!/bin/bash

pushd $(dirname $0)/../src/main/resources
mkdir -p local
pushd local

set -e

. ./local.env

openssl req -newkey rsa:2048 -x509 -keyout key.pem -out cert.pem -days 365 --passout pass:$TIM95BELL_TODO_AS_SSL_KEY_PASSWORD -subj /C=AU/O=tim95bell/

openssl pkcs12 -export -in cert.pem -inkey key.pem -out certificate.p12 -name "certificate" -passin pass:$TIM95BELL_TODO_AS_SSL_KEY_PASSWORD -passout pass:$TIM95BELL_TODO_AS_SSL_CERTIFICATE_PASSWORD

popd
popd
