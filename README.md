**JamDroidFireChat**
===================
Aplicação Android, demostrando o uso do Firebase com Android em uma aplicação de chat com troca de mensagens.

Configurando Aplicação
-------------

 - Criar conta no [Firebase](https://www.firebase.com/login/)
 - Após criar conta, criar novo App Firebase
 - Habilitar no App criado, login com Google e Facebook, em seguida adicionar Google Client ID, Google Client Secret, Facebook App Id e Facebook App Secret
 - Atualizar
   [build.gradle](https://github.com/ygorcesar/JamDroidFireChat/blob/master/app/build.gradle)
   colocando url da aplicação firebase criada `buildConfigField "String",
   "FIREBASE_ROOT_URL", "https://<meuapp>.firebaseio.com"`
 - Atualizar
   [strings.xml](https://github.com/ygorcesar/JamDroidFireChat/blob/master/app/src/main/res/values/strings.xml)
   colocando seu Facebook APP ID `<string name="facebook_app_id">FACEBOOK APP ID</string>`
 - Gerar  google-services.json e colocar-lo na pasta **app** do projeto android:
   https://developers.google.com/mobile/add
 - Google Login Guide: https://developers.google.com/identity/sign-in/android/start
 - Facebook Login Guide: https://developers.facebook.com/docs/facebook-login/android

App
-------------

Aplicação Android de chat integrado com Firebase para troca de mensagens em tempo real, com chat global e direct com usuários registrados.

 - Slide apresentação [Android Jam 2 - Firebase e Android](https://drive.google.com/a/nfeinbox.com.br/file/d/0B-EMWEc1ASc7MXczcUVpTm9Pam8/view)
 - App compilado para [Download](https://drive.google.com/file/d/0B-EMWEc1ASc7MFN3MXJxcTFNT1U/view?usp=sharing)
 - Website Android Jam Aracaju: http://android.gdgaracaju.com.br/
  
![Usuários conectados](https://yiablq.dm2303.livefilestore.com/y3meQGEDAszc_BS_Kjquv0l02Hnbtx5g3R1MCBqgI4kob1lzjzLSIJqZOUkyWJnbIDqvKh0JWcYg71P1M8X8F4OQPE2FON5-oCpUuXSmJuUJiuxl2uEZOZylnooNdXNPs1yTSGl4rfeatbYP72Ps7vFkZ2-N_Lx-9tqX77tCXA-wt8/jamdroid_main_app.PNG?psid=1)

![Chat conversação](https://ysablq.dm2303.livefilestore.com/y3maBgNjcxYVSQevL2tG8oULXSFbGfuFPLcv3ckviQOV2IKlQY42Tix9mCUewJ_CAwalGQMCtE8v_hFL6sAh29cVR4DDWaDJ51TG8YWf6ij1QdgEITAd7_LBIdjjLx-9TzoX2xvneTR2hv48Cy23bv2X3QqYC9OPssDDCxi4iNoRYc/jamdroid_chat_fragment_app.PNG?psid=1)

![Perfil usuário](https://zcablq.dm2303.livefilestore.com/y3m1kq1aOD2pqDvzCnS1O3BPAnfhcZ3Ll2sw79wAjB1RrKazIWbh_kyGABzhL1xxGnXlOsApcKiPdltwpTMU3HyvzohBjkHiruHybsJiNL0V3707TtEAbesYC2UOtSmS6HoTxAHFr5bMkAs94-Cqj9MQGlHD_HyK6KhjQbMVxz_XrI/jamdroid_detail_fragment_app.PNG?psid=1)
