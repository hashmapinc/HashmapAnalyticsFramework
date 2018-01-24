import $ from 'jquery';

/* eslint-disable import/no-unresolved, import/default */

import logoSvg from '../../svg/hm-tempus-logo.svg';

/* eslint-enable import/no-unresolved, import/default */

/* eslint-disable angular/angularelement */

/*@ngInject*/
export default function HomeController(Fullscreen, $scope, $element, $rootScope, $document, $state,
                                       $window, $log, $mdMedia, $animate, $timeout) {

    var siteSideNav = $('.hm-site-sidenav', $element);

    var vm = this;

    vm.Fullscreen = Fullscreen;
    vm.logoSvg = logoSvg;
    vm.isShowSidenav = false;
    vm.isLockSidenav = false;

    vm.openSidenav = openSidenav;
    vm.goBack = goBack;
    vm.sidenavClicked = sidenavClicked;
    vm.toggleFullscreen = toggleFullscreen;

    vm.isGtSm = $mdMedia('gt-sm');
    if (vm.isGtSm) {
        vm.isLockSidenav = true;
        $animate.enabled(siteSideNav, false);
    }

    $scope.$watch(function() { return $mdMedia('gt-sm'); }, function(isGtSm) {
        vm.isGtSm = isGtSm;
        vm.isLockSidenav = isGtSm;
        vm.isShowSidenav = isGtSm;
        if (!isGtSm) {
            $timeout(function() {
                $animate.enabled(siteSideNav, true);
            }, 0, false);
        } else {
            $animate.enabled(siteSideNav, false);
        }
    });

    function toggleFullscreen() {
        if (Fullscreen.isEnabled()) {
            Fullscreen.cancel();
        } else {
            Fullscreen.all();
        }
    }

    function openSidenav() {
        vm.isShowSidenav = true;
    }

    function goBack() {
        $window.history.back();
    }

    function closeSidenav() {
        vm.isShowSidenav = false;
    }

    function sidenavClicked() {
        if (!vm.isLockSidenav) {
            closeSidenav();
        }
    }

}

/* eslint-enable angular/angularelement */