pipeline {
  agent {
    docker {
      label 'yona'
      image 'unitedclassifiedsapps/gitlab-ci-android-fastlane:1.0.5'
    }
  }
  stages {
    stage('Build') {
      when {
        not { changelog '.*\\[ci skip\\].*' }
      }
      environment {
        GIT = credentials('65325e52-5ec0-46a7-a937-f81f545f3c1b')
      }
      steps {
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
    stage('Decide deploy as beta on Google Play') {
      when {
        not { changelog '.*\\[ci skip\\].*' }
      }
      steps {
        checkpoint 'Build and tests done'
        script {
          env.DEPLOY_AS_BETA = input message: 'User input required',
              submitter: 'authenticated',
              parameters: [choice(name: 'Deploy to as beta to Google Play', choices: 'no\nyes', description: 'Choose "yes" if you want to deploy this build as beta to Google Play')]
        }
      }
    }
    stage('Publish as beta') {
      when {
        environment name: 'DEPLOY_AS_BETA', value: 'yes'
      }
      steps {
        sh 'cd app && bundle install'
        withCredentials(bindings: [string(credentialsId: 'GoogleJsonKeyData', variable: 'SUPPLY_JSON_KEY_DATA')]) {
          sh 'cd app && bundle exec fastlane beta'
        }
      }
      post {
        success {
          slackSend color: 'good', channel: '#dev', message: "Android app build ${env.BUILD_NUMBER} on branch ${BRANCH_NAME} successfully published as beta on Google Play"
        }
        failure {
          slackSend color: 'bad', channel: '#dev', message: "Android app build ${env.BUILD_NUMBER} on branch ${BRANCH_NAME} failed to publish as beta on Google Play"
        }
      }
    }
  }
}
