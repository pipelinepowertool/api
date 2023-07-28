pipeline {
  agent any
  stages {
    stage('Maven build artifact') {
      agent {
          docker {
            image 'maven:3.9-eclipse-temurin-17-alpine'
            args '-v $HOME/.m2:/root/.m2:z -u root'
            reuseNode true
          }
      }
      steps {
//         pipelinePowerToolInitiator()
        configFileProvider([configFile(fileId: 'ce7257b3-97e2-4486-86ee-428f65c0ff26', variable: 'MAVEN_SETTINGS')]) {
             sh "mvn -s $MAVEN_SETTINGS -U package -Dnative -Dquarkus.container-image.push=true -Dquarkus.container-image.registry=registry.hub.docker.com -Dquarkus.jib.base-registry-username=${DOCKER_USER} -Dquarkus.jib.base-registry-password=${DOCKER_PASS}"
        }
      }
    }
  }
//   post {
//     always {
//       pipelinePowerToolElasticPublisher(userName: "elastic", password: "WYVI+2L0ZjI3n11PjTNP", hostName: "192.168.1.163", port: 9200)
//     }
//   }
}