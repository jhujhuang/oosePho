describe("Login", function() {
    var mockAuthenticationService, mockFlashService, loginController, deferred;
    beforeEach(function() {
        mockAuthenticationService = jasmine.createSpyObj('AuthenticationService', [
            'ClearCredentials',
            'Login'
        ]);
        mockFlashService = jasmine.createSpyObj('FlashService', [
            'Error'
        ])
        module('app');
        inject(function($controller, $q, $httpBackend) {
            deferred = $q
            var response = {
                success: true
            }
            mockAuthenticationService.Login.and.returnValue(deferred.when(response));
            loginController = $controller('LoginController', {
               AuthenticationService: mockAuthenticationService,
               FlashService: mockFlashService
            });
        })
    })

    it('should call ClearCredentials method', function() {
        spyOn(loginController, 'login');
        loginController.login();
        expect(loginController.login).toHaveBeenCalled();
    })

    it('should call UserService Create method when registering', function(){
        var stubUser = {
            username: "C",
            password: "D"
        }
        loginController.login(stubUser);
        deferred.resolve();
        expect(mockAuthenticationService.ClearCredentials).toHaveBeenCalled;
        expect(mockAuthenticationService.Login).toHaveBeenCalled;
    });

})