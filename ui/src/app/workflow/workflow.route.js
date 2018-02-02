import workflowTemplate from './workflow.tpl.html';

/*@ngInject*/
export default function WorkflowRoutes($stateProvider) {
  $stateProvider
      .state('home.workflowNew', {
          url: 'workflowNew',
          module: 'private',
          //auth: ['SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER'],
          views: {
              "content@home": {
                  controller: 'WorkflowController',
                  controllerAs: 'vm',
                  templateUrl: workflowTemplate
              }
          },
          data: {
              pageTitle: 'home.workflowNew'
          },
          ncyBreadcrumb: {
            label: '{"icon": "devices_other", "label": "workflow.workflowNew"}'
          }
      });
}
