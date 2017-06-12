pipeline {
  agent {
    label 'sbt-slave'
  }
  stages {
    stage('Generate template') {
      steps {
        ansiColor('xterm') {
          script {
            sh "mkdir template.g8; mv src template.g8/"
            sh "sbt new file://./template.g8 --name=akkarepo --project_description=ci-test --organisation_domain=net --organisation=cakesolutions"
          }
        }
      }
    }
    stage('Compile') {
      steps {
        ansiColor('xterm') {
          dir("akkarepo") {
            script {
              sh "sbt compile"
            }
          }
        }
      }
    }
    stage('Test') {
      steps {
        ansiColor('xterm') {
          dir("akkarepo") {
            script {
              sh "sbt coverage test coverageReport"
            }
          }
        }
      }
    }
    stage('Integration test') {
      steps {
        ansiColor('xterm') {
          dir("akkarepo") {
            script {
              try {
                sh "sbt dockerComposeUp"
                def dockerip = sh(returnStdout: true, script:  $/wget http://169.254.169.254/latest/meta-data/local-ipv4 -qO-/$).trim()
                withEnv(["APP_HOST=$dockerip"]) {
                  sh "sbt it:test"
                }
              } finally {
                sh "sbt dockerComposeDown dockerRemove"
              }
            }
          }
        }
      }
    }
  }
}
