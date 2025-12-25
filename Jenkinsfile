pipeline {
    agent any

    tools {
        maven 'M3' 
    }

    triggers {
        // GitHub'ı kontrol etmeye devam eder
        pollSCM('H * * * *')
    }

    stages {
        stage('1. Kodu Çek') {
            steps {
                // SCM üzerinden hangi branch tetiklendiyse onu çeker
                checkout scm
            }
        }

        stage('2. Erişim ve Servis Testleri') {
            when {
                // SADECE 'main' branch'indeyse bu stage çalışır
                branch 'main'
            }
            steps {
                echo 'Main branch algılandı, testler koşturuluyor...'
                sh 'mvn clean test -Dtest=TodoServiceTest'
            }
        }

        stage('3. Test Branch Bilgilendirme') {
            when {
                // SADECE 'test' branch'indeyse bu stage çalışır
                branch 'test'
            }
            steps {
                echo 'Şu an TEST branchindesiniz. Pipeline bu branch için pasif moddadır.'
            }
        }
    }

    post {
        always {
            // Eğer main branch'te test koştuysa raporu yayınlar
            script {
                if (env.BRANCH_NAME == 'main') {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
    }
}
