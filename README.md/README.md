# Apple Push Notification Connection Service
Apache NiFi controller service that supports sending push notifications to Apple's Push Notification service (APNs)

## Installation ##

 1. [Download complied NAR](https://github.com/danshev/nifi-apn-connection-service/blob/master/nifi-apn-connection-service-nar-1.0-SNAPSHOT.nar) and [that of the supporting controller service](https://github.com/danshev/nifi-apn-connection-service) into NiFi's `/lib/` directory.
 2. Change permissions of both files (`chmod 755 nifi-apn-connection-service-nar-1.0-SNAPSHOT.nar`)
 3. Restart NiFi (`/bin/nifi.sh restart`)


## Configuration ##

After NiFi restarts and you've added the [Send Push Notification processor](https://github.com/danshev/nifi-Send-Push-Notification) to your canvas ...
