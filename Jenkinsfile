pipeline {
    agent any

    tools {
        // Jenkins > Tools kısmında tanımladığın Maven ismi
        maven 'M3' 
    }

    triggers {
        // Her 1 dakikada bir GitHub'ı kontrol et (Poll SCM)
        pollSCM('H * * * *')
    }

    stages {
        stage('1. Kaynak Kodu Çek') {
            steps {
                echo 'GitHub üzerinden kodlar indiriliyor...'
                // Kendi GitHub repo URL'ini buraya yaz. 
                // Branch adının 'main' olduğundan emin ol, değilse 'master' yap.
                git branch: 'main', url: 'https://github.com/canberkduran/SpringTodoTest.git'
            }
        }

        stage('2. JUnit Testleri ve Erişim Kontrolü') {
            steps {
                echo 'JUnit testi koşturuluyor (example.com kontrolü dahil)...'
                // 'clean' ile temizlik yapıyoruz, 'test' ile sadece ilgili sınıfı çalıştırıyoruz
                // Eğer Java kodundaki example.com testi 200 dönmezse burası FAIL verir.
                sh 'mvn clean test -Dtest=TodoServiceTest'
            }
        }

        stage('3. Deploy (Dağıtım)') {
            steps {
                // Sadece 2. stage BAŞARILI olursa buraya geçer.
                echo 'Erişim testi başarılı! Uygulama deploy ediliyor...'
                // Buraya uygulamanı ayağa kaldırma komutlarını yazabilirsin:
                // sh 'docker-compose up -d --build'
            }
        }
    }

    post {
        always {
            // Test sonuçlarını Jenkins panelinde görsel grafik olarak saklar
            junit '**/target/surefire-reports/*.xml'
            echo 'İşlem tamamlandı.'
        }
        success {
            echo 'Pipeline başarıyla bitti! 🎉'
        }
        failure {
            echo 'Pipeline başarısız oldu. Lütfen test sonuçlarını kontrol et! ❌'
        }
    }
}
