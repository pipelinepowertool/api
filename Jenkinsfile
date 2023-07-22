pipeline {
  agent any
  stages {
    stage('Maven build artifact') {
      steps {
        sh "./mvnw package -Dnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true"
        withAWS(region:'eu-north-1',credentials:'jenkins-s3') {
          sh 'echo "Uploading content with AWS creds"'
          s3Upload(file:'./target/*-runner', bucket:'energy-reader', acl: 'PublicRead')
        }
      }
    }
  }
}