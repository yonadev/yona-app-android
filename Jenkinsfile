pipeline {
  agent {
    node {
      label 'android-f25'
    }
    
  }
  stages {
    stage('Build') {
      steps {
        checkout scm
        sh 'echo \"y\" | ${ANDROID_HOME}/tools/android --verbose update sdk --no-ui --all --filter build-tools-27.0.3'
        sh './gradlew app:assembleDebug'
        sh 'git tag -a build-$BUILD_NUMBER -m "Jenkins"'
        sh 'git push https://${GIT_USR}:${GIT_PSW}@github.com/yonadev/yona-server.git --tags'
        archiveArtifacts(artifacts: 'app/build/outputs/apk/**/*.apk', allowEmptyArchive: false)
      }
    }
  }
}
