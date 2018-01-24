import uiRouter from 'angular-ui-router';

import HomeLinksRoutes from './home-links.routes';
import HomeLinksController from './home-links.controller';

export default angular.module('redTail.homeLinks', [
    uiRouter
])
    .config(HomeLinksRoutes)
    .controller('HomeLinksController', HomeLinksController)
    .name;
