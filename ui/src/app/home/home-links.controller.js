/*@ngInject*/
export default function HomeLinksController($scope, menu) {
    var vm = this;
    vm.model = menu.getHomeSections();
}
