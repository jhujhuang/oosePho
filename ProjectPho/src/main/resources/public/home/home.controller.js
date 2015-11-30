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
            var selectedFile = $('#upload')[0].files[0];
            uploadImage(selectedFile);
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

        function register() {
        }

         File.prototype.convertToBase64 = function(callback){
                    var FR= new FileReader();
                    FR.onload = function(e) {
                         callback(e.target.result)
                    };
                    FR.readAsDataURL(this);
         }

        function uploadImage(selectedFile) {
            console.log("upload images");
            var imageWithJson = new FormData();
            imageWithJson.append("file", selectedFile);
            imageWithJson.append("userId", vm.user);
            $http.post("/api/createnewphoto", imageWithJson, {
                withCredentials: true,
                headers: {'Content-Type': 'multipart/form-data' },
                transformRequest: angular.identity
            })
            .success(function(){
                alert("Upload success!");
            })
            .error(function(){
            });
        }
    }

})();