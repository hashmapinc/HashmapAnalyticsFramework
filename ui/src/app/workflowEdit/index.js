import uiRouter from 'angular-ui-router';
import ngSanitize from 'angular-sanitize';
//import FBAngular from 'angular-fullscreen';
import 'angular-breadcrumb';

import WorkflowEditRoutes from './workflowEdit.route';
import WorkflowEditController from './workflowEdit.controller';

export default angular.module('redTail.workflowEdit', [
  uiRouter,
  ngSanitize,
  //FBAngular.name,
  'ncy-angular-breadcrumb'
])
  .config(WorkflowEditRoutes)
  .controller('WorkflowEditController', WorkflowEditController)
  .name;
