/* eslint-disable import/no-unresolved, import/default */
import closeIconSvg from '../../svg/icons/ic_close_24px.svg'
/* eslint-enable import/no-unresolved, import/default */

/* eslint-disable angular/angularelement */

/* @ngInject */
export default function CacheController($mdDialog, $mdToast, workflowapiservice, AppUtilityService, dataToPass) {
    var vm = this;
    vm.title = 'CacheController';
    vm.progressActivated = true;
    vm.getCacheValue = getCacheValue;
    vm.closeIconSvg = closeIconSvg;
    vm.cancelDialog = cancelDialog;


    getCacheValue(dataToPass.workflowId, dataToPass.cacheName, dataToPass.numOfRecords);

    function cancelDialog() {
        $mdDialog.cancel();
    }

    function getCacheValue(workflowId, cacheName, numOfRecords) {
        workflowapiservice.getDataByCacheName(workflowId, cacheName, numOfRecords).then(function (data) {
            vm.progressActivated = false;
            if(AppUtilityService.isUndefinedOrNullOrEmpty(data)) {
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('NO DATA AVAILABLE')
                        .hideDelay(3000)
                );
                return;
            }

            //let dataTemp = [["id","url","name","description","rel","app_metadata"],["1","http://www.postgresqltutorial.com","PostgreSQL Tutorial","null","null","1"],["2","http://www.postgresq.com","Tutorial","null","null","2"]];//data;
            let dataTemp = [];
            angular.extend(dataTemp, data);
            vm.headers = dataTemp.shift();
            vm.dataValues = dataTemp;

        }).catch(function (e) {
            $mdToast.show(
                $mdToast.simple()
                    .textContent('Error while fetching data from backend')
                    .hideDelay(3000)
            );
        });
    }

}
