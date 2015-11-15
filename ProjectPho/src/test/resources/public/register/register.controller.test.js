describe("Register", function() {
    var mockUserService, mockFlashService, registerController, deferred;
    beforeEach(function() {
        mockUserService = jasmine.createSpyObj('UserService', ['Create']);
        mockFlashService = jasmine.createSpyObj('FlashService', ['Success', 'Error']);
        module('app');
        inject(function($controller, $q, $httpBackend) {
            deferred = $q
            var response = {
                success: true
            }
            mockUserService.Create.and.returnValue(deferred.when(response));
            registerController = $controller('RegisterController', {
                UserService: mockUserService,
                FlashService: mockFlashService
            });
        })
    })

    it('should call register method', function() {
        spyOn(registerController, 'register');
        registerController.register();
        expect(registerController.register).toHaveBeenCalled();
    })

    it('should call UserService Create method when registering', function(){
        var stubUser = {
            firstName: "A",
            lastName: "B",
            username: "C",
            password: "D"
        }
        var successMessage = "Registration successful";

        registerController.register(stubUser);
        deferred.resolve();
        expect(mockUserService.Create).toHaveBeenCalled;
        expect(mockFlashService.Success).toHaveBeenCalled;
    });

})