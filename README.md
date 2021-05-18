# Smartlook Demo for Android

It serves as a tool for developers and teams that would like to deploy Smartlook in their application. With this app, they can try out all the key features without having to integrate the SDK into their own application. Everything is already ready and available for immediate use.

## How to use

1. Clone this repository and install application on your device
2. Sign up to Smartlook in order to create a testing Demo project and acquire the project’s API key that will be necessary in order to start session recordings in your demo project.
3. During the account’s creation process please select Mobile app project.
4. Once the onboarding is completed, you will be presented with your project’s API key that needs to be served to the sample app that you installed.

<img src="https://github.com/smartlook/android-example-app/raw/master/readme-media/mobile_demo_apikey.png" width="50%" height="50%">

5. When you run the sample application for the first time, **the initial screen asks you to input the API key**. This is the key we acquired in the previous step.


<img src="https://github.com/smartlook/android-example-app/raw/master/readme-media/Screenshot_20210510_170915_org.schabi.newpipe.debug.featuresales.jpg" width="50%" height="50%">


6. In the next step you may input the user ID that will be shown in Smartlook dashboard to identify the session recording you will create.


<img src="https://github.com/smartlook/android-example-app/raw/master/readme-media/Screenshot_20210510_171621_org.schabi.newpipe.debug.featuresales.jpg" width="50%" height="50%">


7. Now that you entered the info, the session recording is initiated. You may browse freely and the session will be then available in Smartlook account that you created in step 1.
8. Outside of auto-tracked events, our sample application has some Custom events implemented as well. So once you navigate to **Events tab** in Smartlook Dashboard, you may use them to interact with Events manager. 
9. **The sample application also has a specific scenario created** in it that you may look at some drop-offs in the funnel (you need to compose a funnel within your testing account).

   - Once you initiate a replay of a video inside the sample application, you may **download the video** (initial step of the funnel).
   - The app will notify you that you need to subscribe in order to be able to download videos.
   - There is a user login screen here:


<img src="https://github.com/smartlook/android-example-app/raw/master/readme-media/Screenshot_20210510_171808_org.schabi.newpipe.debug.featuresales.jpg" width="50%" height="50%">

10. We have 3 sets of credentials with pre-defined output:
    - uname: user1 ; pw: user1 - **the plan subscription is successful** and you will be able to download the video
      - The first credential set will allow you to subscribe and then download the video into your phone. This is considered a successful conversion in our demo funnel. Other credential sets will disallow you to complete the funnel.
      - **Please note** - in case these credentials are entered, they are stored and you would need to remove app’s temporary data and files (from OS app settings area) that will allow you to re-enter other credential set
    - uname: user2 ; pw: user2 - **the plan subscription is unsuccessful**; account not activated
    - uname: user3 ; pw: user3 - **the plan subscription is unsuccessful**; account activated but insufficient funds error

11. After your browsing is done, you may leave the app and it will be terminated after 5 minutes of inactivity. Alternatively, you may manually kill the app and terminate the session recording at once.
12. After the data is uploaded, your session replay will be stored & visible in the Smartlook account you created in step 1.

## Support

Please feel free to share some feedback with us either with your account representative or directly in an email to support@smartlook.com

## License

Smartlook Demo app is based on [NewPipe](https://github.com/TeamNewPipe/NewPipe), A libre lightweight streaming frontend for Android.
