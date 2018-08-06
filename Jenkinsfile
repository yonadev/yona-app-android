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
        sh 'echo "y" | ${ANDROID_HOME}/tools/android --verbose update sdk --no-ui --all --filter android-27,build-tools-27.0.3'
        sh './gradlew app:assembleDebug'
        sh 'git add app/${BRANCH_NAME}.version.properties'
        sh 'git commit -m "Updated versionCode for build $BUILD_NUMBER [ci skip]"'
        sh 'git push https://${GIT_USR}:${GIT_PSW}@github.com/yonadev/yona-app-android.git'
        sh 'git tag -a $BRANCH_NAME-build-$BUILD_NUMBER -m "Jenkins"'
        sh 'git push https://${GIT_USR}:${GIT_PSW}@github.com/yonadev/yona-app-android.git --tags'
        archiveArtifacts 'app/build/outputs/apk/**/*.apk'
      }
      post {
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
