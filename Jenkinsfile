pipeline {
    options { 
        buildDiscarder(logRotator(numToKeepStr: '4', artifactNumToKeepStr: '5'))
        disableConcurrentBuilds() 
    }
    agent any
    tools {
        maven 'Maven 3.5.2'
        jdk 'JDK 1.8'
    }

    stages {
        stage('Fetch SCM') {
            steps {
                checkout scm
            }
        }

        stage('Unit Tests') {
            steps {
                withMaven(
                    maven: 'Maven 3.5.2',
                    mavenSettingsConfig: 'e924d227-1005-4fcb-92ef-3d382c066f09'
                ) {
                    sh 'mvn clean install -DskipTests'
                    sh 'mvn test'
                }
            }
            post {
                success {
                    junit '**/target/surefire-reports/**/*.xml'
                    step( [ $class: 'JacocoPublisher' ] )
                }
            }
        }
        stage('Build and push image') {
            agent { node { label 'docker' } }
            steps {
                script {
                    dockerBuild 'hub.ops.ikats.org/datamodel'
                }
            }
        }
    }
}
