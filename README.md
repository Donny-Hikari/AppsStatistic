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
<b>Splash Screen & Login Activity</b><br/>
<img src="/docs/v2.splash_screen.png" alt="Splash Screen" height="512" />
<img src="/docs/v2.login_screen.png" alt="Login Activity" height="512" />
</div>
<br/>
<div align="center">
<b>Main Activity & Survey Introduction</b><br/>
<img src="/docs/v2.main_screen.png" alt="Main Activity" height="512" />
<img src="/docs/v2.survey_screen1.png" alt="Survey Introduction" height="512" />
</div>
<div align="center">
<b>Notification & About Activity</b><br/>
<img src="/docs/v2.notification.jpg" alt="Notification" height="512" />
<img src="/docs/v2.about_screen.png" alt="About Activity" height="512" />
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
<div align="center">
<img src="/docs/v2.survey_screen1.png" alt="Survey Introduction" height="512" />
<img src="/docs/v2.survey_screen2.png" alt="Survey Questionnaire" height="512" />
</div>

##Author
* Author: Donny Hikari
* Mail: [donny.hikari@gmail.com](mailto:donny.hikari@gmail.com "Mail")
* Blogs: <a href="https://donny-hikari.github.io/Blogs/" target="_blank">Donny's Blogs</a>