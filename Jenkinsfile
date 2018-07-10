pipeline {
  agent {
    node {
      label 'android-f25'
    }
    
  }
  stages {
    stage('Build') {
      environment {
        GIT = credentials('65325e52-5ec0-46a7-a937-f81f545f3c1b')
      }
      steps {
        checkout scm
        sh 'echo \"y\" | ${ANDROID_HOME}/tools/android --verbose update sdk --no-ui --all --filter android-27,build-tools-27.0.3'
        sh './gradlew app:assembleDebug'
        sh 'git status'
        sh 'git tag -a $BRANCH_NAME-build-$BUILD_NUMBER -m "Jenkins"'
        sh 'git push https://${GIT_USR}:${GIT_PSW}@github.com/yonadev/yona-app-android.git --tags'
        archiveArtifacts(artifacts: 'app/build/outputs/apk/**/*.apk', allowEmptyArchive: false)
      }
    }
  }
}
