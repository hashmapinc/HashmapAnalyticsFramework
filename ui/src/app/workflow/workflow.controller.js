/* eslint-disable import/no-unresolved, import/default */

import closeIconSvg from '../../svg/icons/ic_close_24px.svg'

/* eslint-enable import/no-unresolved, import/default */

/* eslint-disable angular/angularelement */

/* @ngInject */
export default function WorkflowController($q, $mdToast, $mdDialog, workflowapiservice) {
  var vm = this;
  vm.title = 'WorkflowController';
  vm.upload = upload;
  vm.closeIconSvg = closeIconSvg;
  vm.cancelDialog = cancelDialog;
  vm.isScheduled = false;

  vm.workflow = {
      name: '',
      start: true,
      xml: '',
      cron: ''
  };

  function cancelDialog() {
        $mdDialog.cancel();
  }

  function upload(workflow, isScheduled) {
      workflowapiservice.saveAndScheduleWorkflow(workflow, isScheduled).then(function(data) {
        $mdDialog.cancel();
        $mdToast.show(
          $mdToast.simple()
            .textContent('Uploaded workflow name: '+data.name+ ' having id: '+ data.id)
            .hideDelay(3000)
          );
        return data;
      });
  }

}
