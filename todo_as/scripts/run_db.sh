#!/bin/bash

pushd $(dirname $0)
set -e

docker compose --env-file ../src/main/resources/local/local.env up -d

popd
