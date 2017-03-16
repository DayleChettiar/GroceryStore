app.controller('CartItemController', function($scope, $rootScope, $location, $http) {
    function initialize() {
        $scope.cartItems = [];
        $scope.keyword = '';
        $scope.page = 0;
        $scope.selectedIds = [];
        $scope.newId = -1;
        $scope.searchCartItems(0);
        $scope.switchToMode($location.path());
    }
    
    $scope.switchToMode = function(mode) {
        if (mode === '/edit') {
            $http.get('rest/security/user')
                .success(function(user) {
                    $scope.editMode = user;
                    $location.path($scope.editMode? '/edit' : '/view');
                });
        } else {
            $scope.editMode = false;
            $location.path('/view');
        }
    };
    
    // watch selected cartItems
    $scope.$watch('cartItems|filter:{selected:true}', function (results) {
            $scope.selectedIds = results.map(function(cartItem) {
            	cartItem.selected = true;
                return cartItem.id;
            });
        }, true)
    
    $scope.searchCartItems = function(page) {
        if ($scope.isLoading) {
            return;
        }
        
        if ($scope.selectedIds.length > 0) {
            if (!confirm("The selected cartItems are not handled. Forget them and continue to search?")) {
                return;
            }
        }
        
        if (!page) {
            // reset cartItems list if search again
            $scope.cartItems = [];
            page = 0;
        }
        
        var PAGE_SIZE = 200;
        $scope.page = page;
        $scope.isLoading = true;
        $http.get('rest/cartItems?keyword=' + $scope.keyword + '&page=' + $scope.page + '&pageSize=' + PAGE_SIZE)
             .success(function(items) {
                 $scope.hasMoreCartItems = (items.length >= PAGE_SIZE);
                 for (var i=0; i<items.length; i++) {
                     $scope.cartItems.push(items[i]);
                 }
             })
             .finally(function() {
                 $scope.isLoading = false;
             });
    }
    
    $scope.newCartItem = function(type) {
        $scope.keyword = '';
        $scope.cartItems.unshift({
            id : $scope.newId--,
            type : type
        });
    };

    $scope.saveCartItem = function(cartItem) {
        if (!confirm('Your changes will be saved. Are you sure?')) {
            return;
        }
        
        delete cartItem.errors;
        
        var action = (cartItem.id < 0) ? $http.post : $http.put;
        var uri = (cartItem.id < 0) ? 'rest/cartItems/' : 'rest/cartItems/' + cartItem.id;
        action(uri, cartItem).then(function(response) {
        	cartItem.id = response.data.id;
        	cartItem.errors = {};
            document.getElementById('filter').focus();
        }, function(response) {
            if (response.status == 400) {
            	cartItem.errors = response.data;
            }
        });
    };

    $scope.deleteCartItems = function() {
        if (!confirm('Are you sure you want to delete selected cartItem(s)?')) {
            return;
        }
        
        var ids = $scope.selectedIds;
        $http.delete('rest/cartItems?ids=' + ids).then(function(response) {
            for (var i = 0; i < ids.length; i++) {
                for (var j = $scope.cartItems.length - 1; j >= 0; j--) {
                    if ($scope.cartItems[j].id === ids[i]) {
                        $scope.cartItems.splice(j, 1);
                        break;
                    }
                }
            }
        });
    };
    
    $scope.selectPhoto = function(cartItemId) {
        if (!$scope.editMode || cartItemId <= 0) {
            return;
        }
        
        document.getElementById('file' + cartItemId).click();
    };
    
    $scope.uploadPhoto = function(fileId) {
        if (!confirm('Are you sure you want to upload this photo?')) {
            return;
        }
        
        var file = document.getElementById(fileId).files[0];
        var formData = new FormData();
        formData.append('file', file);

        var cartItemId = fileId.substring(4);
        $http.post('rest/photos/' + cartItemId, formData, {
                headers: { 
                    'Content-Type': undefined,
                },
                transformRequest: angular.identity,
              })
              .success(function() {
                  var img = document.getElementById('img' + cartItemId);
                  img.src = 'rest/photos/' + cartItemId + '?t=' + new Date().getTime();
             });
    };
    
    initialize();
});