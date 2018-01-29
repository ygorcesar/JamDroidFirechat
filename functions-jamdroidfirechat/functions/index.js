const functions = require('firebase-functions')
const admin = require('firebase-admin')

// https://firebase.google.com/docs/functions/write-firebase-functions

admin.initializeApp(functions.config().firebase);

exports.onNewMessage = functions.database.ref('chat/{chatId}/{messageId}')
    .onCreate((event) => {
        const message = event.data.val()

        return loadUser(message.friendEmail).then(user => {
            const chatReference = event.params.chatId
            const isChatGlobal = (chatReference == 'chatGlobal')

            const payload = {
                data: {
                    title: message.email,
                    body: message.message,
                    email: message.email,
                    friendEmail: message.friendEmail,
                    chatRef: chatReference,
                    type: message.type.toString(),
                    isGlobal: isChatGlobal.toString()
                }
            }

            console.log(user)
            console.log(payload)

            if (isChatGlobal) {
                return admin.messaging().sendToTopic(`/topics/${chatReference}`)
            } else {
                return admin.messaging().sendToDevice(user.fcmUserDeviceId, payload)
                    .then(function (response) {
                        console.log("Successfully sent message:", response);
                    })
                    .catch(function (error) {
                        console.log("Error sending message:", error);
                    })
            }
        })
        return;
    })

function loadUser(userEmail) {
    return new Promise((resolve, reject) => {
        admin.database().ref(`users/${userEmail.replace('.', ',')}`).once('value', (snapshot) => {
            resolve(snapshot.val())
        })
    })
}