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
        configFileProvider([configFile(fileId: 'ce7257b3-97e2-4486-86ee-428f65c0ff26', variable: 'MAVEN_SETTINGS')]) {
             sh "mvn -s $MAVEN_SETTINGS -U package -Dnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true"
        }
        sh "docker build -f src/main/docker/Dockerfile.native-micro -t sdenboer/pipelinepowertool-api ."
        sh "docker push sdenboer/pipelinepowertool-api"
      }
    }
  }
}