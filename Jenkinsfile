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
        withCredentials(bindings: [string(credentialsId: 'AndroidKeystorePassword', variable: 'YONA_KEYSTORE_PASSWORD'),
            string(credentialsId: 'AndroidKeyPassword', variable: 'YONA_KEY_PASSWORD'),
            file(credentialsId: 'AndroidKeystore', variable: 'YONA_KEYSTORE_PATH')]) {
          sh './gradlew clean testDevelopmentDebugUnitTest app:assemble'
        }
        sh 'find . -name *.apk -print'
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
          slackSend color: 'good', channel: '#dev', message: "Android app build ${env.BUILD_NUMBER} on branch ${BRANCH_NAME} completed successfully"
        }
        failure {
          slackSend color: 'bad', channel: '#dev', message: "Android app build ${env.BUILD_NUMBER} on branch ${BRANCH_NAME} failed"
        }
      }
    }
    stage('Upload to Google Play') {
      when {
        allOf {
          not { changelog '.*\\[ci skip\\].*' }
          anyOf {
            branch 'develop'
            branch 'master'
            branch 'appdev-1152-fastlane-deployment'
          }
        }
      }
      steps {
        sh 'cd app && bundle install'
        withCredentials(bindings: [string(credentialsId: 'GoogleJsonKeyData', variable: 'SUPPLY_JSON_KEY_DATA')]) {
          sh 'cd app && bundle exec fastlane --verbose alpha'
        }
      }
      post {
        success {
          slackSend color: 'good', channel: '#dev', message: "Android app build ${env.BUILD_NUMBER} on branch ${BRANCH_NAME} successfully uploaded to Google Play"
        }
        failure {
          slackSend color: 'bad', channel: '#dev', message: "Android app build ${env.BUILD_NUMBER} on branch ${BRANCH_NAME} failed to upload to Google Play"
        }
      }
    }
    stage('Decide deploy as beta on Google Play') {
      when {
        allOf {
          not { changelog '.*\\[ci skip\\].*' }
          anyOf {
            branch 'develop'
            branch 'master'
            branch 'appdev-1152-fastlane-deployment'
          }
        }
      }
      steps {
        checkpoint 'APK uploaded to Google Play'
        script {
          env.DEPLOY_AS_BETA = input message: 'User input required',
              submitter: 'authenticated',
              parameters: [choice(name: 'Promote to beta on Google Play', choices: 'no\nyes', description: 'Choose "yes" if you want to promote this to beta on Google Play')]
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
          sh 'cd app && bundle exec fastlane --verbose beta'
        }
      }
      post {
        success {
          slackSend color: 'good', channel: '#dev', message: "Android app build ${env.BUILD_NUMBER} on branch ${BRANCH_NAME} successfully promoted to beta on Google Play"
        }
        failure {
          slackSend color: 'bad', channel: '#dev', message: "Android app build ${env.BUILD_NUMBER} on branch ${BRANCH_NAME} failed to promote to beta on Google Play"
        }
      }
    }
  }
}
