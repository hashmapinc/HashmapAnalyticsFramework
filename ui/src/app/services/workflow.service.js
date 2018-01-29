export default angular.module('redTail.workflowapiservice', [])
    .factory('workflowapiservice', Workflowapiservice)
    .name;

/* @ngInject */
function Workflowapiservice($http, $q) {
  var service = {
    saveWorkflow: saveWorkflow,
    getWorkflow: getWorkflow,
    getWorkflowById: getWorkflowById,
    deleteWorkflow: deleteWorkflow
  };

  return service;

  function saveWorkflow(data) {
    return $http.put('/workflows-api/api/workflow', data)
      .then(success)
      .catch(fail);

    function success(response) {
      return response.data;
    }

    function fail(e) {
      console.log('XHR Failed for getWorkflowById');
      return e;
    }
  }

  function getWorkflow() {
    return $http.get('/workflows-api/api/workflow/')
      .then(success)
      .catch(fail);

    function success(response) {
      return response.data;
    }

    function fail(e) {
      console.log('XHR Failed for getWorkflowById');
      return e;
    }
  }

  function getWorkflowById(workflowId) {
    return $http.get('/api/workflow/'+ workflowId)
      .then(success)
      .catch(fail);

    function success(response) {
      return response.data;
    }

    function fail(e) {
      console.log('XHR Failed for getWorkflowById');
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
      console.log('XHR Failed for getWorkflowById');
      return e;
    }
  }
}
