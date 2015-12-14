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
            // get all the user's photos.
            openImage();
        });

        $("#hide_open_link").on('click', function(e) {
            e.preventDefault();
            $("#open").css('display', 'none');
        });

        $("#invite_link").on('click', function(e){
            e.preventDefault();
            if (vm.isInSession) {
                $("#invite_message").css('display', 'block');
            } else {
                alert("Please open a photo first!");
            }
        });

        $("#hide_invite_link").on('click', function(e) {
            e.preventDefault();
            $("#invite_message").css('display', 'none');
        });

        $("#revisions_link").on('click', function(e) {
            e.preventDefault();
            if (vm.isInSession) {
                $("#revisions").css('display', 'block');
                // Get all revisions
                getRevisions();
            } else {
                alert("Please open a photo first!");
            }
        });

        $("#hide_revisions_link").on('click', function(e) {
            e.preventDefault();
            console.log("Hide revisions");
            $("#revisions").css('display', 'none');
        });


        $("#upload:hidden").on('change', function(e){
            // var selectedFile = this.files[0];
            // selectedFile.convertToBase64(function(base64){
            //    alert(base64);
            // })
            uploadImage();
        });

        $('#photo-title').on('change', function(e) {
            e.preventDefault();
            if (vm.isInSession) {
                changeTitle(document.getElementById('photo-title').value);
            } else {
                alert("Please open a photo first!");
            }
        })


        var vm = this;

        vm.user = null;
        vm.allUsers = [];
        vm.deleteUser = deleteUser;

        initController();

        // Left panel starts here

        vm.currentTool = '';

        vm.doBlur = function() {
            // TODO: Add a percentage
            applyFilter('BlurFilter', {});
        }
        vm.doEdgeDetect = function() {
            applyFilter('EdgeDetectionFilter', {});
        }

        function applyFilter(filterType, params) {
            var content = {};
            content['canvasId'] = vm.canvasId;
            content['editType'] = filterType;
            content['moreParams'] = JSON.stringify(params);
            $http.post("/api/edit/" + vm.pId + "/change", JSON.stringify(content), {
                withCredentials: true,
                headers: {'Content-Type': undefined },
                transformRequest: angular.identity
            })
            .success(function() {
                console.log(filterType + " applied. Wait for fetch and update.");
            })
            .error(function() {
                alert("Failed to apply filter.");
            });
        }

        // Left panel ends here

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
        vm.revisionsOfPhoto = null;
        vm.pid = "";
        vm.url = "undefined";
        vm.html = "";
        vm.isInSession = false;
        vm.canvasId = "";
        vm.collaborators = [];
        vm.title = "";
        vm.canvasData = "iVBORw0KGgoAAAANSUhEUgAAAAQAAAAECAYAAACp8Z5+AAAMGWlDQ1BJQ0MgUHJvZmlsZQAASImVVwdYU8kWnltSCAktEAEpoTdBepXeO9LBRkgChBJDIKjYkUUF14KKCIqKroAouhZA1ooFCyLY+4KIysq6WLCh8iYJoOu+8r3zfXPnz5lzzvzn3JmbGQDkbVkCQSaqAEAWP1cY6e/FjE9IZJJ+BxSAAkXgDLRZ7ByBZ0RECIAy1v9d3t0CiLi/bi6O9c/x/yqKHG4OGwAkAuJkTg47C+LDAODqbIEwFwBCJ9TrzckViPFbiJWFkCAARLIYp0qxhhgnS7GlxCY60htiHwDIVBZLmAqAnDg+M4+dCuPICSC25HN4fIi3Q+zGTmNxIO6GeFJW1myI5akQGyd/Fyf1bzGTx2OyWKnjWJqLRMg+vBxBJmve/1mO/y1ZmaKxOXRho6YJAyLFOcO61WbMDhZjyB05xk8OC4dYCeILPI7EXozvpYkCYkbtB9g53rBmgAHgy+awfIIhhrVEGaKMGM9RbM0SSnyhPRrGyw2MHsXJwtmRo/HRPH5mWMhonBVp3MAxXMXN8Y0as0nh+QVCDFcaejg/LTpOyhM9m8eLDYNYDuLOnIyo4FHfR/lp3mFjNkJRpJizPsRvU4R+kVIbTDUrZywvzILNksylCrFHblp0gNQXi+fmxIeMceBwfXylHDAOlx8zyg2Dq8srctS3SJAZMWqPVXEz/SOldcYO5ORFjfley4ULTFoH7HE6KyhCyh97J8iNiJZyw3EQAryBD2ACEWzJYDZIB7yOgaYB+Es64gdYQAhSAReYj2rGPOIkI3z4jAL54E+IuCBn3M9LMsoFeVD/ZVwrfZqDFMlonsQjAzyFOAtXx91wFzwEPj1gs8YdcacxP6b82KxEX6IPMYDoRzQZ58GGrDNhEwLev9EFw54LsxNz4Y/l8C0e4Smhi/CYcJPQTbgLYsETSZRRq1m8AuEPzJkgFHTDaH6j2SXDmP1jNrghZG2He+GukD/kjjNwdWCO28JMPHF3mJsd1H7PUDTO7Vstf5xPzPr7fEb1cqZydqMsksffjPe41Y9RvL+rEQf2wT9aYiuwQ1gbdhq7iB3DmgATO4k1Y+3YcTEeXwlPJCthbLZICbcMGIc3ZmNZb9lv+fkfs7NGGQgl7xvkcufmijeE92zBPCEvNS2X6Qm/yFxmIJ9tMYlpbWllB4D4+y79fLxhSL7bCOPSN132KQCciqEy9ZuOpQfA0acA0N990+m9httrLQDHO9kiYZ5Uh4sfBPjPIQ93hhrQAnrAGOZkDeyBC/AAviAIhINokABmwqqngSzIeg5YAJaCIlAC1oKNoAJsAztBLdgHDoImcAycBufBZdAJboL7cG30gRdgELwDwwiCkBAaQkfUEG3EADFDrBFHxA3xRUKQSCQBSUJSET4iQhYgy5ASpBSpQHYgdcivyFHkNHIR6ULuIj1IP/Ia+YRiKBVVRjVRQ3Qy6oh6osFoNDoDTUWz0Xy0EF2NlqPV6F60ET2NXkZvot3oC3QIA5gsxsB0MHPMEfPGwrFELAUTYouwYqwMq8YasBb4rq9j3dgA9hEn4nSciZvD9RmAx+BsPBtfhK/CK/BavBE/i1/He/BB/CuBRtAgmBGcCYGEeEIqYQ6hiFBG2E04QjgH904f4R2RSGQQjYgOcG8mENOJ84mriFuJ+4mniF3EXuIQiURSI5mRXEnhJBYpl1RE2kzaSzpJukbqI30gy5K1ydZkP3IimU8uIJeR95BPkK+Rn5GHZRRkDGScZcJlODLzZNbI7JJpkbkq0yczTFGkGFFcKdGUdMpSSjmlgXKO8oDyRlZWVlfWSXaqLE92iWy57AHZC7I9sh+pSlRTqjd1OlVEXU2toZ6i3qW+odFohjQPWiItl7aaVkc7Q3tE+yBHl7OQC5TjyC2Wq5RrlLsm91JeRt5A3lN+pny+fJn8Ifmr8gMKMgqGCt4KLIVFCpUKRxVuKwwp0hWtFMMVsxRXKe5RvKj4XImkZKjkq8RRKlTaqXRGqZeO0fXo3nQ2fRl9F/0cvU+ZqGykHKicrlyivE+5Q3lQRUnFViVWZa5KpcpxlW4GxjBkBDIyGWsYBxm3GJ8maE7wnMCdsHJCw4RrE96rTlT1UOWqFqvuV72p+kmNqearlqG2Tq1J7aE6rm6qPlV9jnqV+jn1gYnKE10msicWTzw48Z4GqmGqEakxX2OnRrvGkKaWpr+mQHOz5hnNAS2GlodWutYGrRNa/dp0bTdtnvYG7ZPafzBVmJ7MTGY58yxzUEdDJ0BHpLNDp0NnWNdIN0a3QHe/7kM9ip6jXoreBr1WvUF9bf1Q/QX69fr3DGQMHA3SDDYZtBm8NzQyjDNcbthk+NxI1SjQKN+o3uiBMc3Y3TjbuNr4hgnRxNEkw2SrSacpampnmmZaaXrVDDWzN+OZbTXrmkSY5DSJP6l60m1zqrmneZ55vXmPBcMixKLAosni5WT9yYmT101um/zV0s4y03KX5X0rJasgqwKrFqvX1qbWbOtK6xs2NBs/m8U2zTavbM1subZVtnfs6HahdsvtWu2+2DvYC+0b7Psd9B2SHLY43HZUdoxwXOV4wYng5OW02OmY00dne+dc54POf7mYu2S47HF5PsVoCnfKrim9rrquLNcdrt1uTLckt+1u3e467iz3avfHHnoeHI/dHs88TTzTPfd6vvSy9BJ6HfF67+3svdD7lA/m4+9T7NPhq+Qb41vh+8hP1y/Vr95v0N/Of77/qQBCQHDAuoDbgZqB7MC6wMEgh6CFQWeDqcFRwRXBj0NMQ4QhLaFoaFDo+tAHYQZh/LCmcBAeGL4+/GGEUUR2xG9TiVMjplZOfRppFbkgsi2KHjUrak/Uu2iv6DXR92OMY0QxrbHysdNj62Lfx/nElcZ1x0+OXxh/OUE9gZfQnEhKjE3cnTg0zXfaxml90+2mF02/NcNoxtwZF2eqz8yceXyW/CzWrENJhKS4pD1Jn1nhrGrWUHJg8pbkQbY3exP7BceDs4HTz3XllnKfpbimlKY8T3VNXZ/an+aeVpY2wPPmVfBepQekb0t/nxGeUZMxkhmXuT+LnJWUdZSvxM/gn52tNXvu7C6BmaBI0J3tnL0xe1AYLNydg+TMyGnOVYZHnXaRsegnUU+eW15l3oc5sXMOzVWcy5/bPs903sp5z/L98n+Zj89nz29doLNg6YKehZ4LdyxCFiUval2st7hwcd8S/yW1SylLM5ZeKbAsKC14uyxuWUuhZuGSwt6f/H+qL5IrEhbdXu6yfNsKfAVvRcdKm5WbV34t5hRfKrEsKSv5vIq96tLPVj+X/zyyOmV1xxr7NVVriWv5a2+tc19XW6pYml/auz50feMG5obiDW83ztp4scy2bNsmyibRpu7ykPLmzfqb127+XJFWcbPSq3L/Fo0tK7e838rZeq3Ko6phm+a2km2ftvO239nhv6Ox2rC6bCdxZ97Op7tid7X94vhL3W713SW7v9Twa7prI2vP1jnU1e3R2LOmHq0X1ffvnb63c5/PvuYG84Yd+xn7Sw6AA6IDf/ya9Outg8EHWw85Hmo4bHB4yxH6keJGpHFe42BTWlN3c0Jz19Ggo60tLi1HfrP4reaYzrHK4yrH15ygnCg8MXIy/+TQKcGpgdOpp3tbZ7XePxN/5sbZqWc7zgWfu3De7/yZNs+2kxdcLxy76Hzx6CXHS02X7S83ttu1H7lid+VIh31H41WHq82dTp0tXVO6Tlxzv3b6us/18zcCb1y+GXaz61bMrTu3p9/uvsO58/xu5t1X9/LuDd9f8oDwoPihwsOyRxqPqn83+X1/t3338R6fnvbHUY/v97J7XzzJefK5r/Ap7WnZM+1ndc+tnx/r9+vv/GPaH30vBC+GB4r+VPxzy0vjl4f/8virfTB+sO+V8NXI61Vv1N7UvLV92zoUMfToXda74ffFH9Q+1H50/Nj2Ke7Ts+E5n0mfy7+YfGn5Gvz1wUjWyIiAJWRJjgIYbGhKCgCvawCgJcCzA7zHUeSk9y+JINI7owSB/4SldzSJ2ANQ4wFAzBIAQuAZpQo2A4ipsBcfv6M9AGpjM95GJSfFxloaiwpvMYQPIyNvNAEgtQDwRTgyMrx1ZOTLLkj2LgCnsqX3PrEQ4Rl/u+Scc0VvOfhR/gW2WWxMw2i5lgAAAAlwSFlzAAAWJQAAFiUBSVIk8AAAAZlpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IlhNUCBDb3JlIDUuNC4wIj4KICAgPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KICAgICAgPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIKICAgICAgICAgICAgeG1sbnM6ZXhpZj0iaHR0cDovL25zLmFkb2JlLmNvbS9leGlmLzEuMC8iPgogICAgICAgICA8ZXhpZjpQaXhlbFhEaW1lbnNpb24+NDwvZXhpZjpQaXhlbFhEaW1lbnNpb24+CiAgICAgICAgIDxleGlmOlBpeGVsWURpbWVuc2lvbj40PC9leGlmOlBpeGVsWURpbWVuc2lvbj4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgIDwvcmRmOlJERj4KPC94OnhtcG1ldGE+Cpcz29QAAAAcaURPVAAAAAIAAAAAAAAAAgAAACgAAAACAAAAAgAAAEdTv4gHAAAAE0lEQVQYGWJcfvL7fwYkwIguAAAAAP//8SyyzQAAABFJREFUY1x+8vt/BiTAiC4AAB5gDZ08/aekAAAAAElFTkSuQmCC";
        vm.versionId = "";


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
                console.log("Set interval to fetch.");
                window.setInterval(keepUpdated, 500);
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
            .success(function(response) {
                console.log("Upload success.");
                window.location.href = '#/edit/' + response['pId'];
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

        function getRevisions() {
            // TODO: get by request and response
            $http.get("/api/edit/" + vm.pId + "/versions", {
                withCredentials: true,
                headers: {'Content-Type': undefined },
                transformRequest: angular.identity
            })
            .success(function(response) {
                vm.revisionsOfPhoto = response['versions'];
            })
            .error(function() {
                console.log("Failed to get revisions.");
            })
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

        function keepUpdated() {
            $http.get("/api/edit/" + vm.pId + "/canvasid", {
                withCredentials: true,
                headers: {'Content-Type': undefined },
                transformRequest: angular.identity
            })
            .success(function(response) {
                if (vm.canvasId == response['canvasId']) {
                    console.log("Synced!")
                } else {
                    console.log("Not synced, going to fetch and update canvas.")
                    // Fetch and update everything, including canvasId
                    fetchAndUpdate();
                }
            })
            .error(function() {
                console.log("Failed to get newest canvas id.");
            })
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

                //var image = new Image();
                //image.onload = function() {
                //    ctx.drawImage(image, 0, 0);
                //};
                //image.src = "data:image/png;base64; " + vm.canvasData;

            })
            .error(function() {
                console.log("Fetch failed");
            });
            /*if(vm.canvasData) {
            $('#heads_up').css('display', 'none');
            var html = "<img data-ng-src=\"data:image/png;base64, {{vm.canvasData}}\" data-err-src=\"./images/icon.png\" border=\"1px\">";
            $('#canvas').html(html);
            }*/
        }

        /*function hasCanvasData() {
            if(!vm.canvasData) {
                return false;
            }
            return true;
        }*/

        function changeTitle(newTitle) {
            console.log("Change title to: " + newTitle);

            var content = {'title': newTitle};
            $http.post("/api/edit/" + vm.pId + "/edittitle", JSON.stringify(content), {
                withCredentials: true,
                headers: {'Content-Type': undefined },
                transformRequest: angular.identity
            })
            .success(function() {
                console.log("Title changed successfully!");
            })
            .error(function() {
                console.log("Failed to change title.");
            });
        }

        function saveVersion(){

        }
    }

})();