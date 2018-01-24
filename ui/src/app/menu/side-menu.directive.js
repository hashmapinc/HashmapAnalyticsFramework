/* eslint-disable import/no-unresolved, import/default */
import './side-menu.scss';

import menuService from '../services/menu.service';
import menuLink from './menu-link.directive';

import sidemenuTemplate from './side-menu.tpl.html';

/* eslint-enable import/no-unresolved, import/default */

export default angular.module('redTail.directives.sideMenu', [menuService, menuLink])
    .directive('hmSideMenu', SideMenu)
    .name;

/*@ngInject*/
function SideMenu($compile, $templateCache, menu) {

    var linker = function (scope, element) {

        scope.sections = menu.getSections();

        var template = $templateCache.get(sidemenuTemplate);

        element.html(template);

        $compile(element.contents())(scope);
    }

    return {
        restrict: "E",
        link: linker,
        scope: {}
    };
}
