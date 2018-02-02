export default angular.module('redTail.workflowapiservice', [])
    .factory('workflowapiservice', Workflowapiservice)
    .name;

/* @ngInject */
function Workflowapiservice($http, $q, $log, $rootScope) {
  var service = {
    saveAndScheduleWorkflow: saveAndScheduleWorkflow,
    getDetailedWorkflow: getDetailedWorkflow,
    deleteWorkflow: deleteWorkflow
  };

  return service;

  function saveAndScheduleWorkflow(workflow) {
      return saveWorkflow(workflow)
          .then(saveWorkflowInScheduler)
          .then(success)
          .catch(fail);

      function success(savedWorkflow) {
          return savedWorkflow;
      }

      function fail(e) {
          $log.error('XHR Failed for saveAndScheduleWorkflow');
          return e;
      }
  }

  function saveWorkflow(workflow) {

    return $http.put('/workflows-api/api/workflows', workflow.xml, {
      'headers': { "Content-Type": 'text/xml'},
      'responseType': 'text'
    })
      .then(success)
      .catch(fail);

    function success(response) {
        $log.info('Inside success');
        let responseData = {};
        angular.extend(responseData,workflow);
        responseData.id = response.data.id;
        responseData.name = response.data.name;
        return responseData;
    }

    function fail(e) {
      $log.error('XHR Failed for saveWorkflow');
      return e;
    }
}

  function saveWorkflowInScheduler(savedWorkflow){
      $log.info(savedWorkflow);
      let workflowForScheduler = {};

      workflowForScheduler.isRunning = savedWorkflow.start;
      workflowForScheduler.cronExpression = savedWorkflow.cron;
      workflowForScheduler.id = savedWorkflow.id;

      return $http.post('/scheduler/api/workflow', workflowForScheduler)
          .then(success)
          .catch(fail);

      function success(response) {
          $rootScope.$broadcast('savedWorkflow', {
              workflow: savedWorkflow
          });
          return savedWorkflow;
      }

      function fail(e) {
          $log.error('XHR Failed for saveWorkflowInScheduler');
          return e;
      }

  }

  function getDetailedWorkflow() {
      return getAllWorkflows()
          .then(getScheduleInfo)
          .then(success)
          .catch(fail);

      function success(workflows) {
          if(isUndefinedOrNullOrEmpty(workflows)) return;
          $log.info(workflows);
          return workflows;
      }

      function fail(e) {
          $log.error('XHR Failed for saveAndScheduleWorkflow');
          return e;
      }
  }


  function getAllWorkflows() {
    return $http.get('/workflows-api/api/workflows')
      .then(success)
      .catch(fail);

    function success(response) {
      return response.data;
    }

    function fail(e) {
      $log.error('XHR Failed for getWorkflowById');
      return e;
    }
  }

    function getScheduleInfo(workflows) {
        if(isUndefinedOrNullOrEmpty(workflows)) return;
        let workflowIds = Object.keys(workflows);
        return $http.post('/scheduler/api/workflows', workflowIds)
            .then(success)
            .catch(fail);

        function success(response) {
            let retObj = {};
            angular.extend(retObj, workflows);
            angular.merge(retObj, response.data);
            $log.info(retObj);
            return retObj;
        }

        function fail(e) {
            $log.error('XHR Failed for getWorkflowById');
            return e;
        }
    }

  function deleteWorkflow(workflowId) {
    return $http.delete('/api/workflows'+ workflowId)
      .then(success)
      .catch(fail);

    function success(response) {
      return response.data;
    }

    function fail(e) {
      $log.error('XHR Failed for delete workflow');
      return e;
    }
  }

    function isUndefinedOrNullOrEmpty(val) {
        return angular.isUndefined(val) || val === null || (angular.isObject(val) && angular.equals({}, val))
    }
}
