pipeline {
  agent {
      docker {
        image 'sdenboer/pipelinepowertool-maven-alpine-dind'
        args '-v $HOME/.m2:/root/.m2:z -v /var/run/docker.sock:/var/run/docker.sock -u root'
        reuseNode true
      }
  }
  stages {
    stage('Maven build artifact') {
      steps {
        pipelinePowerToolInitiator()
        configFileProvider([configFile(fileId: 'ce7257b3-97e2-4486-86ee-428f65c0ff26', variable: 'MAVEN_SETTINGS')]) {
             sh "mvn -s $MAVEN_SETTINGS -U package -Dnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true"
        }
        sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS} docker.io"
        sh "docker buildx use raspberry-builder"
        sh "docker buildx build --platform='linux/arm64' -f src/main/docker/Dockerfile.native-micro -t sdenboer/pipelinepowertool-api ."
        sh "docker push sdenboer/pipelinepowertool-api"
      }
    }
  }
  post {
    always {
      pipelinePowerToolElasticPublisher(userName: "elastic", password: "WYVI+2L0ZjI3n11PjTNP", hostName: "192.168.1.163", port: 9200)
    }
  }
}