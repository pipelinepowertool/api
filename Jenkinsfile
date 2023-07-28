pipeline {
  agent none
  stages {
    stage('Maven build artifact') {
      agent {
        docker {
          image 'maven:3.9.3-eclipse-temurin-17'
          args '-v $HOME/.m2:/root/.m2:z -v /var/run/docker.sock:/var/run/docker.sock -u root'
          reuseNode true
        }
      }
      steps {
        node('agent1' || 'agent2') {
          configFileProvider([configFile(fileId: 'ce7257b3-97e2-4486-86ee-428f65c0ff26', variable: 'MAVEN_SETTINGS')]) {
            sh "mvn -s $MAVEN_SETTINGS -U clean install"
          }
        }
      }
    }
    stage("Release docker image") {
      agent {
        docker {
          image 'maven:3.9.3-eclipse-temurin-17'
          reuseNode true
        }
      }
      steps {
        node('agent3') {
          configFileProvider([configFile(fileId: 'ce7257b3-97e2-4486-86ee-428f65c0ff26', variable: 'MAVEN_SETTINGS')]) {
            sh "mvn -s $MAVEN_SETTINGS -U package -DskipTests=true -Dnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true"
          }
          sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS} docker.io"
          sh "docker build -f src/main/docker/Dockerfile.native-micro -t sdenboer/pipelinepowertool-api ."
          sh "docker push sdenboer/pipelinepowertool-api"
        }
      }
    }
  }
}