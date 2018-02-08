import $ from 'jquery';

/* eslint-disable import/no-unresolved, import/default */

import logoSvg from '../../svg/hm-tempus-logo.svg';
import launchSvg from '../../svg/icons/launch.svg'

import WorkflowController from "../workflow/workflow.controller";
import workflowTemplate from '../workflow/workflow.tpl.html';
import CacheController from "../cache/cache.controller";
import CacheTemplate from '../cache/cache.tpl.html';
import AppUtilityService from "../services/utility.service";

/* eslint-enable import/no-unresolved, import/default */

/* eslint-disable angular/angularelement */

/* @ngInject */
export default function WorkflowEditController($scope, $q, $log, $mdDialog, $rootScope, $mdToast,
                                               workflowapiservice, AppUtilityService) {
    var vm = this;
    vm.launchSvg = launchSvg;
    vm.title = 'WorkflowEditController';
    vm.workflows = [];
    vm.selectedIndex = 0;
    vm.workflowXml = "";
    vm.selected = [];

    vm.createWorkflow = createWorkflow;
    vm.displayCacheData = displayCacheData;
    vm.onClickSelectWorkflow = onClickSelectWorkflow;
    activate();

    // Broadcasted from workflow service
    let savedWorkflowEvent = $rootScope.$on('savedWorkflow', function (event, data) {
        $log.info('WorkflowEditController: Received broadcast event name: savedWorkflow');
        vm.workflows.unshift(data.workflow);
        onClickSelectWorkflow(0);
    });

    //Destroying event handler manually when controller destory event gets called
    $scope.$on('$destroy', () => savedWorkflowEvent.$destroy());

    function createWorkflow(ev) {
        $mdDialog.show({
            controller: WorkflowController,
            controllerAs: 'vm',
            templateUrl: workflowTemplate,
            targetEvent: ev,
            clickOutsideToClose:false
        })
    }

    function displayCacheData(cacheName, ev) {
        let _dataToPass = {
            workflowId: vm.workflows[vm.selectedIndex].id,
            cacheName: cacheName,
            numOfRecords: 100
        };
        $mdDialog.show({
            locals:{dataToPass: _dataToPass},
            controller: CacheController,
            controllerAs: 'vm',
            templateUrl: CacheTemplate,
            targetEvent: ev,
            clickOutsideToClose:false
        })
    }

    function activate() {
      let promises = [getWorkflow()];
      return $q.all(promises).then(function() {
        $log.info('Activated WorkflowEditController View');
      });
    }

    function onClickSelectWorkflow(index){
        vm.selectedIndex = index;
        setupModel(index);
    }

    function setupModel(index){
        vm.workflowXml = vm.workflows[index].xml;
        vm.workflowTasks = vm.workflows[index].tasks;
    }

    function getWorkflow() {
        workflowapiservice.getDetailedWorkflow().then(function(data) {
            if(AppUtilityService.isUndefinedOrNullOrEmpty(data)) {
                $mdToast.show(
                  $mdToast.simple()
                    .textContent('NO WORKFLOWS DEFINED YET')
                    .hideDelay(3000)
                  );
                return;
            }
          vm.workflows = data;
          setupModel(0);
          return vm.workflows;
        }).catch(function (e) {
            $mdToast.show(
                $mdToast.simple()
                    .textContent('Error while fetching data from backend')
                    .hideDelay(3000)
            );
        });
    }

}
