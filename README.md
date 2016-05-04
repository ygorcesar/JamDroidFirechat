**![JamDroidFirechat logo](https://yydsig.dm2303.livefilestore.com/y3mOT5vMitWd-vV8AEfIMgpop83vOlk7Y66jGnATblkUHQBni7rKwbEMQZ0qBSqRfTKgSqhRkutDeD8IZafgshbygA2c0WUq0uC-psNVFyDYwlTaL4J3ur5BxcvCWAGaIKh988WL3CD8aqvpxvge_2XJuUFXcpk0wjzsnCPPD42jpc/jamdroid.png?psid=1)JamDroidFirechat**
==================
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
 - App compilado para [Download](https://drive.google.com/file/d/0B-EMWEc1ASc7SlBHeDdTSjUtYkE/view?usp=sharing)
 - Website Android Jam Aracaju: http://android.gdgaracaju.com.br/
   
 ![Login Screen](https://xydsig.dm2303.livefilestore.com/y3m0WHoMivZCPkTe3yQ7Ul5nYZcjMvw90VSer1ATtOiEpdc1QXcu2W96Rd5hf7G8B__dmoHi3FN1CSqzQ7bGhVnkIOkExsFPzbQ-BDoXOBueF4ItNFWclfLAdXuBssNHcujSNvJP70MWtTo2Kc6RqEf8zXMaNKlXVtMCwcxcXeRJ8A/jamdroid_login.png?psid=1)![Registered Users](https://yiablq.dm2303.livefilestore.com/y3mfA1zN9KKnygG6-Gz9jTmKeDTlQOuzwhMOjipruPo5ZgGsmIJomMdE5S8DjVLsquS0WZ9UhgSjEpuQIdmgfHFI9sZE8o8vR5OUyHnIefvxCmvZtoj1uH9tTm2XToTrdKE0Vb-V_4CrUKskrdKghO2ekRg-m3TDadfa2p1pqPWxVE/jamdroid_main_app.PNG?psid=1)
![Messages](https://ycdsig.dm2303.livefilestore.com/y3mC_n7nBy1DTnKdFfHMtrwuJFgS3xF1J8c_jV8z5Q9HsNR90bBQub_-eqIEu3w7qNG1XMQZ_VOdoLvnHTNs3M8-BPvxkZH5luPxKGqC6SPu8EVL3EBRPqCJn_WijJLnwlq9DSQytBra85OZH7CAtHCwIwSHNBoeilRApj42oF1OyU/jamdroid_messages.png?psid=1)![User Profile](https://xsdsig.dm2303.livefilestore.com/y3mhRtinRsLngb6iCtzb0osU0Asfj9q3GiUfNjKJYuf16YgF0MbO1-0FGGOsBNwPFCpF5fFSId_JyucITo2RR6MrcLaHfJX0IDuv1WUn4TpVQO9iBZ9cuk3rqffsUnLpUU22AiKrl2rJ-sVtJ7xLwti-7jKhrF226dqMc35Pl9kbHo/jamdroid_user.png?psid=1)
