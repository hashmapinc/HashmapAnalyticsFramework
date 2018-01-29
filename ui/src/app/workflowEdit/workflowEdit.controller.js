import $ from 'jquery';

/* eslint-disable import/no-unresolved, import/default */

import logoSvg from '../../svg/hm-tempus-logo.svg';

/* eslint-enable import/no-unresolved, import/default */

/* eslint-disable angular/angularelement */

/* @ngInject */
export default function WorkflowEditController($q, workflowapiservice, $mdToast) {
    var vm = this;
    vm.title = 'WorkflowEditController';
    vm.workflows = []
    activate();

    function activate() {
      var promises = [getWorkflow()];
      return $q.all(promises).then(function() {
        console.log('Activated WorkflowEditController View');
      });
    }

    function getWorkflow() {
        workflowapiservice.getWorkflow().then(function(data) {
          //data = ["workflow1", "workflow2", "workflow3"];
          data = [{
            id: "1",
            status: "Not Running",
            cronExpression: "None",
            xml: "XML1"
          }, {
            id: "2",
            status: "Running",
            cronExpression: "*/5 * * * * *",
            xml: "XML2"
          }, {
            id: "3",
            status: "Not Running",
            cronExpression: "*/5 1 * * * *",
            xml: "XML3"
          }];
          vm.workflows = data;
          vm.selectedIndex = 0;
          vm.workflowXml = data[0]["xml"]
          //logger.info('Uploaded workflow with id: '+ data);
          // $mdToast.show(
          //   $mdToast.simple()
          //     .textContent('Uploaded workflow with id: '+ data)
          //     .hideDelay(3000)
          //   );
          return vm.workflows;
        });
    }

}
