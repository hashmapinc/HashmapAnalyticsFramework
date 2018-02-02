import uiRouter from 'angular-ui-router';
import ngSanitize from 'angular-sanitize';
//import FBAngular from 'angular-fullscreen';
import 'angular-breadcrumb';

import WorkflowRoutes from './workflow.route';
import WorkflowController from './workflow.controller';

export default angular.module('redTail.workflow', [
  uiRouter,
  ngSanitize,
  //FBAngular.name,
  'ncy-angular-breadcrumb'
])
  .config(WorkflowRoutes)
  .controller('WorkflowController', WorkflowController)
  .name;
