﻿(function () {
    'use strict';

    angular
        .module('app')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['UserService', '$rootScope'];
    function HomeController(UserService, $rootScope) {

        // some jQuery to make a link serve as an input option
        $("#upload_link").on('click', function(e){
            e.preventDefault();
            $("#upload:hidden").trigger('click');
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

        function uploadImage(images) {
            console.log("upload images");
            var imageWithJson = new FormData();
            imageWithJson.append("file", files[0]);
            imageWithJson.append("userId", "haha");
        }
    }

})();