# PhotoSì - Android Developer
## Use case description
Create an Android native app with the aim of upload the user local images to the Cloud in order to share them via a link.

There isn’t a specific requirement about which Cloud service to use.
We suggest *Catbox (see the point 2. of  “Documentation” for further details)* since both the Web and API services are free, but feel free to adopt the one that you prefer.

There is no UI / UX specifications for this app, but we recommend to follow the Apple Human Guidelines. An accurate UI / UX will be considered as a plus in the final evaluation

## List of steps
1. After the app opening, the user is going to select his country from a list of available countries *(see the point 1 of  “Documentation” for further details)*.

2. In the second step, the user is going to select images to upload from the local gallery.
Once that the images have been selected, the user can proceed with the upload phase.

3. Finally, when the upload has done, the user will be able to copy a URL from a list of URLs.
That list is the result of each single upload, so it's the place where the different images have been uploaded.

*Note:* *The uploading process needs to be optimised since most of the users have a Mobile connection, which is slower than a Wi-Fi or a cable connection.*
*So, the idea is to evaluate and apply as much as possible improvements to that part, such as: image compression, parallel uploads, etc..*  

### Nice to have
- During the uploading phase, show a progress view which inform the user about the estimated remaining time or/and the remaining files to be uploaded.
- Allow the user to modify the list of selected images before to proceed with the uploading phase
- Allow the user to take a new photo from the Camera and select it for the upload
- Allow the user to apply a filter or a transformation (zoom, rotation, resize, etc..) to an image before to upload it
- Make the uploading phase suspendible
- Allow the user to select images from Google Photos or Facebook

## Technical Requirements
- Language: Kotlin
- IDE: Android Studio

### Nice to have 
- At least one example of Unit Test
- The setup of a Continuous Integration flow for the project  

## Documentation

### 1. API - List of available Countries

- URL: https://api.photoforse.online/geographics/countries/
- Method: GET
- HTTP Headers: *x-api-key* :  `AIzaSyCccmdkjGe_9Yt-INL2rCJTNgoS4CXsRDc`
- Body Response:
```
[
    {
        "iso": 248,
        "isoAlpha2": "AX",
        "isoAlpha3": "ALA",
        "name": "Aland Islands",
        "phonePrefix": "+358-18"
    },
    {
        "iso": 8,
        "isoAlpha2": "AL",
        "isoAlpha3": "ALB",
        "name": "Albania",
        "phonePrefix": "+355"
    }
]
```

### 2. Catbox Upload Service

- URL: [Catbox](https://catbox.moe)
- API documentation - *File uploads* section: [Catbox Tools](https://catbox.moe/tools.php)
