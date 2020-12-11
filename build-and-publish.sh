#!/usr/bin/env bash
./gradlew bootBuildImage
LATEST_TAG=$(docker images robachmann/pod-lifecycle-notifier --format "{{.Tag}}" | grep -v "<none>" | sort -r | head -n 1) && echo $LATEST_TAG
docker push robachmann/pod-lifecycle-notifier:$LATEST_TAG

# to test locally, you can use docker run -it --rm -p 8080:8080 -e CALLBACK_MS_TEAMS_URI=https://httpbin.org/post robachmann/pod-lifecycle-notifier:$LATEST_TAG