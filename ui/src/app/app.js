import 'event-source-polyfill';

import angular from 'angular';
import ngMaterial from 'angular-material';
import ngMdIcons from 'angular-material-icons';
import ngCookies from 'angular-cookies';
import 'angular-translate';
import 'angular-translate-loader-static-files';
import 'angular-translate-storage-local';
import 'angular-translate-storage-cookie';
import 'angular-translate-handler-log';
import 'angular-translate-interpolation-messageformat';
import 'md-color-picker';
import mdPickers from 'mdPickers';
import ngSanitize from 'angular-sanitize';
import vAccordion from 'v-accordion';
import ngAnimate from 'angular-animate';
import uiRouter from 'angular-ui-router';
import angularJwt from 'angular-jwt';
import 'angular-drag-and-drop-lists';
import mdDataTable from 'angular-material-data-table';
import ngTouch from 'angular-touch';
import 'angular-carousel';
import 'clipboard';
import 'ngclipboard';
import 'material-ui';
import '@flowjs/ng-flow/dist/ng-flow-standalone.min';

import layout from './layout';
import menu from './services/menu.service';
import hmLocales from './translation/locale.constant';
import toast from './toast/toast';
import workflow from './workflow';
import workflowEdit from './workflowEdit';
import workflowapiservice from './services/workflow.service';

import 'typeface-roboto';
import 'font-awesome/css/font-awesome.min.css';
import 'angular-material/angular-material.min.css';
import 'angular-material-icons/angular-material-icons.css';
import 'angular-gridster/dist/angular-gridster.min.css';
import 'v-accordion/dist/v-accordion.min.css'
import 'md-color-picker/dist/mdColorPicker.min.css';
import 'mdPickers/dist/mdPickers.min.css';
import 'angular-hotkeys/build/hotkeys.min.css';
import 'angular-carousel/dist/angular-carousel.min.css';
import '../scss/main.scss';

import AppConfig from './app.config';

angular.module('redTail', [
        uiRouter,
        layout,
        ngMaterial,
        ngMdIcons,
        ngCookies,
        'pascalprecht.translate',
        'mdColorPicker',
        mdPickers,
        ngSanitize,
        vAccordion,
        ngAnimate,
        angularJwt,
        'dndLists',
        mdDataTable,
        ngTouch,
        'angular-carousel',
        'ngclipboard',
        'flow',
        menu,
        hmLocales,
        toast,
        workflow,
        workflowEdit,
        workflowapiservice
      ])
        .config(AppConfig);
