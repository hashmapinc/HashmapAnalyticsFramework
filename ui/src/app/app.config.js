import injectTapEventPlugin from 'react-tap-event-plugin';
//import UrlHandler from './url.handler';

/* eslint-disable import/no-unresolved, import/default */

import mdiIconSet from '../svg/mdi.svg';

/* eslint-enable import/no-unresolved, import/default */

const PRIMARY_BACKGROUND_COLOR = "#305680";//#2856b6";//"#3f51b5";
const SECONDARY_BACKGROUND_COLOR = "#527dad";
const HUE3_COLOR = "#a7c1de";

/*@ngInject*/
export default function AppConfig($provide,
                                  $urlRouterProvider,
                                  $locationProvider,
                                  $mdIconProvider,
                                  $mdThemingProvider,
                                  $httpProvider,
                                  $translateProvider,
                                  locales) {

    injectTapEventPlugin();
    $locationProvider.html5Mode(true);
    //$urlRouterProvider.otherwise(UrlHandler);
    //storeProvider.setCaching(false);

    $translateProvider.useSanitizeValueStrategy('sce');
    $translateProvider.preferredLanguage('en_US');
    $translateProvider.useLocalStorage();
    $translateProvider.useMissingTranslationHandler('hmMissingTranslationHandler');
    $translateProvider.addInterpolation('$translateMessageFormatInterpolation');

    /*var $window = angular.injector(['ng']).get('$window');
    var lang = $window.navigator.language || $window.navigator.userLanguage;
    if (lang === 'ko') {
        $translateProvider.useSanitizeValueStrategy(null);
        $translateProvider.preferredLanguage('ko_KR');
    } else if (lang === 'zh') {
        $translateProvider.useSanitizeValueStrategy(null);
        $translateProvider.preferredLanguage('zh_CN');
    } else if (lang === 'es') {
        $translateProvider.useSanitizeValueStrategy(null);
        $translateProvider.preferredLanguage('es_ES');
    }*/

    for (var langKey in locales) {
        var translationTable = locales[langKey];
        $translateProvider.translations(langKey, translationTable);
    }

    //$httpProvider.interceptors.push('globalInterceptor');

    $provide.decorator("$exceptionHandler", ['$delegate', '$injector', function ($delegate/*, $injector*/) {
        return function (exception, cause) {
/*            var rootScope = $injector.get("$rootScope");
            var $window = $injector.get("$window");
            var utils = $injector.get("utils");
            if (rootScope.widgetEditMode) {
                var parentScope = $window.parent.angular.element($window.frameElement).scope();
                var data = utils.parseException(exception);
                parentScope.$emit('widgetException', data);
                parentScope.$apply();
            }*/
            $delegate(exception, cause);
        };
    }]);

    $mdIconProvider.iconSet('mdi', mdiIconSet);

    configureTheme();

    function blueGrayTheme() {
        var primaryPalette = $mdThemingProvider.extendPalette('blue-grey');
        var accentPalette = $mdThemingProvider.extendPalette('orange', {
            'contrastDefaultColor': 'light'
        });

        $mdThemingProvider.definePalette('hm-primary', primaryPalette);
        $mdThemingProvider.definePalette('hm-accent', accentPalette);

        $mdThemingProvider.theme('default')
            .primaryPalette('hm-primary')
            .accentPalette('hm-accent');

        $mdThemingProvider.theme('hm-dark')
            .primaryPalette('hm-primary')
            .accentPalette('hm-accent')
            .backgroundPalette('hm-primary')
            .dark();
    }

    function indigoTheme() {
        var primaryPalette = $mdThemingProvider.extendPalette('indigo', {
            '500': PRIMARY_BACKGROUND_COLOR,
            '600': SECONDARY_BACKGROUND_COLOR,
            'A100': HUE3_COLOR
        });

        var accentPalette = $mdThemingProvider.extendPalette('deep-orange');

        $mdThemingProvider.definePalette('hm-primary', primaryPalette);
        $mdThemingProvider.definePalette('hm-accent', accentPalette);

        var darkPrimaryPalette = $mdThemingProvider.extendPalette('hm-primary', {
            '500': '#9fa8da'
        });

        var darkPrimaryBackgroundPalette = $mdThemingProvider.extendPalette('hm-primary', {
            '800': PRIMARY_BACKGROUND_COLOR
        });

        $mdThemingProvider.definePalette('hm-dark-primary', darkPrimaryPalette);
        $mdThemingProvider.definePalette('hm-dark-primary-background', darkPrimaryBackgroundPalette);

        $mdThemingProvider.theme('default')
            .primaryPalette('hm-primary')
            .accentPalette('hm-accent');

        $mdThemingProvider.theme('hm-dark')
            .primaryPalette('hm-dark-primary')
            .accentPalette('hm-accent')
            .backgroundPalette('hm-dark-primary-background')
            .dark();
    }

    function configureTheme() {

        var theme = 'indigo';

        if (theme === 'blueGray') {
            blueGrayTheme();
        } else {
            indigoTheme();
        }

        $mdThemingProvider.setDefaultTheme('default');
        //$mdThemingProvider.alwaysWatchTheme(true);
    }

}