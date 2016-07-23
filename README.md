**![JamDroidFirechat logo](https://github.com/ygorcesar/JamDroidFirechat/blob/master/screenshots/jamdroid.png?raw=true)JamDroidFirechat**
==================
[![Google Play Link](https://github.com/ygorcesar/JamDroidFirechat/blob/master/screenshots/google_play_logo.png?raw=true)
](https://play.google.com/store/apps/details?id=com.ygorcesar.jamdroidfirechat)
Aplicação Android, demostrando o uso do Firebase com Android em uma aplicação de chat com troca de mensagens.

Configurando Aplicação
-------------

 - Criar conta no [Firebase](https://www.firebase.google.com)
 - Após criar conta, criar novo App Firebase
 - Habilitar no App criado, login com Google e Facebook, em seguida adicionar Google Client ID, Google Client Secret, Facebook App Id e Facebook App Secret
 - Atualizar [gradle.properties](https://github.com/ygorcesar/JamDroidFireChat/blob/master/gradle.properties)
- Atualizar com seu Facebook App ID: `FacebookAppIdJamdroid="1111111111111111"`
- Atualizar com seu Facebook App ID: `GoogleConsoleAppIdJamdroid="1111111111111111-111111"`
- Atualizar com sua Key do Google Web Oauth: `GoogleWebOauthId="11111111111111-1111111111111111111111.apps.googleusercontent.com"`
-  Atualizar com sua Key do Firebase Cloud Message: `FirebaseCloudMessageKey="key=1111111111111111111111111111111111"`
- Gerar  google-services.json em **Firebase Console->Select Project->Settings/Configurações** e colocar-lo na pasta **app** do projeto android.
 - Firebase Documentation and Guide: https://firebase.google.com/docs/android
 - Google Login Guide: https://developers.google.com/identity/sign-in/android/start
 - Facebook Login Guide: https://developers.facebook.com/docs/facebook-login/android

App
-------------

Aplicação Android de chat integrado com Firebase para troca de mensagens em tempo real, com chat global e direct com usuários registrados.

 - Slide apresentação [Android Jam 2 - Firebase e Android](http://pt.slideshare.net/YgorCsar/aplicaes-android-realtime-com-firebase)
 - Website Android Jam Aracaju: http://android.gdgaracaju.com.br/
   
 ![Login Screen](https://github.com/ygorcesar/JamDroidFirechat/blob/master/screenshots/login.png?raw=true)
 ![Chats](https://github.com/ygorcesar/JamDroidFirechat/blob/master/screenshots/home.png?raw=true)
![Messages](https://github.com/ygorcesar/JamDroidFirechat/blob/master/screenshots/chat.png?raw=true)![User Profile](https://github.com/ygorcesar/JamDroidFirechat/blob/master/screenshots/user_profile.png?raw=true)
![Invites](https://github.com/ygorcesar/JamDroidFirechat/blob/master/screenshots/invite.png?raw=true)![Configuration](https://github.com/ygorcesar/JamDroidFirechat/blob/master/screenshots/config.png?raw=true)
