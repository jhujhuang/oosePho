
Pho
---

Pho is a collaborative photo editing platform. With the rise of smartphones and social media websites, photos have become an essential part of our everyday life. We use photos to record important moments and share with friends. However, we find most cutting-edge photo editing tools are lacking collaboration features. We hope this project can fill in this gap and make photo editing even easier.

#### Collaborators
* Tai-Ting Hsieh (@thsieh10)
* Jiawei Huang (@jhujhuang)
* Bo Liu (@VermicelliInCasserole)
* Jizhou Xu (@Meteorite-J)
* Mozhi Zhang (@zhangmozhi)

Pho was our group project for the [OOSE course](http://pl.cs.jhu.edu/oose) in Fall 2015 at Johns Hopkins University.

<!-- TODO: Introduce software features -->

Travis Status
-------------
![](https://magnum.travis-ci.com/jhu-oose/2015-group-8.svg?token=yn8z5Kcxy4pNxCCzuAEr&branch=master)

Build
-----

The code should be directly importable as an existing Maven project into Eclipse or IntelliJ, and should directly build and run from within the IDE.  You need to set up a run configuration to run <tt>Bootstrap.main()</tt>.

If you instead want to compile and run from the command line, assuming you have the Maven command line installed:

```console
mvn package
java -jar target/Pho1-1.0-SNAPSHOT.jar
```

Having the program running, you may simply point your browser to http://localhost:8080 to use the application.

#### Current Code State

Currently, Pho can run locally and users can experience the whole set of functionalities for collaborative photo editing. We have not fully integrated the database yet, so every time the server is restarted we would still need to start over from registering and uploading photos.

We support jpg photos of all sizes, but it would work better to have a photo not too large since currently we do not have a zoom tool, and would have to scroll around. We sometimes have color problems with png pictures currently.

> Note: please don't open the .html files directly because web pages rendered with AngularJS need to be hosted. Otherwise you won't see the correct views.

#### Possible Future Plans

* Finish the persistent layer
* Add comment as a feature
* Introduce more filters and implement Pencil and other tools
* Deploying to real server and make public!
