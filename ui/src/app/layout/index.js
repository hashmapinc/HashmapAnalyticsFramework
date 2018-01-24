import uiRouter from 'angular-ui-router';
import ngSanitize from 'angular-sanitize';
import FBAngular from 'angular-fullscreen';
import 'angular-breadcrumb';

import homeLinks from '../home';
import sideMenu from '../menu/side-menu.directive';
import menu from '../services/menu.service';
import HomeRoutes from './home.routes';
import HomeController from './home.controller';

export default angular.module('redTail.home', [
        uiRouter,
        ngSanitize,
        'ncy-angular-breadcrumb',
        FBAngular.name,
        sideMenu,
        menu,
        homeLinks
])
    .config(HomeRoutes)
    .controller('HomeController', HomeController)
    .name;