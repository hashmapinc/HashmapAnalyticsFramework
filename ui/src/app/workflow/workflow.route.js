import workflowTemplate from './workflow.tpl.html';

/*@ngInject*/
export default function WorkflowRoutes($stateProvider) {
  $stateProvider
      .state('home.workflow', {
          url: 'workflow',
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
              pageTitle: 'home.workflow'
          },
          ncyBreadcrumb: {
            label: '{"icon": "devices_other", "label": "workflow.workflow"}'
          }
      });
}
