# About
This application serves the purpose of a simple probe that notifies defined channels about its own startup and shutdown.
It can be used to explore a cloud platform's behavior over a longer period.
By default, the provided [daemonset.yml](k8s/daemonset.yml) will create a `DaemonSet` which in turn runs a `Pod` on each `Node` of a Kubernetes cluster.

# Deployment
To deploy the built app, adjust the values in [daemonset.yml](k8s/daemonset.yml) and [secret-callback.yml](k8s/secret-callback.yml) to your needs.
Then, deployment is straight-forward with `kubectl apply`. 

# Configuration
The two files mentioned above ([daemonset.yml](k8s/daemonset.yml) and [secret-callback.yml](k8s/secret-callback.yml)) are also the two locations where certain adjustments can be made:
Initially, the application supported only the Microsoft Teams notification channel.
If you'd like to use that, enter the corresponding teams URI which resembles this pattern: `https://outlook.office.com/webhook/<uuid>@<uuid>/IncomingWebhook/<id>/<uuid>`.
You can provide a custom cluster-name by overwriting the environment variable `KUBERNETES_CLUSTER` in the daemonset to disambiguate notifications from different clusters.
By default, the provided [deploy-k8s.sh](deploy-k8s.sh) will set it to your local machine's cluster-context.

# Extension
To use your own notification channel, have a look at [ConsoleLogger.java](src/main/java/com/swisscom/clouds/callbacks/console/ConsoleLogger.java) and add your own class implementing the interface [Callback.java](src/main/java/com/swisscom/clouds/callbacks/Callback.java).