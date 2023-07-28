pipeline {

  stages {
    stage('Maven build artifact') {
      agent {
          docker {
            image 'sdenboer/pipelinepowertool-maven-alpine-dind'
            args '-v $HOME/.m2:/root/.m2:z -v /var/run/docker.sock:/var/run/docker.sock -u root'
            reuseNode true
          }
      }
      steps {
        pipelinePowerToolInitiator()
        configFileProvider([configFile(fileId: 'ce7257b3-97e2-4486-86ee-428f65c0ff26', variable: 'MAVEN_SETTINGS')]) {
             sh "mvn -s $MAVEN_SETTINGS -U package -Dnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true"
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