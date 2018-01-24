/* eslint-disable import/no-unresolved, import/default */

import homeLinksTemplate from './home-links.tpl.html';

/* eslint-enable import/no-unresolved, import/default */

/*@ngInject*/
export default function HomeLinksRoutes($stateProvider) {

    $stateProvider
        .state('home.links', {
            url: 'home',
            //module: 'private',
            //auth: ['SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER'],
            views: {
                "content@home": {
                    templateUrl: homeLinksTemplate,
                    controllerAs: 'vm',
                    controller: 'HomeLinksController'
                }
            },
            data: {
                pageTitle: 'home.home'
            },
            ncyBreadcrumb: {
                label: '{"icon": "home", "label": "home.home"}',
                icon: 'home'
            }
        });
}
