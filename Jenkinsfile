pipeline {
    agent any

    tools {
        // Jenkins > Tools kısmındaki Maven ismin (M3 demiştik)
        maven 'M3' 
    }

    triggers {
        // GitHub'daki her iki branch'i de kontrol eder
        pollSCM('H * * * *')
    }

    stages {
        stage('1. Kodu Çek') {
            steps {
                // Hangi branch'ten tetiklendiyse o kodu indirir
                checkout scm
            }
        }

        stage('2. Erişim Kontrolü (Sadece Main)') {
            when {
                // Sadece branch adı 'main' ise bu stage çalışır
                // Not: Bazı sistemlerde 'origin/main' gerekebilir
                branch 'main'
            }
            steps {
                echo "Şu an MAIN branch'indesiniz. Erişim testi başlatılıyor..."
                // Senin yazdığın JUnit testini çalıştırır
                sh 'mvn clean test -Dtest=TodoServiceTest'
            }
        }

        stage('3. Pasif Mod (Sadece Test)') {
            when {
                // Sadece branch adı 'test' ise bu stage çalışır
                branch 'test'
            }
            steps {
                echo "------------------------------------------------"
                echo "DİKKAT: TEST branch'i algılandı."
                echo "Bu branch için pipeline PASİF moddadır, test yapılmadı."
                echo "------------------------------------------------"
            }
        }
    }

    post {
        always {
            // Main branch'te test koştuysa raporu Jenkins'e yansıtır
            script {
                if (env.GIT_BRANCH == 'origin/main' || env.GIT_BRANCH == 'main') {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
    }
}
