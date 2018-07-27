pipeline {
  agent {
    node {
      label 'android-f25'
    }
    
  }
  stages {
    stage('Build') {
      when {
        expression {
          result = sh (script: "git log -1 | grep '.*\\[ci skip\\].*'", returnStatus: true) // Check if commit message contains skip ci label
          result != 0 // Evaluate the result
        }
        
      }
      environment {
        GIT = credentials('65325e52-5ec0-46a7-a937-f81f545f3c1b')
      }
      steps {
        sh './gradlew app:assembleRelease'
        sh './gradlew testZproductionReleaseUnitTest'
        publishHTML(allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'app/build/reports/tests/testZproductionReleaseUnitTest/', reportFiles: 'index.html', reportName: 'YonaAndroidTestReport', reportTitles: 'testReport')
      }
    }
  }
}