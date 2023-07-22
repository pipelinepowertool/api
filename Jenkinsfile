pipeline {
  agent {
      docker {
        image 'maven:3.9-eclipse-temurin-17-alpine'
        args '-v $HOME/.m2:/root/.m2:z -u root'
      }
  }
  stages {
    stage('Maven build artifact') {
      steps {
        sh "mvn package -Dnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true"
        withAWS(region:'eu-north-1',credentials:'jenkins-s3') {
          sh 'echo "Uploading content with AWS creds"'
          s3Upload(file:'./target/*-runner', bucket:'energy-reader', acl: 'PublicRead')
        }
      }
    }
  }
}