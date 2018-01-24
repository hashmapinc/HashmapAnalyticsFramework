import './user-menu.scss';

/* eslint-disable import/no-unresolved, import/default */

import userMenuTemplate from './user-menu.tpl.html';

/* eslint-enable import/no-unresolved, import/default */

export default angular.module('redTail.directives.usermenu', [])
    .directive('hmUserMenu', UserMenu)
    .name;

/*@ngInject*/
function UserMenu() {
    return {
        restrict: "E",
        scope: true,
        bindToController: {
            displayUserInfo: '=',
        },
        controller: UserMenuController,
        controllerAs: 'vm',
        templateUrl: userMenuTemplate
    };
}

/*@ngInject*/
function UserMenuController(/*$scope, userService, $translate, $state*/) {

    var vm = this;

    //var dashboardUser = userService.getCurrentUser();

    vm.authorityName = authorityName;
    vm.logout = logout;
    vm.openProfile = openProfile;
    vm.userDisplayName = userDisplayName;

    function authorityName() {
        /*var name = "user.anonymous";
        if (dashboardUser) {
            var authority = dashboardUser.authority;
            if (authority === 'SYS_ADMIN') {
                name = 'user.sys-admin';
            } else if (authority === 'TENANT_ADMIN') {
                name = 'user.tenant-admin';
            } else if (authority === 'CUSTOMER_USER') {
                name = 'user.customer';
            }
        }
        return $translate.instant(name);*/
        return "User";
    }

    function userDisplayName() {
        /*var name = "";
        if (dashboardUser) {
            if ((dashboardUser.firstName && dashboardUser.firstName.length > 0) ||
                (dashboardUser.lastName && dashboardUser.lastName.length > 0)) {
                if (dashboardUser.firstName) {
                    name += dashboardUser.firstName;
                }
                if (dashboardUser.lastName) {
                    if (name.length > 0) {
                        name += " ";
                    }
                    name += dashboardUser.lastName;
                }
            } else {
                name = dashboardUser.email;
            }
        }
        return name;*/
        return "Dummy User";
    }

    function openProfile() {
        //$state.go('home.profile');
    }

    function logout() {
        //userService.logout();
    }
}