# Membuat User di GSuite Programmatically #

Cara pakai :

1. Buat project di Google Developer Console. Tutorialnya ada [di sini](https://software.endy.muhardin.com/java/mengirim-email-gmail-api/)
2. Buat folder `${user.home}/.gsuite-api/credentials` di komputer yang akan menjalankan aplikasi. Bila ingin mengganti nama atau lokasi folder, edit file [application.properties](src/main/resources/application.properties)
3. Download `client_secret.json` dan taruh di folder `${user.home}/.gsuite-api/credentials`
4. Ikuti langkah-langkah [di sini](https://software.endy.muhardin.com/java/mengirim-email-gmail-api/) sampai mendapatkan `StoredCredential`
5. Run test file [BelajarGsuiteApplicationTests](src/test/java/com/muhardin/endy/belajar/gsuite/BelajarGsuiteApplicationTests.java)

## Referensi ##

* [Java SDK API untuk Directory](https://developers.google.com/admin-sdk/directory/v1/quickstart/java)
* [Setup Project di Google Developer Console](https://software.endy.muhardin.com/java/mengirim-email-gmail-api/)
