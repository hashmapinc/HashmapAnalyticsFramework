import './toast.scss';

/* eslint-disable import/no-unresolved, import/default */
import Toast from './toast.service';
import ToastController from './toast.controller';
/* eslint-enable import/no-unresolved, import/default */

export default angular.module('redTail.toast', [])
    .factory('toast', Toast)
    .controller('ToastController', ToastController)
    .name;
