pipeline {
  agent {
    label 'sbt-slave'
  }

  environment {
    // Ensure that build scripts recognise the environment they are running within
    CI = 'jenkins'
  }

  stages {
    stage('GenerateTemplate') {
      steps {
        ansiColor('xterm') {
          script {
            sh "mkdir template.g8; cp -fr src template.g8/"
            sh "sbt new file://./template.g8 --name=akkarepo --project_description=ci-test --organisation_domain=net --organisation=cakesolutions"
          }
        }
      }
    }

    stage('Environment') {
      steps {
        ansiColor('xterm') {
          dir("akkarepo") {
            script {
              sh "sbt checkExternalBuildTools"
            }
          }
        }
      }
    }

    stage('Compile') {
      steps {
        ansiColor('xterm') {
          dir("akkarepo") {
            script {
              sh "sbt clean compile test:compile it:compile doc"
            }
          }
        }
      }
    }

    stage('Verification') {
      steps {
        ansiColor('xterm') {
          dir("akkarepo") {
            script {
              // Since copyright headers are not set up for test projects, we omit headerCheck, test:headerCheck and it:headerCheck here
              sh "sbt scalastyle test:scalastyle it:scalastyle sbt:scalafmt::test scalafmt::test test:scalafmt::test it:scalafmt::test"
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
              // We intentionally avoid any coverage based checks
              sh "sbt test"
            }
          }
        }
      }
    }

    stage('IntegrationTest') {
      steps {
        ansiColor('xterm') {
          dir("akkarepo") {
            script {
              try {
                sh "sbt dockerComposeUp"
                sh "sbt it:test"
              } finally {
                sh "sbt dockerComposeDown dockerRemove"
              }
            }
          }
        }
      }
    }

    stage('Publish') {
      steps {
        ansiColor('xterm') {
          dir("akkarepo") {
            sh "sbt createRelease"
          }
        }
      }
    }
  }
}
