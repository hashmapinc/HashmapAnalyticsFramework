import AppUtilityService from "./utility.service";

export default angular.module('redTail.workflowapiservice', [])
    .factory('workflowapiservice', Workflowapiservice)
    .name;

/* @ngInject */
function Workflowapiservice($http, $q, $log, $rootScope, AppUtilityService) {
  var service = {
    saveAndScheduleWorkflow: saveAndScheduleWorkflow,
    getDetailedWorkflow: getDetailedWorkflow,
    deleteWorkflow: deleteWorkflow, 
    getDataByCacheName: getDataByCacheName
  };

  return service;

  function getDataByCacheName(workflowId, cacheName, numOfRecords) {
      return $http.get(`/workflow-executor-api/api/workflow/${workflowId}/cache/${cacheName}/${numOfRecords}`)
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
        responseData.xml = response.data.xml;
        responseData.tasks = response.data.tasks;
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
          if(AppUtilityService.isUndefinedOrNullOrEmpty(workflows))
              return;
          $log.info(workflows);
          return createWorkflowModel(workflows);
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
        if(AppUtilityService.isUndefinedOrNullOrEmpty(workflows)) return;
        let workflowIds = Object.keys(workflows);
        return $http.post('/scheduler/api/workflows', workflowIds)
            .then(success)
            .catch(fail);

        function success(response) {
            $log.info("Success");
            let retObj = {};
            angular.extend(retObj, workflows);
            angular.merge(retObj, response.data);
            $log.info(retObj);
            return retObj;
        }

        function fail(e) {
            let retObj = {};
            $log.info("FAIL");
            $log.info(workflows);
            var ob = {};
            Object.keys(workflows).forEach(_id => {

                ob[_id] = {};
                ob[_id]["id"] = _id;
                ob[_id]["cronExpression"] = "";
                ob[_id]["isRunning"] = false;
                //return ob;
            });
            let mockResponse = ob;
            $log.info(mockResponse);
            angular.extend(retObj, workflows);
            angular.merge(retObj, mockResponse);
            $log.info(retObj);
            return retObj;

            //$log.error('XHR Failed for getWorkflowById');
            //return e;
        }
    }

    function createWorkflowModel(workflows){
        if(AppUtilityService.isUndefinedOrNullOrEmpty(workflows)) {
            return null;
        }
        return Object.keys(workflows).map(id => {
            let temp = {};
            let currObj = workflows[id];
            temp.id = currObj.id;
            temp.cron = currObj.cronExpression;
            temp.name = currObj.name;
            temp.start = currObj.isRunning;
            temp.xml = currObj.xml;
            temp.tasks = currObj.tasks;
            return temp;
        });
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
}
