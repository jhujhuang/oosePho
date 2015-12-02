(function () {
    'use strict';

    angular
        .module('app')
        .controller('RegisterController', RegisterController);

    RegisterController.$inject = ['UserService', '$location', '$rootScope', 'FlashService', '$http'];
    function RegisterController(UserService, $location, $rootScope, FlashService, $http) {
        var vm = this;

        vm.register = register;

        function register() {
            vm.dataLoading = true;

            UserService.Create(vm.user)
                .then(function (response) {
                    if (response.success) {
                        console.log(UserService.Create(vm.user));
                        console.log(response);
                        console.log("Hi, im registering!");
                        FlashService.Success('Registration successful', true);
                        $location.path('/login');
                    } else {
                        FlashService.Error(response.message);
                        vm.dataLoading = false;
                    }
                });

            var userCredentials = {
                "userId": vm.user.username,
                "password": vm.user.password
            };

            /* alert(JSON.stringify(userCredentials));*/

            $http.post("/api/register", JSON.stringify(userCredentials))
            .success(function(){
                console.log("Registration success.");
            })
            .error(function(){
                console.log("Failed to store user info!");
            });
        }
    }

})();
