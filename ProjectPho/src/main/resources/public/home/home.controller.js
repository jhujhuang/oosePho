(function () {
    'use strict';

    angular
        .module('app')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['UserService', '$rootScope', '$http'];
    function HomeController(UserService, $rootScope, $http) {

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

        if (vm.isInSession) {
            window.setInterval(fetchAndUpdate(), 4000);
        }   else {
            console.log("Currently no editing session is in place.");
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

        function initiateEditingSession(idx){
            $("#open").css('display', 'none');
            vm.pId = Object.keys(vm.photosOfUser)[idx];
            vm.url = "http://localhost:8080/#/edit/" + pId;
            isInSession = true;
            console.log("fck!!!")
        }

        function fetchAndUpdate(){
            $http.post("http://localhost:8080/#/edit/" + pId + "/fetch", {
            })
            .success(function(response){

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
            .error();
        }

        function saveVersion(){

        }
    }

})();