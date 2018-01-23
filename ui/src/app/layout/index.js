import uiRouter from 'angular-ui-router';
import ngSanitize from 'angular-sanitize';
import FBAngular from 'angular-fullscreen';
import 'angular-breadcrumb';

import HomeRoutes from './home.routes';
import HomeController from './home.controller';

export default angular.module('redTail.home', [
        uiRouter,
        ngSanitize,
        'ncy-angular-breadcrumb'
])
    .config(HomeRoutes)
    .controller('HomeController', HomeController)
    .name;