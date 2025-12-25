pipeline {
    agent any
    tools { maven 'M3' }
    triggers { pollSCM('H * * * *') }

    stages {
        stage('1. Kodu Çek') {
            steps {
                checkout scm
            }
        }

        stage('2. Erişim Kontrolü (Sadece Main)') {
            steps {
                script {
                    // Jenkins'in yakaladığı branch ismini logda görelim (Hata ayıklamak için)
                    echo "Mevcut Branch: ${env.GIT_BRANCH}"
                    
                    // Branch ismi 'main' içeriyorsa testi çalıştır
                    if (env.GIT_BRANCH.contains('main')) {
                        echo "MAIN branch algılandı. Example.com testi yapılıyor..."
                        sh 'mvn clean test -Dtest=TodoServiceTest'
                    } else {
                        // Diğer tüm branchlerde (test dahil) burası çalışır
                        echo "Bu branch (${env.GIT_BRANCH}) TEST veya başka bir branch. Testler ATLANDI."
                    }
                }
            }
        }
    }

    post {
        always {
            // Sadece test koştuysa raporu yayınla (Klasör yoksa hata vermemesi için script içinde)
            script {
                def testFolder = fileNameExists 'target/surefire-reports'
                if (testFolder) {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
    }
}

// Yardımcı fonksiyon: Klasör var mı kontrolü
def fileNameExists(String file) {
    return filePathExists(file)
}
def filePathExists(String file) {
    return sh(script: "test -d ${file}", returnStatus: true) == 0
}
