/*@ngInject*/
export default function ToastController($mdToast, message) {
    var vm = this;
    vm.message = message;

    vm.closeToast = closeToast;

    function closeToast() {
        $mdToast.hide();
    }

}
