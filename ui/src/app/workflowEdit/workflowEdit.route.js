import workflowEditTemplate from './workflowEdit.tpl.html';

/*@ngInject*/
export default function WorkflowRoutes($stateProvider) {
  $stateProvider
      .state('home.workflowEdit', {
          url: 'workflowEdit',
          module: 'private',
          //auth: ['SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER'],
          views: {
              "content@home": {
                  controller: 'WorkflowEditController',
                  controllerAs: 'vm',
                  templateUrl: workflowEditTemplate
              }
          },
          data: {
              pageTitle: 'home.workflowEdit'
          },
          ncyBreadcrumb: {
            label: '{"icon": "devices_other", "label": "workflow.workflowEdit"}'
          }
      });
}
