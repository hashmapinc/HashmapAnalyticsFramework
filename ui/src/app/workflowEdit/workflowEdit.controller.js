import $ from 'jquery';

/* eslint-disable import/no-unresolved, import/default */

import logoSvg from '../../svg/hm-tempus-logo.svg';

import WorkflowController from "../workflow/workflow.controller";
import workflowTemplate from '../workflow/workflow.tpl.html';

/* eslint-enable import/no-unresolved, import/default */

/* eslint-disable angular/angularelement */

/* @ngInject */
export default function WorkflowEditController($scope, $q, $log, $mdDialog, $rootScope, $mdToast, workflowapiservice) {
    var vm = this;
    vm.title = 'WorkflowEditController';
    vm.workflows = [];
    vm.selectedIndex = 0;
    vm.workflowXml = "";

    vm.createWorkflow = createWorkflow;
    activate();

    // Broadcasted from workflow service
    let savedWorkflowEvent = $rootScope.$on('savedWorkflow', function (event, data) {
        $log.info('WorkflowEditController: Received broadcast event name: savedWorkflow');
        vm.workflows.unshift(data.workflow);
        vm.selectedIndex = 0;
        vm.workflowXml = data.workflow.xml;
    });

    //Destroying event handler manually when controller destory event gets called
    $scope.$on('$destroy', () => savedWorkflowEvent.$destroy());

    function createWorkflow(ev) {
        $mdDialog.show({
            controller: WorkflowController,
            controllerAs: 'vm',
            templateUrl: workflowTemplate,
            //parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose:false
        })
    }

    function activate() {
      var promises = [getWorkflow()];
      return $q.all(promises).then(function() {
        $log.info('Activated WorkflowEditController View');
      });
    }

    function isUndefinedOrNullOrEmpty(val) {
        return angular.isUndefined(val) || val === null || (angular.isObject(val) && angular.equals({}, val))
    }
    function getWorkflow() {
        workflowapiservice.getDetailedWorkflow().then(function(data) {
            if(isUndefinedOrNullOrEmpty(data)) {
                $mdToast.show(
                  $mdToast.simple()
                    .textContent('NO WORKFLOWS DEFINED YET')
                    .hideDelay(3000)
                  );
                return;
            }
            //console.dir(data);
          //data = ["workflow1", "workflow2", "workflow3"];
          /*let data1 = [{
            id: "1",
            name:"cxvx",
            start: true,
            cron: "None",
            xml: "XML1"
          }, {
              id: "2",
              name:"asd",
              start: false,
              cron: "None",
              xml: "XML2"
          }];*/
          //let workflows = [];
          let data1 = Object.keys(data).map(id => {
              let temp = {};
              let currObj = data[id];
              temp.id = currObj.id;
              temp.cron = currObj.cronExpression;
              temp.name = currObj.name;
              temp.start = currObj.isRunning;
              temp.xml = currObj.xml;
              return temp;
          });
          $log.info(data1);
          vm.workflows = data1;
          vm.workflowXml = data1[0]["xml"];
          //logger.info('Uploaded workflow with id: '+ data);
          // $mdToast.show(
          //   $mdToast.simple()
          //     .textContent('Uploaded workflow with id: '+ data)
          //     .hideDelay(3000)
          //   );
          return vm.workflows;
        }).catch(function (e) {
            $mdToast.show(
                $mdToast.simple()
                    .textContent('Error while fetching data from backend')
                    .hideDelay(3000)
            );
            return;
        });
    }

}
