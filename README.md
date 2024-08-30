# myenergi-app

## Backend

A java backend with cloud function to persist myenergi data.
A cron-job will scrape and persist data into a database.

 * [readme backend](backend/README.md)

Application hosted as Google Cloud Function on Firebase.

## Frontend 

An angular frontend that connects to firebase RTDB.
Easily and quickly access historical data. E.g. yearly overview of how much KWh is used to charge your EV.


 * [demo on firebase](https://myenergi-app.web.app)
 * [readme frontend](frontend/README.md)

## Deploy

Deploy using firebase cli in the root folder

Build and deploy the frontend application
 `firebase deploy`


