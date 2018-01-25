export default angular.module('redTail.menu', [])
    .factory('menu', Menu)
    .name;

/*@ngInject*/
function Menu($state) {

    var sections = [];
    var homeSections = [];

    buildMenu();

    var service = {
        getHomeSections: getHomeSections,
        getSections: getSections,
        sectionHeight: sectionHeight,
        sectionActive: sectionActive
    }

    return service;

    function getSections() {
        return sections;
    }

    function getHomeSections() {
        return homeSections;
    }

    function buildMenu() {
        sections = [
            {
                name: 'home.home',
                type: 'link',
                state: 'home.links',
                icon: 'home'
            },
            {
                name: 'Workflow',
                type: 'link',
                state: 'home.workflow',
                icon: 'dashboards'
            }/*,
            {
                name: 'Tempusboard',
                type: 'link',
                state: 'home.tempusboard',
                icon: 'dashboards'
            },
            {
                name: 'plugin.plugins',
                type: 'link',
                state: 'home.plugins',
                icon: 'extension'
            },
            {
                name: 'rule.rules',
                type: 'link',
                state: 'home.rules',
                icon: 'settings_ethernet'
            },
            {
                name: 'customer.customers',
                type: 'link',
                state: 'home.customers',
                icon: 'supervisor_account'
            },
            {
                name: 'asset.assets',
                type: 'link',
                state: 'home.assets',
                icon: 'domain'
            },
            {
                name: 'device.devices',
                type: 'link',
                state: 'home.devices',
                icon: 'devices_other'
            },
            {
                name: 'widget.widget-library',
                type: 'link',
                state: 'home.widgets-bundles',
                icon: 'now_widgets'
            },
            {
                name: 'dashboard.dashboards',
                type: 'link',
                state: 'home.dashboards',
                icon: 'dashboards'
            },
            {
                name: 'computation.computations',
                type: 'link',
                state: 'home.computations',
                icon: 'dashboards'
            }*/];

        homeSections =
            [{
                name: 'rule-plugin.management',
                places: [
                    {
                        name: 'plugin.plugins',
                        icon: 'extension',
                        state: 'home.plugins'
                    },
                    {
                        name: 'rule.rules',
                        icon: 'settings_ethernet',
                        state: 'home.rules'
                    }
                ]
            },
            {
                name: 'customer.management',
                places: [
                    {
                        name: 'customer.customers',
                        icon: 'supervisor_account',
                        state: 'home.customers'
                    }
                ]
            },
            {
                name: 'asset.management',
                places: [
                    {
                        name: 'asset.assets',
                        icon: 'domain',
                        state: 'home.assets'
                    }
                ]
            },
            {
                name: 'Tempusboard',
                places: [
                    {
                        name: 'Tempusboard',
                        icon: 'dashboard',
                        state: 'home.tempusboard'
                    }
                ]
            },
            {
                name: 'device.management',
                places: [
                    {
                        name: 'device.devices',
                        icon: 'devices_other',
                        state: 'home.devices'
                    }
                ]
            },
            {
                name: 'dashboard.management',
                places: [
                    {
                        name: 'widget.widget-library',
                        icon: 'now_widgets',
                        state: 'home.widgets-bundles'
                    },
                    {
                        name: 'dashboard.dashboards',
                        icon: 'dashboard',
                        state: 'home.dashboards'
                    }
                ]
            }];
    }

    function sectionHeight(section) {
        if ($state.includes(section.state)) {
            return section.height;
        } else {
            return '0px';
        }
    }

    function sectionActive(section) {
        return $state.includes(section.state);
    }

}
