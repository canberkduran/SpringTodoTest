pipeline {
    agent any
    
    tools {
        // Jenkins Tools menüsünde Maven'a hangi ismi verdiysen (M3 demiştik) onu yaz
        maven 'M3' 
    }

    triggers {
        // Her dakika kontrol eder
        pollSCM('H * * * *')
    }

    stages {
        stage('1. Kodu Çek') {
            steps {
                // 'scm' ifadesi Jenkins arayüzünde belirttiğin repo ayarlarını otomatik kullanır
                checkout scm
            }
        }

        stage('2. JUnit Erişim Testi') {
            steps {
                echo "Uygulama testleri ve example.com kontrolü yapılıyor..."
                // Java koduna eklediğimiz JUnit testini çalıştırır
                sh 'mvn test -Dtest=TodoServiceTest'
            }
        }

        stage('3. Deploy') {
            steps {
                echo "Erişim testi başarılı! Deploy adımı çalışıyor..."
                // Buraya uygulamanı ayağa kaldıracak komutları (örn: docker-compose up) ekleyebilirsin
            }
        }
    }

    post {
        always {
            // Test sonuçlarını Jenkins üzerinde raporlar
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
