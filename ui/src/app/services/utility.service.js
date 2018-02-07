export default angular.module('redTail.AppUtilityService', [])
    .factory('AppUtilityService', AppUtilityService)
    .name;

/* @ngInject */
function AppUtilityService() {
    var service = {
        isUndefinedOrNullOrEmpty: isUndefinedOrNullOrEmpty
    };

    return service;

    function isUndefinedOrNullOrEmpty(val) {
        return angular.isUndefined(val) || val === null || (angular.isObject(val) && angular.equals({}, val))
    }
}
