pipeline {
  agent any
  stages {
    stage('Maven build artifact') {
      agent {
          docker {
            image 'sdenboer/pipelinepowertool-maven-alpine-dind'
            args '-v /var/run/docker.sock:/var/run/docker.sock -u root'
            reuseNode true
          }
      }
      steps {
        configFileProvider([configFile(fileId: 'ce7257b3-97e2-4486-86ee-428f65c0ff26', variable: 'MAVEN_SETTINGS')]) {
             sh "./mvnw -s $MAVEN_SETTINGS dependency:resolve"
             sh "./mvnw package -DskipTests=true -Dnative -Dquarkus.native.container-build=true -Dquarkus.native.builder-image=quay.io/quarkus/ubi-quarkus-mandrel-builder-image:22.0.0.2-Final-java11-arm64"
        }
      }
    }
    stage('Release image') {
      agent {
        label 'agent3'
      }
      steps {
        sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS} docker.io"
        sh "docker build -f src/main/docker/Dockerfile.native-micro -t sdenboer/pipelinepowertool-api ."
        sh "docker push sdenboer/pipelinepowertool-api"
      }
    }
  }
}