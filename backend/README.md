# myenergi-backend

Java application to parse and store data from myenergi-api using a gcp cloud run function

Persisting data into the repository
* `GET https://europe-west1-my-project-id.cloudfunctions.net/myenergi-app-persist-zappi-data?serial=20665021`
* `GET https://europe-west1-my-project-id.cloudfunctions.net/myenergi-app-persist-zappi-data?serial=20665021&date=2024-08-28`

## setup

### Create a firebase project

1. https://console.firebase.google.com/

### deploying on firebase

1. export the adminsdk json from firebase: https://console.firebase.google.com/u/0/project/my-project-id/settings/serviceaccounts/adminsdk
2. setup env variables in `envvars.yaml` (the firebase-adminsdk.json goes into the resources folder)
   - MYENERGI_HUB_SERIAL: "serial of the hub or zappi in case of a vHUB"
   - MYENERGI_PASSWORD: "api-key generated on https://myaccount.myenergi.com/login"
   - FIREBASE_DATABASE: "https://my-project-id-default-rtdb.europe-west1.firebasedatabase.app"
   - FIREBASE_ADMIN_SDK: "/firebase-adminsdk.json"
3. Run `mvn function:deploy -f pom.xml` 