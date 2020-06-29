#!/usr/bin/env bash
./gradlew bootBuildImage
LATEST_TAG=$(docker images robachmann/pod-lifecycle-notifier --format "{{.Tag}}" | sort -r | head -n 1)
echo $LATEST_TAG
docker push robachmann/pod-lifecycle-notifier:$LATEST_TAG
