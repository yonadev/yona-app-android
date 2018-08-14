pipeline {
  agent none
  stages {
    stage('Build') {
      agent {
        docker {
          label 'yona'
          image 'unitedclassifiedsapps/gitlab-ci-android-fastlane'
        }
      }
      when {
        not { changelog '.*\\[ci skip\\].*' }
      }
      environment {
        GIT = credentials('65325e52-5ec0-46a7-a937-f81f545f3c1b')
      }
      steps {
        sh 'echo "y" | ${ANDROID_HOME}/tools/android --verbose update sdk --no-ui --all --filter android-27,build-tools-27.0.3'
        sh './gradlew clean testZacceptanceDebugUnitTest'
        sh './gradlew app:assembleDebug'
        sh 'git add app/version.properties'
        sh 'git commit -m "Updated versionCode for build $BUILD_NUMBER [ci skip]"'
        sh 'git push https://${GIT_USR}:${GIT_PSW}@github.com/yonadev/yona-app-android.git'
        sh 'git tag -a $BRANCH_NAME-build-$BUILD_NUMBER -m "Jenkins"'
        sh 'git push https://${GIT_USR}:${GIT_PSW}@github.com/yonadev/yona-app-android.git --tags'
        archiveArtifacts 'app/build/outputs/apk/**/*.apk'
      }
      post {
        always {
          junit '**/build/test-results/*/*.xml'
        }  
        success {
          slackSend color: 'good', channel: '#dev', message: "Android app build ${env.BUILD_NUMBER} on branch ${BRANCH_NAME} succeeded"
        }
        failure {
          slackSend color: 'bad', channel: '#dev', message: "Android app build ${env.BUILD_NUMBER} on branch ${BRANCH_NAME} failed"
        }
      }
    }
  }
}
