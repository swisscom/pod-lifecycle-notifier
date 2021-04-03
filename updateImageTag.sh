#!/usr/bin/env bash
if [[ -z $1 ]]; then
  LATEST_TAG=$(docker images robachmann/pod-lifecycle-notifier --format "{{.Tag}}" | grep -v "<none>" | sort -r | head -n 1)
else
  LATEST_TAG=$1
fi
echo $LATEST_TAG
sed -i '' "s/pod-lifecycle-notifier:.*/pod-lifecycle-notifier:$LATEST_TAG/g" k8s/notifier-daemonset.yml
