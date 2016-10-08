#Apps Statistic Application

An application to record your usage of the screen and your apps on your phone.

![Poster](/docs/structure_concept.png "Apps Statistic")

##Main Function
1. Track the usage of screen
2. Track the usage of apps
3. Sync data with remote server
4. Receive notification from remote server

##Main Moudles

###Screenshot
<div align="center">
<img src="/docs/v2.splash_screen.png" alt="Splash Screen" height="512" style="margin-right:30px;"/>
<img src="/docs/v2.login_screen.png" alt="Login Activity" height="512" style="margin-left:30px;"/>
</div>
<br/>
<div align="center">
<img src="/docs/v2.main_screen.png" alt="Main Activity" height="512" style="margin-right:30px;"/>
<img src="/docs/v2.survey_screen1.png" alt="Survey Introduction" height="512" style="margin-left:30px;"/>
</div>

###Design
0. Splash Screen
1. Login Activity
	User enter the ID of the medical experiment.
2. Main Activity
	1. Display the usage of the screen today.
	2. Display the detail of the usage of apps today.
3. Survey
	Provide questionnaire to figure out the mental status of user
4. Sync Service
	Sync user's data with remote server
5. Notification
	Receive survey websites from remote server and Push notification to user

###Survey Model

####Design
<img src="/docs/survey_design.png" alt="Survey Design" />

####Screenshot
<img src="/docs/v2.survey_screen1.png" alt="Survey Introduction" height="512" />
<img src="/docs/v2.survey_screen2.png" alt="Survey Questionnaire" height="512" />