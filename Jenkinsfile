pipeline {
  agent {
    label 'agent3'
  }
  stages {
    stage('Maven build artifact') {
      agent {
        docker {
          image 'maven:3.9.3-eclipse-temurin-17'
          args '-v /var/run/docker.sock:/var/run/docker.sock -u root'
          reuseNode true
        }
      }
      steps {
        configFileProvider([configFile(fileId: 'ce7257b3-97e2-4486-86ee-428f65c0ff26', variable: 'MAVEN_SETTINGS')]) {
          sh "mvn -s $MAVEN_SETTINGS -U package -DskipTests=true -Dnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true"
        }
      }
    }
    stage("Release docker image") {
      steps {
        sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS} docker.io"
        sh "docker build -f src/main/docker/Dockerfile.native-micro -t sdenboer/pipelinepowertool-api ."
        sh "docker push sdenboer/pipelinepowertool-api"
      }
    }
  }
}