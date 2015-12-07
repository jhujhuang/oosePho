(function () {
    'use strict';

    angular
        .module('app')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['UserService', '$rootScope', '$http', '$routeParams'];
    function HomeController(UserService, $rootScope, $http, $routeParams) {

        // some jQuery to make a link serve as an input option
        $("#upload_link").on('click', function(e){
            e.preventDefault();
            $("#upload:hidden").trigger('click');
        });

        $("#open_link").on('click', function(e){
            e.preventDefault();
            $("#open").css('display', 'block');
            // get all newest versions of the user's photos.
            openImage();
        });

        $("#invite_link").on('click', function(e){
            e.preventDefault();
            $("#invite_message").css('display', 'block');
        });


        $("#upload:hidden").on('change', function(e){
            // var selectedFile = this.files[0];
            // selectedFile.convertToBase64(function(base64){
            //    alert(base64);
            // })
            uploadImage();
        });

        var vm = this;

        vm.user = null;
        vm.allUsers = [];
        vm.deleteUser = deleteUser;

        initController();

        vm.tapFilter = function() {
            console.log("I tapped the filter!");
        }

        function initController() {
            console.log("Initialize the controller");
            loadCurrentUser();
            loadAllUsers();
        }

        function loadCurrentUser() {
            console.log("load current user");
            UserService.GetByUsername($rootScope.globals.currentUser.username)
                .then(function (user) {
                    vm.user = user;
                });
        }

        function loadAllUsers() {
            console.log("load all users");
            UserService.GetAll()
                .then(function (users) {
                    vm.allUsers = users;
                });
        }

        function deleteUser(id) {
            console.log("delete all users");
            UserService.Delete(id)
            .then(function () {
                loadAllUsers();
            });
        }

         // File.prototype.convertToBase64 = function(callback){
         //           var FR= new FileReader();
         //           FR.onload = function(e) {
         //                callback(e.target.result)
         //           };
         //           FR.readAsDataURL(this);
         // }

        /* Editing related variables and logic. */

        vm.photosOfUser = null;
        vm.pid = "";
        vm.url = "";
        vm.html = "";
        vm.isInSession = false;
        vm.canvasId = "";
        vm.collaborators = [];
        vm.title = "";
        vm.canvasData = "";
        vm.versionId = "";
        var canvas = document.getElementById("canvas");
        var ctx = canvas.getContext("2d");


        if($routeParams.pId) {
            console.log(vm.user);
            console.log($rootScope.globals.currentUser);
            joinEditingSession($routeParams.pId);

            console.log("Is in session: " + vm.isInSession);
        }

        checkIsInSession();

        function checkIsInSession() {
            console.log("Checking is in session: " + vm.isInSession)
            if (vm.isInSession) {
                window.setInterval(fetchAndUpdate(), 4000);
            }   else {
                console.log("Currently no editing session is in place.");
            }
        }

        function uploadImage() {
            var imageWithJson = new FormData();
            imageWithJson.append('file', $('#upload')[0].files[0]);
            imageWithJson.append('userId', vm.user.username);
            alert($('#upload')[0].files[0].name);
            $http.post("/api/createnewphoto", imageWithJson, {
                withCredentials: true,
                headers: {'Content-Type': undefined },
                transformRequest: angular.identity
            })
            .success(function(){
                console.log("Upload success.");
            })
            .error(function(){
                // console.log("Failed to send the image to server!");
            });
        }

        function openImage(){
            /* Check for the various File API support.*/
            /*if (window.File && window.FileReader && window.FileList && window.Blob) {
              // Great success! All the File APIs are supported.
            } else {
              alert('The File APIs are not fully supported in this browser.');
            }*/
            $http.post("/api/listphotos", JSON.stringify({userId : vm.user.username}), {
            })
            .success(function(response){
                vm.photosOfUser = response['photos'];
                /* Check if the response has the correct string. */
                // console.log(JSON.stringify(vm.photosOfUser, null, 2));
            })
            .error(function(){
                // console.log("Failed to send the image to server!");
            });
        }

        function joinEditingSession(pId){
            $("#open").css('display', 'none');

            console.log($rootScope.globals.currentUser.username + " is entering Editing Session: " + pId);

            //alert(vm.user);  // TODO: Why is null? Just use user form scope now
            //$http.post("/api/edit/" + pId, JSON.stringify({userId : vm.user.username}), {})
            $http.post("/api/edit/" + pId, JSON.stringify({userId : $rootScope.globals.currentUser.username}), {
                withCredentials: true,
                headers: {'Content-Type': undefined },
                transformRequest: angular.identity
            })
            .success(function() {
                vm.pId = pId;
                vm.url = "http://localhost:8080/#/edit/" + pId;
                vm.isInSession = true;
                console.log("join success");

                console.log(vm.isInSession);
                console.log(vm.pId);

                checkIsInSession();
            })
            .error(function() {
                console.log("Photo does not exist! Not joining any editing session.");
                alert("Photo does not exist! Not joining any editing session.");
            });
        }

        function fetchAndUpdate(){
            $http.get("/api/edit/" + vm.pId + "/fetch", {
                withCredentials: true,
                headers: {'Content-Type': undefined },
                transformRequest: angular.identity
            })
            .success(function(response){
                console.log("Fetch success");

                vm.canvasId = response['canvasId'];
                vm.collaborator = response['collaborators'];
                vm.title = response['title'];
                vm.canvasData = response['canvasData'];
                vm.versionId = response['versionId'];

                var image = new Image();
                image.onload = function() {
                    ctx.drawImage(image, 0, 0);
                };
                image.src = "data:image/png;base64; " + vm.canvasData;

            })
            .error(function() {
                console.log("Fetch failed");
            });
        }

        function saveVersion(){

        }
    }

})();