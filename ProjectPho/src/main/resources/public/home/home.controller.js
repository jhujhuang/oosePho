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
                console.log("Failed to send the image to server!");
            });
        }

        function openImage(){
            /* Check for the various File API support.*/
            if (window.File && window.FileReader && window.FileList && window.Blob) {
              // Great success! All the File APIs are supported.
            } else {
              alert('The File APIs are not fully supported in this browser.');
            }
        }
    }

})();