pipeline {
  agent {
    docker {
      image 'hashmapinc/tempusbuild:latest'
      args '-u root -v /var/run/docker.sock:/var/run/docker.sock'
    }

  }
  stages {
    stage('Initialize') {
      steps {
        sh 'echo $USER'
        sh '''echo PATH = ${PATH}
              echo M2_HOME = ${M2_HOME}
              mvn clean
              mvn validate
           '''
        slackSend(message: 'HAF build Started for Branch: '+env.BRANCH_NAME+' for: '+env.CHANGE_AUTHOR+' on: '+env.BUILD_TAG, color: 'Green', channel: 'Tempus', botUser: true)
      }
    }
    stage('Build') {
      steps {
        sh 'mvn -Dmaven.test.failure.ignore=true install'
      }
    }
    stage('Report and Archive') {
      steps {
        junit '**/target/surefire-reports/**/*.xml,**/target/failsafe-reports/**/*.xml'
        archiveArtifacts '**/target/*.jar'
      }
    }
    stage('Publish Image') {
      when {
        branch 'master'
      }
      steps {
        withCredentials(bindings: [usernamePassword(credentialsId: 'docker_hub', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
          sh 'sudo docker login -u $USERNAME -p $PASSWORD'
        }
        sh 'mvn dockerfile:build dockerfile:push'
        sh 'mvn dockerfile:tag@latest-version dockerfile:push@latest-version'
      }
    }
    stage('Success Message') {
      steps {
        slackSend(message: 'HAF build Completed for Branch: '+env.BRANCH_NAME+' for: '+env.CHANGE_AUTHOR+' on: '+env.BUILD_TAG, channel: 'Tempus', color: 'Green')
      }
    }
  }
  post {
    always {
      sh 'chmod -R 777 .'
    }
  }
}