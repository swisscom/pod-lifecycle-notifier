#!/usr/bin/env bash
LATEST_TAG=$(docker images robachmann/pod-lifecycle-notifier --format "{{.Tag}}" | grep -v "<none>" | sort -r | head -n 1) && echo $LATEST_TAG
docker run -it --rm -p 8080:8080 -e CALLBACK_MS_TEAMS_URI=https://httpbin.org/post robachmann/pod-lifecycle-notifier:$LATEST_TAG
