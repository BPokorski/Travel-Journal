# Travel Journal
Application that allow users to create own account, make photos,  add description to any photo, 
display single photo, whole gallery or map with countries and places where photo was taken (if location included).

## Table of Contents
* [General Information](#general-information)
* [Technologies and Libraries](#technologies-and-libraries)
* [Features](#features)
* [Usage](#usage)
* [Contact](#contact)

## General Information
- Client-Server architecture
- Using geolocation of pictures
- Images are stored on Google Drive
- Created from passion for travel
- Application was created using REST

## Technologies and Libraries
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%230095D5.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)
![Invision](https://img.shields.io/badge/invision-FF3366?style=for-the-badge&logo=invision&logoColor=white)

### Main Libraries
* [OpenCage](https://opencagedata.com/api) - API for geocoding
* [Metadata Extractor](https://drewnoakes.com/code/exif/) - library for reading metadata from media files
* [Fotoapparat](https://github.com/RedApparat/Fotoapparat) - API for android camera usage
* [Google Maps SDK](https://developers.google.com/maps/documentation/android-sdk/overview) - Map component for client part
* [Retrofit](https://square.github.io/retrofit) - type-safe HTTP Client for Android and Java
* [Glide](https://github.com/bumptech/glide) - media managament and image loading framework for Android
* [JUnit](https://junit.org/junit5/) - Library for unit tests

## Features
* Signing up and signing in users
* Make photo(with location as well)
* Display gallery of all user pictures
* Display map with countries and marker in country, where photo was taken
* Add own description to image
* Delete pictures or user

### To Do
* friends list
* share journals with others
* Logging with Google or Facebook
* Adding images from local gallery
* Possibility to change language of application
* New camera's usage features e.g.: flash mode etc.

## Usage
For security issues file containing cloud storage access, DB access and Geocoding API Key was removed.
API Key for Google Maps SDK also needs to be self-generated.

Maps display only those countries in which photo was taken.

Client(Mobile app in this case) send request to the server, which is processed and response with proper data is returned.

Server wasn't published, so connection is only available localy.

### API Endpoints
Here is complete list of avaiable endpoints

#### GET Methods
* `localhost:8080/{login}/map`  
Endpoint Parameters: login of user.  
Returns: JSON containing names of countries.

* `localhost:8080/{login}/map/{country}`  
Endpoint Parameters: login of user, name of country.  
Returns: JSON containing photo data in given country.  

* `localhost:8080/{login}/map/ocean`  
Endpoint Parameters: login of user.  
Returns: JSON containing photo data, that have location, but weren't assigned to any country.

* `localhost:8080/{login}/photo/{photoId}`  
Endpoint Parameters: login of user, id of photo.  
Returns: JPG image.

* `localhost:8080/{login}/photo/{photoId}/photodata`  
Endpoint Parameters: login of user, id of photo.  
Returns: JSON containing photo data of single photo.

* `localhost:8080/{login}/photo/photodata`  
Endpoint Parameters: login of user.  
Request Parameters: page number, size of page.  
Returns: Paginated JSON containing all photos data.

#### POST Methods
* `localhost:8080/user/signin`  
Request Parameters: login, password.  
Returns: JSON containing user's authentication.

* `localhost:8080/user/signup`  
Request Parameters: login, e-mail, role, password.  
Returns: Information with successfully registered user.

* `localhost:8080/{login}/photo`  
Endpoint Parameters: login of user.  
Request Parameters: JPG picture.  
Returns: JSON containing PhotoData of newly added photo.

#### PUT Methods
* `localhost:8080/{login}/photo/{photoId}/description`  
Endpoint Parameters: login of user, id of photo.  
Request Parameters: new description.  
Returns: JSON containing updated PhotoData.

#### DELETE Methods
* `localhost:8080/{login}/photo/{photoId}`  
Endpoint Parameters: login of user, id of photo.  
Returns: Information with successfully deleting photo with given id.

* `localhost:8080/{login}`  
Endpoint Parameters: login of user  
Returns: Information with successfully deleting user with given login.

### Example screens
<img src="./img/gallery.png" height=25% width=25%> <img src="./img/login.png" height=25% width=25%>
<img src="./img/photo.png" height=25% width=25%> <img src="./img/place.png" height=25% width=25%>

## Contact
If You want to contact me, write email to [bartosz.pokorski67@gmail.com](mailto:bartosz.pokorski67@gmail.com)
